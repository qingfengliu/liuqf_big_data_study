package org.liuqf.make_random_data;
import net.datafaker.transformations.JsonTransformer;
import net.datafaker.transformations.Schema;
import net.datafaker.Faker;
import static net.datafaker.transformations.Field.field;

public class Address_Data {
    private Faker faker = new Faker();

    String city = faker.address().city();
    String country = faker.address().country();
    String state = faker.address().state();
    String zip = faker.address().zipCode();
    //距离火车站距离
    Double distance = faker.random().nextDouble(0.1, 100);

    String make() {
        Schema<Object, ?> schema = Schema.of(
                field("city", () -> city),
                field("country", () -> country),
                field("state", () -> state),
                field("zip", () -> zip),
                field("distance", () -> distance)
        );
        JsonTransformer<Object> transformer = JsonTransformer.builder().build();

        String json = transformer.generate(schema, 1);
        return json;
    }


    String get_city() {
        return city;
    }

    String get_country() {
        return country;
    }

    String get_state() {
        return state;
    }

    String get_zip() {
        return zip;
    }

    Double get_distance() {
        return distance;
    }
}
