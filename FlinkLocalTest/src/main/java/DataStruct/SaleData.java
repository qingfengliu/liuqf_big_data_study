package DataStruct;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleData {
    String name;
    String address;
    String restaurant;
    String food;
    int count;
    double price;
    double gmv;
    long tm;

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
                ", tm:" + tm +
                '}';
    }
}
