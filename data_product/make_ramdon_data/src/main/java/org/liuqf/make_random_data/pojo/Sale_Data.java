package org.liuqf.make_random_data.pojo;

import net.datafaker.transformations.JsonTransformer;
import net.datafaker.transformations.Schema;
import net.datafaker.Faker;
import static net.datafaker.transformations.Field.field;
public class Sale_Data {

    private Faker faker = new Faker();

    String name=faker.name().firstName();
    String address=faker.address().streetAddress();
    String restaurant=faker.restaurant().name();
    String food=faker.food().ingredient();
    Integer count = faker.random().nextInt(1, 100);
    Double price = faker.random().nextDouble(10, 1000);
    Double gmv = count * price;

    String make(){


        Schema<Object, ?> schema = Schema.of(
                field("name", () -> name),
                field("address", () -> address),
                field("restaurant", () -> restaurant),
                field("food", () -> food),
                field("count", () -> count),
                field("price", () -> price),
                field("gmv", () -> gmv)
        );
        JsonTransformer<Object> transformer = JsonTransformer.builder().build();

        String json = transformer.generate(schema, 1);
        return json;
    }

    public String get_name(){
        return name;
    }

    public String get_address(){
        return address;
    }

    public String get_restaurant(){
        return restaurant;
    }

    public String get_food(){
        return food;
    }

    public Integer get_count(){
        return count;
    }

    public Double get_price(){
        return price;
    }

    public Double get_gmv(){
        return gmv;
    }
}
