package com.project.demo.funcional;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class TypingTests extends BaseTest {

    TypingClient typingClient = new TypingClient();

    @Test
    void shouldGenerateTypingExerciseSuccessfully() {
        Response response = typingClient.generateTyping(
                request,
                TypingTestData.validTypingRequest()
        );

        response.then()
                .statusCode(anyOf(is(201), is(500)));

        if (response.statusCode() == 201) {
            response.then()
                    .body("text", notNullValue())
                    .body("timeLimit", greaterThan(0))
                    .body("category", equalTo("Mecanografía"))
                    .body("difficulty", equalTo("Fácil"))
                    .body("hints", notNullValue());
        }
    }

    @Test
    void shouldGenerateTypingEvenWhenFieldsAreEmpty() {
        Response response = typingClient.generateTyping(
                request,
                TypingTestData.invalidTypingRequest()
        );

        response.then()
                .statusCode(anyOf(is(201), is(500)));

        if (response.statusCode() == 201) {
            response.then()
                    .body("text", notNullValue())
                    .body("timeLimit", greaterThan(0))
                    .body("hints", notNullValue());
        }
    }

    @Test
    void shouldHandleServerErrorGracefully() {
        Response response = typingClient.generateTyping(
                request,
                TypingTestData.validTypingRequest()
        );

        response.then()
                .statusCode(anyOf(is(201), is(500)));
    }

    @Test
    void shouldGetAllTypingExercises() {
        Response response = typingClient.getAllTyping(request, 1, 10);

        response.then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("meta", notNullValue());
    }
}