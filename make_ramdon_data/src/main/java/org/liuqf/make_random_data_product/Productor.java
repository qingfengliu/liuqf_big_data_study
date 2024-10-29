package org.liuqf.make_random_data_product;

import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;


public class Productor {
    KafkaProducer<String, String> producer_sale;

    Properties prop;

    Productor() {
        this.prop = new Properties();
        prop.put("bootstrap.servers", "hadoop1:9092,hadoop2:9092,hadoop3:9092");
        prop.put("key.serializer", StringSerializer.class.getName());
        prop.put("value.serializer", StringSerializer.class.getName());
        this.producer_sale = new KafkaProducer(prop);

    }


    public static void main(String[] args) {
        Productor test = new Productor();
        test.run();
    }

    public void run() {
        DataFactory dataFactory = null;
        try {
            dataFactory = new DataFactory(1000 * 30);   // 30s

            for (int i = 0; i < 100000; i += 10) {
                List<List<String>> data=dataFactory.run_data(i);
                data.forEach((List<String> item) -> {
                    //第二个topic有时候会出现数据丢失的情况，这个是因为kafka的分区策略导致的，可以通过自定义分区策略解决
                    System.out.println(item.get(0));
                    System.out.println(item.get(1));
//                    producer_sale.send(new ProducerRecord<String, String>("sale_random_data", item.get(0)));
//                    producer_sale.send(new ProducerRecord<String, String>("person_random_data", item.get(1)));
//                    producer_sale.flush();
                });
                break;//一般拿10个数据测试一下
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer_sale.close();
            dataFactory.close();
        }
    }
}
