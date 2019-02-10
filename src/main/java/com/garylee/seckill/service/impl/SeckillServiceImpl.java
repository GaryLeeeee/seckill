package com.garylee.seckill.service.impl;

import com.garylee.seckill.dao.SeckillDao;
import com.garylee.seckill.dao.SuccessKilledDao;
import com.garylee.seckill.domain.Seckill;
import com.garylee.seckill.domain.SuccessKilled;
import com.garylee.seckill.dto.Exposer;
import com.garylee.seckill.dto.SeckillExecution;
import com.garylee.seckill.enums.SeckillStateEnum;
import com.garylee.seckill.exception.RepeatKillException;
import com.garylee.seckill.exception.SeckillCloseException;
import com.garylee.seckill.exception.SeckillException;
import com.garylee.seckill.service.SeckillService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by GaryLee on 2019-02-01 12:07.
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SeckillDao seckillDao;
    @Autowired
    SuccessKilledDao successKilledDao;
    @Resource
    RedisTemplate<String,Seckill> redisTemplate;

    private final String salt = "garylee";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    //输出秒杀的显示(如剩余时间，是否开启)
    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //高并发优化:缓存优化
        //1.访问redis
        String key ="seckill-"+seckillId;
        Seckill seckill = redisTemplate.opsForValue().get(key);
        if(seckill == null){
            //redis为空
            //2.访问数据库
            seckill = seckillDao.queryById(seckillId);
            if(seckill ==null) {
                return new Exposer(false, seckillId);
            }
            else {
                //如果数据库有，redis没有，则写入redis
                redisTemplate.opsForValue().set(key, seckill);
            }
        }
        //获取秒杀开始和结束时间
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //获取系统当前时间
        Date nowTime = new Date();
        //判断是否为"秒杀未开始"或"秒杀已结束"
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime())
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());

        //MD5 加密
        String md5 = getMD5(seckillId);//todo
        return new Exposer(true, md5, seckillId);
    }

    @Override
    @Transactional
    /**
     * 使用注释控制事务方法的优点:
     * 1.约定大于配置
     * 2.不是所有的方法都需要事务,如只有一条修改操作、只读操作不需要事务控制
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId)))
            throw new SeckillException("非法请求!");
        //如果可以秒杀，则执行逻辑
        //秒杀逻辑:减库存 + 记录购买行为
        Date nowTime = new Date();
        int updateCount = seckillDao.reduceNumber(seckillId, nowTime);

        try {
            if (updateCount <= 0) {
                //没有更新成功(库存不足/时间不对)
                throw new SeckillCloseException("秒杀已结束!");
            } else {
                //记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    //没插入成功(已存在该数据->重复秒杀)
                    throw new RepeatKillException("重复秒杀!");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryBySeckillId(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e) {
            logger.error(e.getMessage(),e);
            //所有编译器异常 转化为运行期异常
            throw new SeckillException("系统异常!");
        }
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
