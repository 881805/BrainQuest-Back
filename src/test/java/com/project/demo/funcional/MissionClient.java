package com.project.demo.funcional;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class MissionClient {

    public Response updateMission(RequestSpecification request, Long missionId, String body) {
        return request
                .body(body)
                .when()
                .put("/missions/" + missionId);
    }
}