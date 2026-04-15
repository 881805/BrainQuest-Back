package com.project.demo.funcional;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    protected RequestSpecification request;
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlci5hZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3NzQ0ODY5NDksImV4cCI6NDkyODA4Njk0OX0.laFb-BM_ImWQeUmNM0QHQfjJGFuqPuW4nMLdxfU1gRo";

    private static final String BASE_URL = "http://localhost:8080"; // cambia si usas otro puerto
    @BeforeEach
    void setup() {
        request = RestAssured.given()
                .baseUri(BASE_URL) .header("Authorization", "Bearer " + TOKEN)
                .contentType(ContentType.JSON);
    }
}