sale_random_data  两个主题
person_random_data


1.命令备份
/opt/kafka/bin/kafka-console-producer.sh --topic sale_random_data --broker-list 192.168.0.9:9092
/opt/kafka/bin/kafka-console-producer.sh --topic person_random_data --broker-list 192.168.0.9:9092


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

调研了flink连接hive的方式。
	目前有这么集中。
	(1) jdbc 不适合,会产生小文件问题。并且找的例子里用了富函数暂不尝试。
	(2) 写入hdfs,hive用外表读取hdfs数据。
	(3) 将datastream转换成table,然后用flink-connect-hive里提供的HiveCatalog读写
