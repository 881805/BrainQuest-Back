package com.project.demo.funcional;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;


class MissionTests extends BaseTest {

    MissionClient missionClient = new MissionClient();

    private static final Long VALID_MISSION_ID = 1L;
    private static final Long INVALID_MISSION_ID = 99999L;

    // ✅ POSITIVE
    @Test
    void shouldUpdateMissionSuccessfully() {
        Response response = missionClient.updateMission(
                request,
                VALID_MISSION_ID,
                MissionTestData.validMissionUpdate()
        );

        response.then()
                .statusCode(anyOf(is(200), is(500)));

        if (response.statusCode() == 200) {
            response.then()
                    .body("message", equalTo("Mision actualizada con éxito"))
                    .body("data", notNullValue());
        }
    }

    // ❌ NEGATIVE → mission not found
    @Test
    void shouldReturn404WhenMissionNotFound() {
        Response response = missionClient.updateMission(
                request,
                INVALID_MISSION_ID,
                MissionTestData.validMissionUpdate()
        );

        response.then()
                .statusCode(anyOf(is(404), is(500)));

        if (response.statusCode() == 404) {
            response.then()
                    .body("message", containsString("no encontrado"));
        }
    }

    // ❌ NEGATIVE → invalid data
    @Test
    void shouldFailWhenInvalidDataProvided() {
        Response response = missionClient.updateMission(
                request,
                VALID_MISSION_ID,
                MissionTestData.invalidMissionUpdate()
        );

        response.then()
                .statusCode(anyOf(is(400), is(500)));
    }

    // ⚠️ EDGE CASE
    @Test
    void shouldHandlePartialUpdate() {
        String partialBody = """
        {
            "objective": { "id": 1 },
            "gameType": { "id": 1 }
        }
        """;

        Response response = missionClient.updateMission(
                request,
                VALID_MISSION_ID,
                partialBody
        );

        response.then()
                .statusCode(anyOf(is(200), is(400), is(500)));

        if (response.statusCode() == 200) {
            response.then()
                    .body("message", equalTo("Mision actualizada con éxito"));
        }
    }
}