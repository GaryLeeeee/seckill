package com.garylee.seckill.service;

import com.garylee.seckill.domain.Seckill;
import com.garylee.seckill.dto.Exposer;
import com.garylee.seckill.dto.SeckillExecution;
import com.garylee.seckill.exception.RepeatKillException;
import com.garylee.seckill.exception.SeckillCloseException;
import com.garylee.seckill.exception.SeckillException;

import java.util.List;

/**
 * Created by GaryLee on 2019-02-01 12:07.
 */
public interface SeckillService {
    //查询所有查询记录
    List<Seckill> getSeckillList();
    //查询单个查询记录
    Seckill getById(long seckillId);
    //秒杀开启时输出秒杀接口地址
    //否则输出系统时间和秒杀时间
    Exposer exportSeckillUrl(long seckillId);
    //执行秒杀操作
    SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
    throws SeckillException,RepeatKillException,SeckillCloseException;
}
