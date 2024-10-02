package org.liuqf.make_random_data_product;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

public class Productor {
    public static void main(String[] args) {
//        Faker faker = new Faker();
//
//        for (int i = 0; i < 10; i++) {
//            String name = faker.name().fullName(); // Miss Samanta Schmidt
//            String streetAddress = faker.address().streetAddress(); // 60018 Sawayn Brooks Suite 449
//            System.out.println(name + " " + streetAddress);
//        }

        Properties prop = new Properties();
        //指定kafka的broker地址
        prop.put("bootstrap.servers", "hadoop1:9092,hadoop2:9092,hadoop3:9092");
        //指定key-value数据的序列化格式
        prop.put("key.serializer", StringSerializer.class.getName());
        prop.put("value.serializer", StringSerializer.class.getName());


        //指定topic
        String topic = "test";

        //创建kafka生产者
        KafkaProducer<String, String> producer = new KafkaProducer<String,String>(prop);

        for (int i = 0; i < 10; i++) {
            //向topic中生产数据
            producer.send(new ProducerRecord<String, String>(topic, "hello kafka" + i));
        }

        //关闭链接
        producer.close();

        System.out.println("Hello world!");
    }
}