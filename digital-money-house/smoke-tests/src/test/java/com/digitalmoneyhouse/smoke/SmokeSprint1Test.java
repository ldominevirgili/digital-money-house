package com.digitalmoneyhouse.smoke;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class SmokeSprint1Test extends BaseSmokeTest {

    @Test
    void register_returns201_withCvuAndAlias() {
        String email = "smoke.s1.%s@test.com".formatted(System.currentTimeMillis());
        given()
                .contentType(ContentType.JSON)
                .body("{\"firstName\":\"A\",\"lastName\":\"B\",\"email\":\"" + email + "\",\"password\":\"pass123\"}")
                .when()
                .post("/users/register")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("cvu", notNullValue())
                .body("alias", notNullValue())
                .body("email", equalTo(email));
    }

    @Test
    void login_returns200_withToken() {
        BaseSmokeTest base = new SmokeSprint1Test();
        String email = "smoke.login.%s@test.com".formatted(System.currentTimeMillis());
        base.registerUser(email, "pass123");
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"pass123\"}")
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("type", equalTo("Bearer"));
    }

    @Test
    void logout_returns200() {
        given()
                .when()
                .post("/user/logout")
                .then()
                .statusCode(200);
    }
}
