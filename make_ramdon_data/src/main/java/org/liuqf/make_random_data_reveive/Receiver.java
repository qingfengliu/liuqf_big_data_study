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
        prop.put("bootstrap.servers", "hadoop1:9092,hadoop2:9092,hadoop3:9092");
        prop.put("key.deserializer", StringDeserializer.class.getName());
        prop.put("value.deserializer", StringDeserializer.class.getName());
        prop.put("group.id", "con-1");
        KafkaConsumer<String, String> consumer = new KafkaConsumer(prop);
        Collection<String> topics = new ArrayList();
        topics.add("test");
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
