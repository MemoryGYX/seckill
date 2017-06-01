package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExcution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by GYX on 2017/5/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;
    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckills = seckillService.getSeckillList();
        log.info("list = {}",seckills);
    }

    @Test
    public void getSeckillById() throws Exception {
        Seckill seckill =  seckillService.getSeckillById(1000);
        log.info("seckill = {}" , seckill);
    }

    @Test
    public void excuteSeckillLogic() throws Exception {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        log.info("exposer = {}" , exposer);
        if(exposer.isExposed()){
            long userphone = 18510240132l;
            try{
                SeckillExcution excution = seckillService.excuteSeckill(id, userphone, exposer.getMd5());
                log.info("Excution = {}" ,excution);
            }catch (SeckillCloseException e){
                log.info("ExcutionFail = {}","SeckillCloseException");
            }catch (RepeatKillException e){
                log.info("ExcutionFail = {}","RepeatKillException");
            }
        }
        //exposer = Exposer{exposed=true, md5='7697ce8f3b1f379eb0792bf88b22baee', seckillId=1000, now=0, start=0, end=0}
    }

    @Test
    public void excuteSeckill() throws Exception {
        long id = 1000;
        long userphone = 18510240132l;
        String md5 = "7697ce8f3b1f379eb0792bf88b22baee";
        SeckillExcution excution = seckillService.excuteSeckill(id, userphone, md5);
        log.info("Excution = {}" ,excution);
    }

}