package APITest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class LoginGuestTest {

    RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://test-api.d6.dev.devcaz.com/")
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build();
    static String tokenGuest;
    static String tokenPlayer;
    static String username;
    static int IdPlayer;
    static JSONObject myMap = new JSONObject();
    @Test
    public void getTokenGuestTest() {

        String requestBody = "{ \"grant_type\": \"client_credentials\",\"scope\": \"guest:default\"}";

        JsonPath response = given()
                .spec(requestSpec)
                .auth()
                .preemptive()
                .basic("front_2d6b0a8391742f5d789d7d915755e09e", "")
                .body(requestBody)
                .when()
                .post(EndPoint.guests)
                .then()
                .statusCode(200)
                .extract()
                .body().jsonPath();

        assertThat(response.get("access_token"), notNullValue());
        tokenGuest = response.get("access_token").toString();
    }
    @Nested
    public class PlayersPage {
        @Test
        public void registerNewPlayerTest() {

            username = NewPlayer.username;
            myMap.put("username", username);
            myMap.put("password_change", "Faker18a25bbd===");
            myMap.put("password_repeat", "Faker18a25bbd===");
            myMap.put("email", NewPlayer.email);
            myMap.put("name", NewPlayer.name);
            myMap.put("surname", NewPlayer.surname);

            JsonPath response = given()
                    .auth()
                    .preemptive()
                    .oauth2(tokenGuest)
                    .spec(requestSpec)
                    .body(myMap.toString())
                    .when()
                    .post(EndPoint.players)
                    .then()
                    .statusCode(201)
                    .extract()
                    .body()
                    .jsonPath();
            IdPlayer = response.get("id");
        }
        @Nested
        public class RegistrationPlayer {
            @Test
            public void protectResourceRequestTest() {

                String requestBody = "{\n" +
                        "\"grant_type\": \"password\",\n" +
                        "\"username\": \"" + username + "\",\n" +
                        "\"password\": \"Faker18a25bbd===\"\n" +
                        "}";
                JsonPath response = given()
                        .auth()
                        .preemptive()
                        .basic("front_2d6b0a8391742f5d789d7d915755e09e", "")
                        .spec(requestSpec)
                        .body(requestBody)
                        .when()
                        .post(EndPoint.playersToken)
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath();
                tokenPlayer = response.get("access_token").toString();
            }
            @Nested
            public class PlayerProfile {

                @Test
                public void getSingleProfilePlayerTest() {

                    given()
                            .auth()
                            .preemptive()
                            .oauth2(tokenPlayer)
                            .spec(requestSpec)
                            .pathParam("id", IdPlayer)
                            .when()
                            .get(EndPoint.playerProfile)
                            .then()
                            .statusCode(200);
                }
                @Test
                public void getSingleProfilePlayerFailTest() {

                    given()
                            .auth()
                            .preemptive()
                            .oauth2(tokenPlayer)
                            .spec(requestSpec)
                            .pathParam("id", 12187)
                            .when()
                            .get(EndPoint.playerProfile)
                            .then()
                            .statusCode(404);
                }
            }
        }
    }
}