package com.project.demo.funcional;

public class GameTestData {

    public static String validGameCreate() {
        return """
        {
          "conversation": { "id": 1 },
          "winner": { "id": 1 },
          "triviaQuestion": { "id": null },
          "gameType": { "id": 2 },
          "isOngoing": true,
          "pointsEarnedPlayer1": 0,
          "pointsEarnedPlayer2": 8,
          "elapsedTurns": 0,
          "maxTurns": 3,
          "expirationTime": null
        }
        """;
    }

    public static String validGameUpdate() {
        return """
        {
          "id": 1,
          "conversation": { "id": 1 },
          "winner": { "id": 1 },
          "triviaQuestion": { "id": null },
          "gameType": { "id": 2 },
          "isOngoing": true,
          "pointsEarnedPlayer1": 10,
          "pointsEarnedPlayer2": 8
        }
        """;
    }
}