package org.liuqf.make_random_data.pojo;
import net.datafaker.transformations.JsonTransformer;
import net.datafaker.transformations.Schema;
import net.datafaker.Faker;
import static net.datafaker.transformations.Field.field;

public class Restaurant_Data {
    private Faker faker = new Faker();


    String company = faker.company().name();
    //生成经纬度
    String address = faker.address().latitude() + "," + faker.address().longitude();
    //生成墙颜色
    String color = faker.color().name();

    String make() {
        Schema<Object, ?> schema = Schema.of(
                field("company", () -> company),
                field("address", () -> address),
                field("color", () -> color)

        );
        JsonTransformer<Object> transformer = JsonTransformer.builder().build();

        String json = transformer.generate(schema, 1);
        return json;
    }


    public String get_address() {
        return address;
    }


    public String get_company() {
        return company;
    }

    public String get_color() {
        return color;
    }

}
