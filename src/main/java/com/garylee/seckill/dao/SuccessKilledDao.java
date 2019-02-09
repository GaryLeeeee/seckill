package com.garylee.seckill.dao;

import com.garylee.seckill.domain.SuccessKilled;
import org.apache.ibatis.annotations.Param;

public interface SuccessKilledDao {
    //插入购买明细，可过滤重复
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
    //根据id查询SuccessKilled并携带秒杀产品对象实体
    //这里应该是List
    SuccessKilled queryBySeckillId(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);
}
