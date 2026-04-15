package com.project.demo.funcional;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AuthClient {

    public Response login(RequestSpecification request, String body) {
        return request
                .body(body)
                .when()
                .post("/auth/login");
    }

    public Response signup(RequestSpecification request, String body) {
        return request
                .body(body)
                .when()
                .post("/auth/signup");
    }
}