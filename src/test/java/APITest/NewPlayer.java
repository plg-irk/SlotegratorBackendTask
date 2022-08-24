package APITest;

import com.github.javafaker.Faker;

public class NewPlayer {
    static Faker faker =new Faker();
    static String username = faker.name().username();
    static String password_change = faker.toString();
    static String password_repeat = password_change;
    static String email = faker.internet().emailAddress();
    static String name = faker.name().firstName();
    static String surname = faker.name().lastName();
}
