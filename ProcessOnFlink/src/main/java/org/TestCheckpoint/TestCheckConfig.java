package org.TestCheckpoint;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.execution.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class TestCheckConfig {
    public static void main(String[] args) {
        StreamExecutionEnvironment env= StreamExecutionEnvironment.getExecutionEnvironment();

//        StreamExecutionEnvironment env = streamExecutionEnvironment.createLocalEnvironmentWithwebUI(new configurationn());
        System.setProperty("HADOOP_USER_NAME","hadoop" );


        env.setParallelism(1);

        //开启changelog状态后端
        //要求checkpoint的最大并发必须为1,其他参数建议在配置文件中配置
        env.enableChangelogStateBackend(true);

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

        //TODO 非对其检查点
        // 开启之后的要求:checkpoint模式必须是精准一次，最大并发必须设为1
        //checkpointConfig.enableUnalignedCheckpoints();


        env.socketTextStream( "hadoop1",7777)
                .flatMap(
                        (String value, Collector<Tuple2<String,Integer>> out)->{
                            String[]words = value.split( " ");
                            for(String word :words) {
                                out.collect(Tuple2.of(word, 1));
                            }
                        })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(value ->value.f0)
                .sum(1)
                .print();

        try {
            env.execute("TestCheckConfig");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
