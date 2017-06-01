package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * Created by Administrator on 2017/5/23.
 */
public interface SuccessKilledDao {

    /**
     * 插入购买明细，可过滤重复
     * @param seckillId
     * @param userPhone
     * @return 更新的记录行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone")long userPhone);

    /**
     * 根据Id查询successKilled并携带秒杀商品实体
     * @param seckillId
     * @return
     */
    SuccessKilled quertByIdWithSeckill(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);

}
