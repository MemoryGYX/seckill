package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by GYX on 2017/5/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;
    @Test
    public void insertSuccessKilled() throws Exception {
       int insertCount =  successKilledDao.insertSuccessKilled(1001L,18500240132L);
       System.out.print("insertCount = " + insertCount);
    }

    @Test
    public void quertByIdWithSeckill() throws Exception {
        SuccessKilled killed = successKilledDao.quertByIdWithSeckill(1001L,18500240132L);
        System.out.println(killed);
        System.out.println(killed.getSeckill());

    }

}