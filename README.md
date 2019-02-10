# seckill
做一个秒杀系统练手
## 介绍
从[慕课网](https://www.imooc.com/u/2145618/courses?sort=publish)教程学习并实践。
## 使用
由于项目是基于springboot框架，也就是maven风格，所以导入项目直接打开pom.xml即可。并通过入口类SpringbootApplication启动(内置tomcat)。
## 笔记
* 倒计时功能用jquery.countdown.min.js插件
* cookie用jquery.cookie.min.js插件,web层用@CookieValue获取
* 关于thymeleaf中日期格式化：`<td th:text="${#dates.format(item.startTime, 'yyyy-MM-dd HH:mm:ss')}">`
* dto(数据传输对象)封装了返回结果Result，暴露秒杀地址Exposer，秒杀结果SeckillExecution

## 截图
![avatar](https://github.com/GaryLeeeee/seckill/blob/master/img/%E5%80%92%E8%AE%A1%E6%97%B6.png)<br>
![avatar](https://github.com/GaryLeeeee/seckill/blob/master/img/%E5%88%97%E8%A1%A8%E9%A1%B5.png)<br>
![avatar](https://github.com/GaryLeeeee/seckill/blob/master/img/%E6%B3%A8%E5%86%8C%E9%A1%B5.png)<br>
![avatar](https://github.com/GaryLeeeee/seckill/blob/master/img/%E7%A7%92%E6%9D%80%E7%BB%93%E6%9E%9C.png)<br>

## 高并发优化方案
* 由于用户会不断刷新页面，就会不断地去mysql中查询，所以可以加入缓存redis
```Java
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
 ```
* 调整事务执行顺序(简单优化)
 
|优化前|优化后|
|:-------------:|:-------------:|
|update 减库存rowLock|insert 购买明细|
|insert 购买明细|update 减库存rowLock|
|commit/rollback freeLock| commit/rollback freeLock|

`因为在一个事务中，update->insert->commit/rollback过程中都要伴随网络延迟和gc，会影响效率，mysql本身效率并不低。
而update会占据行锁，如果放在前面执行会加大执行时间，放在后面后等待时间就可以由2倍变成1倍了`

* 事务SQL在MySQL端执行(深度优化) 

  详见[秒杀操作-并发操作-2](https://www.imooc.com/video/11825)
