package com.project.demo.funcional;

public class TypingTestData {

    public static String validTypingRequest() {
        return """
        {
          "category": "Mecanografía",
          "difficulty": "Fácil"
        }
        """;
    }

    public static String invalidTypingRequest() {
        return """
        {
          "category": "",
          "difficulty": ""
        }
        """;
    }
}