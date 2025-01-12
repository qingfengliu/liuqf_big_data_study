package org.liuqf.make_random_data;
import net.datafaker.transformations.JsonTransformer;
import net.datafaker.transformations.Schema;
import net.datafaker.Faker;
import static net.datafaker.transformations.Field.field;

public class Person_Data {
    private Faker faker = new Faker();


    String address = faker.address().streetAddress();
    String phone = faker.phoneNumber().cellPhone();
    String email = faker.internet().emailAddress();
    String company = faker.company().name();
    String job = faker.job().position();
    String make() {
        Schema<Object, ?> schema = Schema.of(
                field("address", () -> address),
                field("phone", () -> phone),
                field("email", () -> email),
                field("company", () -> company),
                field("job", () -> job)
        );
        JsonTransformer<Object> transformer = JsonTransformer.builder().build();
        String json = transformer.generate(schema, 1);
        return json;
    }


    String get_address() {
        return address;
    }

    String get_phone() {
        return phone;
    }

    String get_email() {
        return email;
    }

    String get_company() {
        return company;
    }

    String get_job() {
        return job;
    }
}
