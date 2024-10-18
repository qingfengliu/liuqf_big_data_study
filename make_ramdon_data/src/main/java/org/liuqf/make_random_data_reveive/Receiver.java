package org.liuqf.make_random_data_reveive;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

public class Receiver {
    public static void main(String[] args) {
        Properties prop = new Properties();
        prop.put("bootstrap.servers", "192.168.0.9:9092");
        prop.put("key.deserializer", StringDeserializer.class.getName());
        prop.put("value.deserializer", StringDeserializer.class.getName());
        //从最新的数据开始消费
        prop.put("auto.offset.reset", "latest");
        prop.put("group.id", "con-1"); //报错了必须提供group.id
        KafkaConsumer<String, String> consumer = new KafkaConsumer(prop);
        Collection<String> topics = new ArrayList();
        topics.add("sale_random_data");
        consumer.subscribe(topics);

        while(true) {
            ConsumerRecords<String, String> poll = consumer.poll(Duration.ofSeconds(1L));
            Iterator var5 = poll.iterator();

            while(var5.hasNext()) {
                ConsumerRecord<String, String> consumerRecord = (ConsumerRecord)var5.next();
                System.out.println((String)consumerRecord.value());
            }
        }
    }
}
