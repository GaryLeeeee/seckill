package com.garylee.seckill.dao;

import com.garylee.seckill.domain.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by GaryLee on 2019-01-31 23:49.
 */
public interface SeckillDao {
    //减库存
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);
    //查询秒杀商品
    Seckill queryById(long seckillId);
    //根据偏移量查询商品列表(从第offset个开始，查询limit个）
    List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit);
}
