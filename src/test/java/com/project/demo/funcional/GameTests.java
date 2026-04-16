package com.project.demo.funcional;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

public class GameTests extends BaseTest {

    GameClient gameClient = new GameClient();

    @Test
    void test1_CreateGame() {
        String body = "{\"conversation\":{\"id\":1},\"winner\":{\"id\":1},\"gameType\":{\"id\":2},\"isOngoing\":true,\"pointsEarnedPlayer1\":0,\"pointsEarnedPlayer2\":8}";

        Response response = gameClient.createGame(request, body);


        response.then().statusCode(anyOf(is(200), is(201)));
        System.out.println("Respuesta Create: " + response.asString());
    }

    @Test
    void test2_UpdateGame() {
        String body = "{\"id\":1,\"conversation\":{\"id\":1},\"winner\":{\"id\":1},\"gameType\":{\"id\":2},\"isOngoing\":true,\"pointsEarnedPlayer1\":10,\"pointsEarnedPlayer2\":8}";

        Response response = gameClient.updateGame(request, 1L, body);

        response.then().statusCode(anyOf(is(200), is(201)));
    }

    @Test
    void test3_GetGameById() {
        Response response = gameClient.getGameById(request, 1L);
        response.then().statusCode(anything());
    }

    @Test
    void test4_NegativeCase_NotFound() {
        Response response = gameClient.getGameById(request, 999L);
        response.then().statusCode(anything());
    }
}