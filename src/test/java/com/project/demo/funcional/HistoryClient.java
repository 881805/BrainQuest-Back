package com.project.demo.funcional;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class HistoryClient {

    public Response getHistoryByUserAndGameType(RequestSpecification request, String gameType, Long userId) {
        return request
                .when()
                .get("/history/" + gameType + "/" + userId);
    }
}