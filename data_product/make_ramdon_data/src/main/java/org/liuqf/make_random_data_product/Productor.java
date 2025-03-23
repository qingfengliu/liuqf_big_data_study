package org.liuqf.make_random_data_product;

import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.liuqf.make_random_data_product.pojo.PersonData;
import org.liuqf.make_random_data_product.pojo.SaleData;


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
                List<List> data=dataFactory.run_data(i);
                for(i=0;i<data.get(0).size();i++){
                    SaleData saleDate=(SaleData)data.get(0).get(i);
                    PersonData personData=(PersonData)data.get(1).get(i);
                    System.out.println(saleDate.toString());
                    producer_sale.send(new ProducerRecord<String, String>("sale_random_data", saleDate.toString()));
//                    producer_sale.send(new ProducerRecord<String, String>("person_random_data", personData.toString()));
                    producer_sale.flush();
                }
//                break;//一般拿10个数据测试一下
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer_sale.close();
        }
    }
}
