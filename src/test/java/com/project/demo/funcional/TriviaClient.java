package com.project.demo.funcional;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TriviaClient {

    public Response generateTrivia(RequestSpecification request, String body) {
        return request
                .body(body)
                .when()
                .post("/trivia/generate");
    }

    public Response getAllTrivia(RequestSpecification request, int page, int size) {
        return request
                .queryParam("page", page)
                .queryParam("size", size)
                .when()
                .get("/trivia");
    }
}