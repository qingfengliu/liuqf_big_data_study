package org.liuqf.make_random_data_product;

import net.datafaker.transformations.JsonTransformer;
import net.datafaker.transformations.Schema;
import net.datafaker.Faker;
import static net.datafaker.transformations.Field.field;

public class cun_random_data {
    public cun_random_data() {

    }

    public cun_random_data(long timebreak, int run_times, long sleep_time) {
        this.timebreak = timebreak;
        this.run_times = run_times;
        this.sleep_time = sleep_time;
    }
    //最高运行时间
    private long timebreak = 1000 * 60 * 60;
    //每分钟运行次数
    private int run_times = 10;
    //每次运行间隔时间
    private long sleep_time = 1000 * 60; // 1分钟

    private Faker faker = new Faker();

    public String get_data(){

        Integer count = faker.random().nextInt(1, 100);
        Double price = faker.random().nextDouble(10, 1000);

        Schema<Object, ?> schema = Schema.of(
                field("name", () -> faker.name().fullName()),
                field("address", () -> faker.address().streetAddress()),
                field("restaurant", () -> faker.restaurant().name()),
                field("food", () -> faker.food().ingredient()),
                field("count", () -> count),
                field("price", () -> price),
                field("gmv", () -> count * price)
        );

        JsonTransformer<Object> transformer = JsonTransformer.builder().build();

        String json = transformer.generate(schema, 1);
        return json;
    }

    //javabean


    public int getRun_times() {
        return run_times;
    }

    public void setRun_times(int run_times) {
        this.run_times = run_times;
    }

    public long getTimebreak() {
        return timebreak;
    }

    public void setTimebreak(long timebreak) {
        this.timebreak = timebreak;
    }

    public long getSleep_time() {
        return sleep_time;
    }

    public void setSleep_time(long sleep_time) {
        this.sleep_time = sleep_time;
    }

}
