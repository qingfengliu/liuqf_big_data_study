package org.liuqf.make_random_data_product;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
public class Productor {
    public static void main(String[] args) {
        Properties prop = new Properties();
        prop.put("bootstrap.servers", "hadoop1:9092,hadoop2:9092,hadoop3:9092");
        prop.put("key.serializer", StringSerializer.class.getName());
        prop.put("value.serializer", StringSerializer.class.getName());
        String topic = "test";
        KafkaProducer<String, String> producer = new KafkaProducer(prop);

        for(int i = 0; i < 10; ++i) {
            producer.send(new ProducerRecord(topic, "hello kafka" + i));
        }

        producer.close();
        System.out.println("Hello world!");
    }
}
