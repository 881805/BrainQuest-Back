package com.project.demo.funcional;

public class AuthTestData {

    public static String validLogin() {
        return """
        {
          "email": "super.admin@gmail.com",
          "password": "superadmin123"
        }
        """;
    }

    public static String invalidLogin() {
        return """
        {
          "email": "super.admin@gmail.com",
          "password": "wrongpassword"
        }
        """;
    }

    public static String validSignup() {
        return """
        {
          "email": "user%s@gmail.com",
          "password": "123456"
        }
        """.formatted(System.currentTimeMillis());
    }

    public static String duplicateSignup() {
        return """
        {
          "email": "super.admin@gmail.com",
          "password": "123456"
        }
        """;
    }
}