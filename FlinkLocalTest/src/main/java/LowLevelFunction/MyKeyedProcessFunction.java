package LowLevelFunction;

import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

//定时器
public class MyKeyedProcessFunction extends KeyedProcessFunction<String, String, String> {
    @Override
    public void processElement(String value, Context ctx, Collector<String> out) throws Exception {
        //注册一个定时器。定时器的触发是依赖于watermark的,需要注意event time的流水线自带的-1ms.
        // 所以当时间戳是当前时间+5001ms的事件到来时，定时器会触发
        ctx.timerService().registerEventTimeTimer(ctx.timestamp() + 5000);
        System.out.println("当前key:"+ctx.getCurrentKey()+",当前时间=" +ctx.timestamp()+ ",注册了一个5s的定时器");

    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
        System.out.println("定时器触发了，触发时间=" + timestamp);
    }
}
