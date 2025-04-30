package org.example;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

//org.example.FlickCDC_CheckPoint
public class FlickCDC_CheckPoint {
    public static void main(String[] args) {
        //1.获取Flink执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        //2.开启checkPoint
        env.enableCheckpointing(5000L);
        env.getCheckpointConfig().setCheckpointTimeout(10000L);
        env.getCheckpointConfig().setCheckpointStorage("hdfs://192.168.2.128:9000/flinkcDc/ck");
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        //3.使用FlinkCDC构IMySQLSource
        MySqlSource<String> source = MySqlSource.<String>builder()
                .hostname("192.168.2.128")
                .port(3306)
                .databaseList("test") // set captured database
                .tableList("test.t1") // 写表名是需要加上数据库名的
                .username("root")
                .password("111111")
                .startupOptions(StartupOptions.initial()) //initial,第一次启动读取全量数据。以后增量数据
                .serverTimeZone("Asia/Shanghai")
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();
        //4.读取数据
        DataStreamSource<String> mysqlDS=env.fromSource(source, WatermarkStrategy.noWatermarks(),"mysql-source");
        //5.打印
        mysqlDS.print("mysqlDS");
        //6.启动
        try {
            env.execute("FlickCDC_DataStream");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //直接本地就能跑
    }
}