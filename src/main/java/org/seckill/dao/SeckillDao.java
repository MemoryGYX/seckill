package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/23.
 */
public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("seckillId")long seckillId, @Param("killTime")Date killTime);

    /**
     * 根据Id查询实体
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    List<Seckill> queryAll(@Param("offset")int offset, @Param("limit") int limit);
}
