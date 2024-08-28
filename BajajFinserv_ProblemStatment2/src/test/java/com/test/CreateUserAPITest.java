package com.test;

import io.restassured.RestAssured;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserAPITest {

    private static final String BASE_URL = "https://bfhldevapigw.healthrx.co.in/automation-campus/create/user";
    private static final String VALID_ROLL_NUMBER = "1";
    private static final String INVALID_ROLL_NUMBER = "999";

    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }
    
    @Test
    public void testValidUserCreation() {
        String validUserPayload = "{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"phoneNumber\": 1234567890, \"emailId\": \"john.doe@example.com\" }";
        
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body(validUserPayload)
        .when()
            .post()
        .then()
            .statusCode(equalTo(201))
            .log().all(); // Logs the full response for debugging
    }


    @Test
    public void testDuplicatePhoneNumber() {
        String duplicatePhonePayload = "{ \"firstName\": \"Test1\", \"lastName\": \"Test1\", \"phoneNumber\": 9999999999, \"emailId\": \"test1.test1@test.com\" }";
        
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body(duplicatePhonePayload)
        .when()
            .post()
        .then()
            .statusCode(400);
    }

    @Test
    public void testDuplicateEmailId() {
        String duplicateEmailPayload = "{ \"firstName\": \"Test2\", \"lastName\": \"Test2\", \"phoneNumber\": 8888888888, \"emailId\": \"test.test@test.com\" }";
        
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body(duplicateEmailPayload)
        .when()
            .post()
        .then()
            .statusCode(400);
    }

    @Test
    public void testMissingRollNumber() {
        String payload = "{ \"firstName\": \"Test\", \"lastName\": \"Test\", \"phoneNumber\": 7777777777, \"emailId\": \"test3.test3@test.com\" }";
        
        given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post()
        .then()
            .statusCode(401);
    }

    @Test
    public void testInvalidPhoneNumberLength() {
        String invalidPhonePayload = "{ \"firstName\": \"Test\", \"lastName\": \"Test\", \"phoneNumber\": 123, \"emailId\": \"test4.test4@test.com\" }";
        
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body(invalidPhonePayload)
        .when()
            .post()
        .then()
            .statusCode(400);
    }

    @Test
    public void testInvalidEmailFormat() {
        String invalidEmailPayload = "{ \"firstName\": \"Test\", \"lastName\": \"Test\", \"phoneNumber\": 6666666666, \"emailId\": \"invalid-email\" }";
        
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body(invalidEmailPayload)
        .when()
            .post()
        .then()
            .statusCode(anyOf(equalTo(400), equalTo(500)));
    }


    @Test
    public void testMissingFirstName() {
        String payload = "{ \"lastName\": \"Test\", \"phoneNumber\": 5555555555, \"emailId\": \"test5.test5@test.com\" }";
        
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post()
        .then()
            .statusCode(400);
    }

    @Test
    public void testSQLInjection() {
        String sqlInjectionPayload = "{ \"firstName\": \"Test'; DROP TABLE Users;--\", \"lastName\": \"Test\", \"phoneNumber\": 4444444444, \"emailId\": \"test6.test6@test.com\" }";
        
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body(sqlInjectionPayload)
        .when()
            .post()
        .then()
            .statusCode(400);
    }
}
