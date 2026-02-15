package com.digitalmoneyhouse.smoke;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseSmokeTest {

    protected static final String BASE_URI = System.getProperty("test.base.uri", "http://localhost:8080");

    @BeforeAll
    static void setupRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected String loginAndGetToken(String email, String password) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    protected RegisterData registerUser(String email, String password) {
        String body = """
                {"firstName":"Smoke","lastName":"Test","email":"%s","password":"%s"}
                """.formatted(email, password);
        var response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/users/register")
                .then()
                .statusCode(201)
                .extract()
                .response();
        Object id = response.path("id");
        long userId = id instanceof Number n ? n.longValue() : Long.parseLong(id.toString());
        String cvu = response.path("cvu");
        String alias = response.path("alias");
        return new RegisterData(userId, email, password, cvu, alias);
    }

    protected Long getAccountIdByUser(String token, Long userId) {
        Object id = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts/by-user/" + userId)
                .then()
                .statusCode(200)
                .extract()
                .path("id");
        return id instanceof Number n ? n.longValue() : Long.valueOf(id.toString());
    }

    protected record RegisterData(long userId, String email, String password, String cvu, String alias) {}
}
