package com.project.demo.funcional;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

class AuthTests extends BaseTest {

    AuthClient authClient = new AuthClient();

    @Test
    void shouldLoginSuccessfully() {
        Response response = authClient.login(
                request,
                AuthTestData.validLogin()
        );

        response.then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("expiresIn", greaterThan(0L))
                .body("authUser.email", equalTo("super.admin@gmail.com"));
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        Response response = authClient.login(
                request,
                AuthTestData.invalidLogin()
        );

        response.then()
                .statusCode(anyOf(is(401), is(403), is(500)));
    }

    @Test
    void shouldSignupSuccessfully() {
        String body = AuthTestData.validSignup();

        Response response = authClient.signup(request, body);

        response.then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("email", notNullValue());
    }


    @Test
    void shouldReturnConflictWhenEmailExists() {
        Response response = authClient.signup(
                request,
                AuthTestData.duplicateSignup()
        );

        response.then()
                .statusCode(409)
                .body(equalTo("Email already in use"));
    }
}