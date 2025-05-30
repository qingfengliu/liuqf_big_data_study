package org.liuqf;


import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class Main {
    public static void main(String[] args) throws Exception {

        //1. env-准备环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 获取tableEnv对象
        // 通过env 获取一个table 环境
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        tEnv.executeSql("CREATE TABLE sale_order ( \n" +
                "\tname String, \n" +
                "\taddress  String, \n" +
                "\trestaurant String, \n" +
                "\tfood      String, \n" +
                "\t`count`      BIGINT, \n" +
                "\tprice      DOUBLE, \n" +
                "\tgmv      DOUBLE, \n" +
                "\t`tm` BIGINT,\n" +
                "\tts AS TO_TIMESTAMP_LTZ(tm, 3),\n" +
                "\tWATERMARK FOR ts AS ts - INTERVAL '5' SECOND\n" +
                ") WITH (\n" +
                "\t'connector'= 'kafka', \n" +
                "\t'topic'= 'sale_random_data',\n" +
                "\t'properties.bootstrap.servers'='hadoop1:9092,hadoop2:9092,hadoop3:9092',\n" +
                "\t'properties.group.id'='abc',\n" +
                "\t'scan.startup.mode' = 'latest-offset',\n" +
                "\t'format'= 'json'\n" +
                ");");

        Table resultTable = tEnv.sqlQuery("SELECT food,price,sum(gmv) over(PARTITION BY food \n" +
                "ORDER BY ts\n" +
                "RANGE BETWEEN INTERVAL '30' MINUTE PRECEDING AND CURRENT ROW\n" +
                ") as money\n" +
                "FROM \n" +
                "sale_order\n" +
                ";");
        DataStream<Row> resultStream = tEnv.toChangelogStream(resultTable);

        resultStream.print();//这个在webui上可以看到
        env.execute();

    }
}