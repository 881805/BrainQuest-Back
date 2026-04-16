package com.project.demo.funcional;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TypingClient {

    public Response generateTyping(RequestSpecification request, String body) {
        return request
                .body(body)
                .when()
                .post("/typing/generate");
    }

    public Response getAllTyping(RequestSpecification request, int page, int size) {
        return request
                .queryParam("page", page)
                .queryParam("size", size)
                .when()
                .get("/typing/all");
    }
}