package DataStruct;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonData {
    String name;
    String address;
    String phone;
    String email;
    String job;
    String company;
    long tm;

    @Override
    public String toString() {
        return "{" +
                "name:'" + name + '\'' +
                ", address:'" + address + '\'' +
                ", phone:'" + phone + '\'' +
                ", email:'" + email + '\'' +
                ", job:'" + job + '\'' +
                ", company:'" + company + '\'' +
                ", tm:" + tm +
                '}';
    }
}
