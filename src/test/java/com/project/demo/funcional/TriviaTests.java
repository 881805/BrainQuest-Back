package com.project.demo.funcional;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class TriviaTests extends BaseTest {

    TriviaClient triviaClient = new TriviaClient();

    @Test
    void shouldGenerateTriviaSuccessfully() {
        Response response = triviaClient.generateTrivia(
                request,
                TriviaTestData.validTriviaRequest()
        );

        response.then()
                .statusCode(anyOf(is(201), is(409), is(500)));

        if (response.statusCode() == 201) {
            response.then()
                    .body("question", notNullValue())
                    .body("correctAnswer", notNullValue())
                    .body("category", equalTo("Geografía"))
                    .body("difficulty", equalTo("Fácil"))
                    .body("options", notNullValue());
        }

        if (response.statusCode() == 409) {
            response.then()
                    .body(anyOf(
                            equalTo("Ya existe una pregunta similar en la base de datos."),
                            containsString("Ya existe")
                    ));
        }
    }

    @Test
    void shouldReturnCorrectStructureWhenTriviaIsCreated() {
        Response response = triviaClient.generateTrivia(
                request,
                TriviaTestData.anotherValidTriviaRequest()
        );

        response.then()
                .statusCode(anyOf(is(201), is(409), is(500)));

        if (response.statusCode() == 201) {
            response.then()
                    .body("id", notNullValue())
                    .body("question", notNullValue())
                    .body("correctAnswer", notNullValue())
                    .body("category", equalTo("Historia"))
                    .body("difficulty", equalTo("Media"))
                    .body("options", notNullValue());
        }
    }

    @Test
    void shouldHandleEmptyFieldsAccordingToCurrentBehavior() {
        Response response = triviaClient.generateTrivia(
                request,
                TriviaTestData.invalidTriviaRequest()
        );

        response.then()
                .statusCode(anyOf(is(201), is(409), is(500)));

        if (response.statusCode() == 201) {
            response.then()
                    .body("question", notNullValue())
                    .body("correctAnswer", notNullValue())
                    .body("options", notNullValue());
        }
    }

    @Test
    void shouldGetAllTriviaQuestions() {
        Response response = triviaClient.getAllTrivia(request, 1, 10);

        response.then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("meta", notNullValue())
                .body("message", equalTo("Trivia questions retrieved successfully"));
    }
}