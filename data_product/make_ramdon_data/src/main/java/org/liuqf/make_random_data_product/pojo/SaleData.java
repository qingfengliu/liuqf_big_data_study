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

    public String toString() {
        //返回格式为json
        return "{\"name\":\"" + name + "\",\"address\":\"" + address + "\",\"restaurant\":\"" + restaurant + "\",\"food\":\"" + food + "\",\"count\":" + count + ",\"price\":" + price + ",\"gmv\":" + gmv + ",\"tm\":" + tm + "}";
    }

}
