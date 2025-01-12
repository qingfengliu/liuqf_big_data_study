package org.TestCheckpoint;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.execution.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class SavePointDemo {
    public static void main(String[] args) {
        StreamExecutionEnvironment env= StreamExecutionEnvironment.getExecutionEnvironment();


        System.setProperty("HADOOP_USER_NAME","hadoop" );


        env.setParallelism(1);

        env.setDefaultSavepointDirectory("hdfs:/flink/savepoints");
        //TODO 检查点配置
        // 1、启用检查点:默认是barrier对齐的，周期为5s，精准一次
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        // 2、指定检查点的存储位置
        CheckpointConfig checkpointConfig=env.getCheckpointConfig();
        checkpointConfig.setCheckpointStorage("hdfs:/flink/checkpoint");
        // 3、设置检查点的超时时间
        checkpointConfig.setCheckpointTimeout(60000);
        // 4、设置最大并发检查点
        checkpointConfig.setMaxConcurrentCheckpoints(1);
        // 5、设置检查点的最小间隔时间
        checkpointConfig.setMinPauseBetweenCheckpoints(500);
        // 6、设置当作业被cancel时，保留检查点
        checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        // 7、容忍检查点的失败次数
        checkpointConfig.setTolerableCheckpointFailureNumber(3);



        env.socketTextStream( "hadoop1",7777)
                .uid("source")
                .flatMap(
                        (String value, Collector<Tuple2<String,Integer>> out)->{
                            String[]words = value.split( " ");
                            for(String word :words) {
                                out.collect(Tuple2.of(word, 1));
                            }
                        }).uid("flatmap").name("flatmap2")
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(value ->value.f0)
                .sum(1).uid("sum")
                .print().uid("print");

        try {
            env.execute("TestCheckConfig");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
