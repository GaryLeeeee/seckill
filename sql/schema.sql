CREATE DATABASE seckill;
use seckill;
-- 创建秒杀库存表
CREATE TABLE seckill(
  `seckill_id`  bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` VARCHAR (120) NOT NULL COMMENT '商品名称',
  `number` int NOT NULL COMMENT '库存数量',
  `start_time` TIMESTAMP NOT  NULL  COMMENT'秒杀开启时间',
  `end_time` TIMESTAMP NOT  NULL  COMMENT'秒杀结束时间',  `create_time` TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT'创建时间',
PRIMARY KEY (seckill_id),
KEY idx_start_time(start_time),
KEY idx_end_time(end_time),
KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT  CHARSET=utf8 COMMENT='秒杀库存表';
/*
CREATE TABLE seckill(
'seckill_id' bigint NOT NULL AUTO_INCREMENT ,
'name' VARCHAR (120) NOT NULL ,
'number' int NOT NULL COMMENT ,
'start_time' TIMESTAMP NOT  NULL  ,
'end_time' TIMESTAMP NOT  NULL  ,
'start_time' TIMESTAMP NOT DEFAULT CURRENT_TIMESTAMP NULL  ,
PRIMARY KEY (seckill_id),
KEY idx_start_time(start_time),
KEY idx_end_time(end_time),
KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT  CHARSET=utf8 ;
 */


--初始化数据
insert INTO
seckill(name ,number ,start_time,end_time)
VALUES
('1000元秒杀iphone6',100,'2019-11-01 00:00:00','2019-11-02 00:00:00'),
('500元秒杀ipid',100,'2019-11-01 00:00:00','2019-11-02 00:00:00'),
('300元秒杀小米',100,'2019-11-01 00:00:00','2019-11-02 00:00:00'),
('100元秒杀红米',100,'2019-11-01 00:00:00','2019-11-02 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关的信息
create table  success_killed(
`seckill_id` bigint not null COMMENT '商品库存id',
`user_phone` bigint not null COMMENT '用户手机号码',
`state` tinyint not null DEFAULT  -1 COMMENT '状态：-1无效， 0：成功， 1：已付款',
`create_time` TIMESTAMP not null COMMENT '创建时间',
PRIMARY KEY (seckill_id,user_phone),/* 联合组件*/
KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT  CHARSET=utf8 COMMENT='秒杀成功明细表';