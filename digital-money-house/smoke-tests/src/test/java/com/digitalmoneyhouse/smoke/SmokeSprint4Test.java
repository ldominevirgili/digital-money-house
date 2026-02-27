package com.digitalmoneyhouse.smoke;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SmokeSprint4Test extends BaseSmokeTest {

    private static String token;
    private static Long userId;
    private static Long accountId;
    private static String tokenOther;
    private static Long userIdOther;
    private static Long accountIdOther;

    @BeforeAll
    static void setup() {
        BaseSmokeTest base = new SmokeSprint4Test();
        String email = "smoke.sprint4.%s@test.com".formatted(System.currentTimeMillis());
        var user = base.registerUser(email, "password123");
        token = base.loginAndGetToken(user.email(), user.password());
        userId = user.userId();
        accountId = base.getAccountIdByUser(token, userId);

        String emailOther = "smoke.s4.other.%s@test.com".formatted(System.currentTimeMillis());
        var userOther = base.registerUser(emailOther, "pass456");
        tokenOther = base.loginAndGetToken(userOther.email(), userOther.password());
        userIdOther = userOther.userId();
        accountIdOther = base.getAccountIdByUser(tokenOther, userIdOther);
    }

    @Test @Order(1)
    void TF01_transfer_successful_returns200() {
        // deposit funds first
        String cardBody = "{\"number\":\"9999888877776666\",\"type\":\"DEBIT\",\"holderName\":\"S4 Test\",\"expiry\":\"12/30\"}";
        Object cardIdObj = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(cardBody)
                .when()
                .post("/cards")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
        long cardId = cardIdObj instanceof Number n ? n.longValue() : Long.parseLong(cardIdObj.toString());
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"cardId\":" + cardId + ",\"amount\":100.00}")
                .when()
                .post("/accounts/" + accountId + "/transferences")
                .then()
                .statusCode(201);

        // perform transfer to other account by alias
        String targetAlias = given()
                .header("Authorization", "Bearer " + tokenOther)
                .when()
                .get("/accounts/by-user/" + userIdOther)
                .then()
                .statusCode(200)
                .extract()
                .path("alias");

        String transferBody = "{\"target\":\"" + targetAlias + "\",\"amount\":50.00," + "\"description\":\"smoke transfer\"}";
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(transferBody)
                .when()
                .post("/accounts/" + accountId + "/transferences/transfer")
                .then()
                .statusCode(200)
                .body("type", equalTo("TRANSFER_OUT"))
                .body("amount", equalTo(50.0f));
    }

    @Test @Order(2)
    void TF02_insufficientFunds_returns410() {
        // attempt transfer with no funds
        String targetAlias = given()
                .header("Authorization", "Bearer " + tokenOther)
                .when()
                .get("/accounts/by-user/" + userIdOther)
                .then()
                .statusCode(200)
                .extract()
                .path("alias");

        String transferBody = "{\"target\":\"" + targetAlias + "\",\"amount\":999999.00}";
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(transferBody)
                .when()
                .post("/accounts/" + accountId + "/transferences/transfer")
                .then()
                .statusCode(410);
    }
}
