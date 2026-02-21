package com.digitalmoneyhouse.smoke;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SmokeSprint2Test extends BaseSmokeTest {

    private static String token;
    private static Long userId;
    private static Long accountId;
    private static String tokenOther;
    private static Long userIdOther;
    private static Long accountIdOther;
    private static Long cardId;

    @BeforeAll
    static void setup() {
        BaseSmokeTest base = new SmokeSprint2Test();
        String email = "smoke.sprint2.%s@test.com".formatted(System.currentTimeMillis());
        RegisterData user = base.registerUser(email, "password123");
        token = base.loginAndGetToken(user.email(), user.password());
        userId = user.userId();
        accountId = base.getAccountIdByUser(token, userId);

        String emailOther = "smoke.other.%s@test.com".formatted(System.currentTimeMillis());
        RegisterData userOther = base.registerUser(emailOther, "pass456");
        tokenOther = base.loginAndGetToken(userOther.email(), userOther.password());
        userIdOther = userOther.userId();
        accountIdOther = base.getAccountIdByUser(tokenOther, userIdOther);
    }

    @Test @Order(1)
    void S2_01_getBalance_withToken_returns200() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts/" + accountId)
                .then()
                .statusCode(200)
                .body("balance", notNullValue());
    }

    @Test @Order(2)
    void S2_02_getBalance_withoutToken_returns401() {
        given()
                .when()
                .get("/accounts/" + accountId)
                .then()
                .statusCode(401);
    }

    @Test @Order(3)
    void S2_03_getBalance_otherUserAccount_returns403() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts/" + accountIdOther)
                .then()
                .statusCode(403);
    }

    @Test @Order(4)
    void S2_04_getTransactions_withToken_returns200() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts/" + accountId + "/transactions")
                .then()
                .statusCode(200)
                .body("", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test @Order(5)
    void S2_05_getTransactions_withoutToken_returns401() {
        given()
                .when()
                .get("/accounts/" + accountId + "/transactions")
                .then()
                .statusCode(401);
    }

    @Test @Order(6)
    void S2_06_getProfile_ownUser_returns200WithCvuAlias() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(200)
                .body("id", equalTo(userId.intValue()))
                .body("cvu", notNullValue())
                .body("alias", notNullValue());
    }

    @Test @Order(7)
    void S2_07_getProfile_otherUser_returns403() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/users/" + userIdOther)
                .then()
                .statusCode(403);
    }

    @Test @Order(8)
    void S2_08_getProfile_withoutToken_returns401() {
        given()
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(401);
    }

    @Test @Order(9)
    void S2_09_createCard_returns201() {
        String body = """
                {"number":"1234567890123456","type":"DEBIT","holderName":"Smoke Test","expiry":"12/28"}
                """;
        Object id = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/cards")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .path("id");
        cardId = id instanceof Number n ? n.longValue() : Long.parseLong(id.toString());
    }

    @Test @Order(10)
    void S2_10_associateCardToAccount_returns201() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"cardId\":" + cardId + "}")
                .when()
                .post("/accounts/" + accountId + "/cards")
                .then()
                .statusCode(201);
    }

    @Test @Order(11)
    void S2_11_associateSameCardToOtherAccount_returns409() {
        given()
                .header("Authorization", "Bearer " + tokenOther)
                .contentType(ContentType.JSON)
                .body("{\"cardId\":" + cardId + "}")
                .when()
                .post("/accounts/" + accountIdOther + "/cards")
                .then()
                .statusCode(409);
    }

    @Test @Order(12)
    void S2_12_listCards_returns200() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cards/accounts/" + accountId + "/cards")
                .then()
                .statusCode(200)
                .body("", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test @Order(13)
    void S2_13_getCardDetail_returns200() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cards/accounts/" + accountId + "/cards/" + cardId)
                .then()
                .statusCode(200)
                .body("id", equalTo(cardId.intValue()))
                .body("number", notNullValue());
    }

    @Test @Order(14)
    void S2_14_deleteCard_returns200() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/cards/accounts/" + accountId + "/cards/" + cardId)
                .then()
                .statusCode(200);
    }

    @Test @Order(15)
    void S2_15_getCardNonexistent_returns404() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/cards/accounts/" + accountId + "/cards/999999")
                .then()
                .statusCode(404);
    }

    @Test @Order(16)
    void S2_16_getAccountByUser_returns200WithCvuAlias() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/accounts/by-user/" + userId)
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("cvu", notNullValue())
                .body("alias", notNullValue());
    }

    @Test @Order(17)
    void S2_17_createCardWithoutRequiredFields_returns400() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/cards")
                .then()
                .statusCode(400);
    }
}
