package com.project.demo.funcional;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class GameClient {

    public Response createGame(RequestSpecification request, String body) {
        return request.body(body).when().post("/games");
    }

    public Response updateGame(RequestSpecification request, Long gameId, String body) {
        return request.body(body).when().put("/games/" + gameId);
    }

    public Response getGameById(RequestSpecification request, Long gameId) {
        return request.when().get("/games/" + gameId);
    }
}