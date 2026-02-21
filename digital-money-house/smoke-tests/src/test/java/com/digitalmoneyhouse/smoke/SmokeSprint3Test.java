package com.digitalmoneyhouse.smoke;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SmokeSprint3Test extends BaseSmokeTest {

    private static String token;
    private static Long accountId;
    private static String tokenOther;
    private static Long accountIdOther;
    private static Long cardId;
    private static Long cardIdOther;
    private static Long transactionId;

    @BeforeAll
    static void setup() {
        BaseSmokeTest base = new SmokeSprint3Test();
        String email = "smoke.sprint3.%s@test.com".formatted(System.currentTimeMillis());
        RegisterData user = base.registerUser(email, "password123");
        token = base.loginAndGetToken(user.email(), user.password());
        accountId = base.getAccountIdByUser(token, user.userId());

        String emailOther = "smoke.s3.other.%s@test.com".formatted(System.currentTimeMillis());
        RegisterData userOther = base.registerUser(emailOther, "pass456");
        tokenOther = base.loginAndGetToken(userOther.email(), userOther.password());
        accountIdOther = base.getAccountIdByUser(tokenOther, userOther.userId());

        String cardBody = "{\"number\":\"9876543210987654\",\"type\":\"DEBIT\",\"holderName\":\"S3 Test\",\"expiry\":\"12/30\"}";
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
        cardId = cardIdObj instanceof Number n ? n.longValue() : Long.parseLong(cardIdObj.toString());
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"cardId\":" + cardId + "}")
                .when()
                .post("/accounts/" + accountId + "/cards")
                .then()
                .statusCode(201);

        String cardOtherBody = "{\"number\":\"1111222233334444\",\"type\":\"CREDIT\",\"holderName\":\"Other\",\"expiry\":\"01/29\"}";
        Object cardIdOtherObj = given()
                .header("Authorization", "Bearer " + tokenOther)
                .contentType(ContentType.JSON)
                .body(cardOtherBody)
                .when()
                .post("/cards")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
        cardIdOther = cardIdOtherObj instanceof Number n ? n.longValue() : Long.parseLong(cardIdOtherObj.toString());
        given()
                .header("Authorization", "Bearer " + tokenOther)
                .contentType(ContentType.JSON)
                .body("{\"cardId\":" + cardIdOther + "}")
                .when()
                .post("/accounts/" + accountIdOther + "/cards")
                .then()
                .statusCode(201);
    }

    @Test @Order(1)
    void S3_01_getActivity_withToken_returns200() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts/" + accountId + "/activity")
                .then()
                .statusCode(200)
                .body("", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test @Order(2)
    void S3_02_getActivity_withoutToken_returns401() {
        given()
                .when()
                .get("/accounts/" + accountId + "/activity")
                .then()
                .statusCode(401);
    }

    @Test @Order(3)
    void S3_03_getActivity_otherAccount_returns403() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts/" + accountIdOther + "/activity")
                .then()
                .statusCode(403);
    }

    @Test @Order(4)
    void S3_07_deposit_returns201() {
        String body = "{\"cardId\":" + cardId + ",\"amount\":50.00,\"description\":\"Smoke deposit\"}";
        Object txId = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/accounts/" + accountId + "/transferences")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("type", equalTo("DEPOSIT"))
                .body("amount", equalTo(50.0f))
                .extract()
                .path("id");
        transactionId = txId instanceof Number n ? n.longValue() : Long.parseLong(txId.toString());
    }

    @Test @Order(5)
    void S3_04_getTransactionDetail_returns200() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts/" + accountId + "/activity/" + transactionId)
                .then()
                .statusCode(200)
                .body("id", equalTo(transactionId.intValue()))
                .body("type", equalTo("DEPOSIT"))
                .body("amount", notNullValue());
    }

    @Test @Order(6)
    void S3_06_getTransactionDetail_nonexistent_returns404() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts/" + accountId + "/activity/999999")
                .then()
                .statusCode(404);
    }

    @Test @Order(7)
    void S3_05_getTransactionDetail_withoutToken_returns401() {
        given()
                .when()
                .get("/accounts/" + accountId + "/activity/" + transactionId)
                .then()
                .statusCode(401);
    }

    @Test @Order(8)
    void S3_08_deposit_withoutToken_returns401() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"cardId\":1,\"amount\":10.00}")
                .when()
                .post("/accounts/" + accountId + "/transferences")
                .then()
                .statusCode(401);
    }

    @Test @Order(9)
    void S3_09_deposit_cardFromOtherAccount_returns404() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"cardId\":" + cardIdOther + ",\"amount\":10.00}")
                .when()
                .post("/accounts/" + accountId + "/transferences")
                .then()
                .statusCode(404);
    }

    @Test @Order(10)
    void S3_10_deposit_invalidAmount_returns400() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"cardId\":" + cardId + ",\"amount\":0}")
                .when()
                .post("/accounts/" + accountId + "/transferences")
                .then()
                .statusCode(400);
    }
}
