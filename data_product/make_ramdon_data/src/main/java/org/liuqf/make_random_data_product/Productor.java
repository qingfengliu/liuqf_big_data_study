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
        prop.put("bootstrap.servers", "hadoop1:9092");
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

                //循环10次，每次取出两个数据，一个是sale_data，一个是person_data
                //这里只是打印出来，实际上可以直接发送到kafka
                for (int j = 0; j < data.get(0).size(); j++) {
                    SaleData saleData = (SaleData)(data.get(0).get(j));
                    PersonData personData = (PersonData)(data.get(1).get(j));
//                    System.out.println(personData.toString());
                    System.out.println(saleData.toString());
                    producer_sale.send(new ProducerRecord<String, String>("sale_random_data", saleData.toString()));
//                    producer_sale.send(new ProducerRecord<String, String>("person_random_data", item.get(1)));

//                    producer_sale.send(new ProducerRecord<String, String>("sale_random_data", item.get(0)));
//                    producer_sale.send(new ProducerRecord<String, String>("person_random_data", item.get(1)));
                    producer_sale.flush();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer_sale.close();
        }
    }
}
