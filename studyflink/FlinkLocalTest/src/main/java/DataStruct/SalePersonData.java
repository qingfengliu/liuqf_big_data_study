package DataStruct;

public class SalePersonData {
    String name;
    String address;
    String restaurant;
    String food;
    double price;
    int count;
    double gmv;
    String email;
    String phone;
    String job;
    String company;

    long tm;
    String dt;

    //静态内部类可以new
    @Override
    public String toString() {
        return "{" +
                "name:'" + name + '\'' +
                ", address:'" + address + '\'' +
                ", restaurant:'" + restaurant + '\'' +
                ", food:'" + food + '\'' +
                ", price:" + price +
                ", count:" + count +
                ", gmv:" + gmv +
                ", email:'" + email + '\'' +
                ", phone:'" + phone + '\'' +
                ", job:'" + job + '\'' +
                ", company:'" + company + '\'' +
                ", tm:" + tm +
                ", dt:'" + dt + '\'' +
                '}';
    }
}
