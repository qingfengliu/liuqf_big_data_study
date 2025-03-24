package org.liuqf.make_random_data_product.pojo;

import lombok.Data;

@Data
public class PersonData {
    private String name;
    private String address;
    private String phone;
    private String email;
    private String company;
    private String job;
    private long tm;

    public String toString() {
        //返回格式为json
        return "{\"name\":\"" + name + "\",\"address\":\"" + address + "\",\"phone\":\"" + phone + "\",\"email\":\"" + email + "\",\"company\":\"" + company + "\",\"job\":\"" + job + "\",\"tm\":" + tm + "}";
    }
}
