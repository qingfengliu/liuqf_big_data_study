package DataStruct;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleData {

    int id;
    String name;
    int age;
    double num;
    long ts;

    public SimpleData(int id, String name, int age,double num, long ts) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.num = num;
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "SimpleData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", num=" + num +
                ", ts=" + ts +
                '}';
    }
}
