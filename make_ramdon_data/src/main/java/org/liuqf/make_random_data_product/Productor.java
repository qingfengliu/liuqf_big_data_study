package org.liuqf.make_random_data_product;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;

import org.apache.kafka.common.serialization.StringSerializer;


public class Productor {
    private cun_random_data random_data;
    KafkaProducer<String, String> producer;
    Properties prop;

    Productor() {
        this.prop = new Properties();
        prop.put("bootstrap.servers", "hadoop1:9092,hadoop2:9092,hadoop3:9092");
        prop.put("key.serializer", StringSerializer.class.getName());
        prop.put("value.serializer", StringSerializer.class.getName());
        this.producer = new KafkaProducer(prop);

        this.random_data = new cun_random_data();
    }

    Productor(long timebreak,int run_times,long sleep_time) {
        this();
        this.random_data.setRun_times(run_times);
        this.random_data.setTimebreak(timebreak);
        this.random_data.setSleep_time(sleep_time);
    }

    public static void main(String[] args) {
        Productor test = new Productor(1000 * 60 * 60, 10, 1000 * 60);
        test.run();
    }

    public void run() {
        while (true) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < random_data.getRun_times(); i++) {
                String data = random_data.get_data();
                producer.send(new org.apache.kafka.clients.producer.ProducerRecord<>("test", data));
                System.out.println(data);
            }
            long end = System.currentTimeMillis();
            if (end - start < random_data.getTimebreak()) {
                try {
                    Thread.sleep(random_data.getSleep_time());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }
}
