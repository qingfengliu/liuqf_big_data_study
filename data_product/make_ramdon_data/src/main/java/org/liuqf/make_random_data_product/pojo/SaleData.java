package org.liuqf.make_random_data_product.pojo;

import lombok.Data;

@Data
public class SaleData {
    private String name;
    private String address;
    private String restaurant;
    private String food;
    private int count;
    private double price;
    private double gmv;
    private long tm;

}
