package com.garylee.seckill.service.impl;

import com.garylee.seckill.dao.SeckillDao;
import com.garylee.seckill.domain.Seckill;
import com.garylee.seckill.dto.Exposer;
import com.garylee.seckill.dto.SeckillExecution;
import com.garylee.seckill.exception.RepeatKillException;
import com.garylee.seckill.exception.SeckillCloseException;
import com.garylee.seckill.service.SeckillService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SeckillServiceImplTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SeckillService seckillService;
    @Autowired
    SeckillDao seckillDao;
    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckills = seckillService.getSeckillList();
        logger.info("list={}",seckills);
    }

    @Test
    public void getById() throws Exception {
        long id = 1001;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);
    }
//exposer=Exposer{exposed=true,
// md5='6b3ff0d66a83a5eacae149a8e4d1a011',
// seckillId=1001,
// now=0, start=0, end=0}
    @Test
    public void executeSeckill() throws Exception {
        long id = 1001L;
        long phone = 12345678910L;
        String md5 = "6b3ff0d66a83a5eacae149a8e4d1a011";
        SeckillExecution seckillExecution = seckillService.executeSeckill(id,phone,md5);
        logger.info("result={}",seckillExecution);
    }

    @Test
    public void test(){
        seckillDao.reduceNumber(1001,new Date());
    }

    @Test
    public void seckill(){
        int id = 1001;
        //判断是否开启
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer={}",exposer);
            long phone = 12564795636L;
            String md5 = exposer.getMd5();
            try {
                //秒杀开始
                //秒杀结果
                SeckillExecution execution = seckillService.executeSeckill(id,phone,md5);
                logger.info("result={}",execution);
            }catch (RepeatKillException e1){
                logger.error("重复秒杀:"+e1.getMessage());
            }catch (SeckillCloseException e2){
                logger.error("秒杀关闭:"+e2.getMessage());
            }
        }else {
            //秒杀未开启
            logger.warn("exposer={}",exposer);
        }
    }

}