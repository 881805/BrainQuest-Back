package com.project.demo.funcional;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class HistoryTests extends BaseTest {

    HistoryClient historyClient = new HistoryClient();

    private static final Long VALID_USER_ID = 1L;
    private static final Long INVALID_USER_ID = 99999L;

    @Test
    void shouldReturnHistoryWhenValidUserAndGameType() {
        Response response = historyClient.getHistoryByUserAndGameType(request, "TRIVIA", VALID_USER_ID);

        response.then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("message", equalTo("History retrieved successfully"));
    }


    @Test
    void shouldReturn404WhenUserNotFound() {
        Response response = historyClient.getHistoryByUserAndGameType(request, "TRIVIA", INVALID_USER_ID);

        response.then()
                .statusCode(404)
                .body(equalTo("User not found"));
    }


    @Test
    void shouldReturn400WhenGameTypeInvalid() {
        Response response = historyClient.getHistoryByUserAndGameType(request, "INVALID_TYPE", VALID_USER_ID);

        response.then()
                .statusCode(400)
                .body(equalTo("Invalid game type"));
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveHistory() {
        Response response = historyClient.getHistoryByUserAndGameType(request, "TRIVIA", VALID_USER_ID);

        response.then()
                .statusCode(200)
                .body("data", notNullValue());
    }
}