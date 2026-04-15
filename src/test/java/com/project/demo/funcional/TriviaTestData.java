package com.project.demo.funcional;

public class TriviaTestData {

    public static String validTriviaRequest() {
        return """
        {
          "category": "Geografía",
          "difficulty": "Fácil"
        }
        """;
    }

    public static String anotherValidTriviaRequest() {
        return """
        {
          "category": "Historia",
          "difficulty": "Media"
        }
        """;
    }

    public static String invalidTriviaRequest() {
        return """
        {
          "category": "",
          "difficulty": ""
        }
        """;
    }
}