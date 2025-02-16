sale_random_data  两个主题
person_random_data


1.命令备份
/opt/kafka/bin/kafka-console-producer.sh --topic sale_random_data --broker-list hadoop1:9092,hadoop2:9092,hadoop3:9092
/opt/kafka/bin/kafka-console-producer.sh --topic person_random_data --broker-list hadoop1:9092,hadoop2:9092,hadoop3:9092


2.试验结论
(1) timeservice触发试验

    1) 只有connect上了才会改变流水线
    2) timeservice触发条件是流水线发生变化,并且timeservice的超时时间小于流水线时间。双流中,貌似需要两个流的流水线都变化才会触发timeservice。
    这实用中就需要注意了,如果乱序十分严重。某条数据拉低高了流水线,并且其后的数据均小于流水线,那么timeservice就不会触发。内存中的数据就会越来越多。
    3) 按目前UsingCoGroupFunction的实现,如果同一时间A流有两条数据进来,那么timeservice只会触发一次,并且还不能笛卡尔积,join的结果为最新的一条数据。
    但是我看通过修改也能实现笛卡尔积。


下一步了解一下ProcessWindowFunction

3.xiaofei_kuanb的mysql建表语句
drop table `random_data.xiaofei_kuanb2`;
CREATE TABLE `random_data.xiaofei_kuanb2`(
  `name` string, 
  `address` string, 
  `restaurant` string, 
  `food` string, 
  `price` double, 
  `count` int, 
  `gmv` double, 
  `email` string, 
  `phone` string, 
  `job` string, 
  `company` string, 
  `tm` double)
PARTITIONED BY (`dt` string)

STORED AS PARQUET
;

alter table random_data.xiaofei_kuanb2 set TBLPROPERTIES ('sink.partition-commit.policy.kind'='metastore,success-file'); 

查询的时候使用MSCK REPAIR TABLE random_data.xiaofei_kuanb2 
才能查到数据SELECT * FROM random_data.xiaofei_kuanb2 

4.hive外表
CREATE EXTERNAL TABLE random_data.xiaofei_kuanb(
  `name` string,
  `address` string,
  `restaurant` string,
  `food` string,
  `price` double,
  `count` INT,
  `gmv` double,
  `email` string,
  `phone` string,
  `job` string,
  `company` string,
  `tm` double
)
PARTITIONED BY (`dt` string COMMENT '分区字段')
STORED AS ORC
;

分区需要在插入语句中指定格式否则hive会查不到数据
ALTER table random_data.xiaofei_kuanb  add partition(dt='2024-11-08') location '/opt/hive/warehouse/random_data.db/xiaofei_kuanb/2024-11-08';

5.doris建表
插入doris做了两版本实验一种有分区的,一种没有分区的。最终程序一有分区的为准，但是两个实验的建表语句都放在这吧
遇到问题,在虚拟机搭建的fe和be,但是fe连接不上be了,
在mysql中删除了BACKEND然后重新add就好了。不清楚有啥问题
drop table random_data.xiaofei_kuanb2;
CREATE TABLE random_data.xiaofei_kuanb2(
  `name` varchar(200),
  `address`  varchar(500),
  `restaurant`  varchar(500),
  `food` varchar(500),
  `price` double,
  `count` int,
  `gmv` double,
  `email` string,
  `phone` string,
  `job` string,
  `company` string,
  `tm` BIGINT,
  `dt` DATE
  )
ENGINE=olap

DUPLICATE KEY(name,address,restaurant,food)		--string不能做key
PARTITION BY RANGE(dt)()		--分区字段也不能是string
DISTRIBUTED BY HASH(name,food)	BUCKETS AUTO
PROPERTIES
(
    "dynamic_partition.enable" = "true",
    "dynamic_partition.time_unit" = "DAY",
    "dynamic_partition.start" = "-7",
    "dynamic_partition.end" = "3",
    "dynamic_partition.prefix" = "p",
    "dynamic_partition.buckets" = "32",
	"replication_num" = "1",
    "min_load_replica_num" = "1"
);



drop table random_data.xiaofei_kuanb2;
CREATE TABLE random_data.xiaofei_kuanb2(
  `name` varchar(200),
  `address`  varchar(500),
  `restaurant`  varchar(500),
  `food` varchar(500),
  `price` double,
  `count` int,
  `gmv` double,
  `email` string,
  `phone` string,
  `job` string,
  `company` string,
  `tm` BIGINT,
  `dt` varchar(20)
  )
ENGINE=olap

DUPLICATE KEY(name,address,restaurant,food)		--string不能做key

DISTRIBUTED BY HASH(name,food)	BUCKETS AUTO
PROPERTIES
(

	"replication_num" = "1",
    "min_load_replica_num" = "1"
);