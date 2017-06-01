package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by GYX on 2017/5/25.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;

    public final String slat = "adhshADAHDUHAIUDGYI1231824y91y89!&#(!@*&#(!#";//用于混淆MD5
    /**
     * 查询所有秒杀单
     *
     * @return
     */
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,10);
    }

    /**
     * 查询单个秒杀记录
     *
     * @param seckillId
     * @return
     */
    public Seckill getSeckillById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    /**
     * 秒杀开启时输出秒杀地址，
     * 否则输出系统时间和秒杀时间
     *
     * @param seckillId
     */
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if(seckill == null){
            return new Exposer(false,seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }

        String md5 = getMD5(seckillId);//转化特定字符串，不可逆
        return new Exposer(true,md5,seckillId);
    }

    /**
     * 加密
     * @param seckillId
     * @return
     */
    private String getMD5(long seckillId){
        String base = seckillId+"/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 执行秒杀操作
     * 使用注解控制事务的优点
     * 1 明确标注事务方法的编程风格
     * 2 保证事务方法的执行时间尽可能短 不要穿插其他网络操作，如缓存、http 或者剥离到事务方法外
     * 3 不是所有的方法都需要事务 比如只有一条修改操作，或只读操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    @Transactional
    public SeckillExcution excuteSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(StringUtils.isEmpty(md5) || !md5.equals(getMD5(seckillId))){
            throw  new SeckillException("seckill data rewrite");
        }
        try {
            //执行秒杀业务逻辑，减库存，记录秒杀行为
            int updateCount = seckillDao.reduceNumber(seckillId,new Date());
            if(updateCount <= 0 ){
                throw new SeckillCloseException("seckill is closed");
            }else{
                int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
                if(insertCount <= 0){
                    throw new RepeatKillException("seckill is repeat");
                }else{
                    SuccessKilled successKilled = successKilledDao.quertByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExcution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                }
            }
        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new SeckillException("seckill inner error"+e.getMessage());
        }
    }


}
