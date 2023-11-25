package com.tariqkhan051.reviewrover.helpers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tariqkhan051.reviewrover.models.Response;

public class Messages {
        public static class ResponseMessages {
                final static String SUCCESS = "Success";
                final static String ERROR = "Error";

                final static String MISSING_REQUIRED_FIELDS = "Missing required fields.";
                final static String TEAM_CREATED = "Team created successfully.";

                public final static String USER_IS_NOT_LIVE = "Your account is disabled. Please contact admin.";
                public final static String REVIEW_FOR_USER_IS_NOT_LIVE = "Review can't be submitted for the user. Reason: User is disabled.";

                public static ResponseEntity<Object> MissingFieldsResponse() {
                        return new ResponseEntity<Object>(
                                        CreateResponse(
                                                        ERROR,
                                                        MISSING_REQUIRED_FIELDS),
                                        HttpStatus.BAD_REQUEST);
                }

                public static ResponseEntity<Object> ErrorResponse(String message) {
                        return new ResponseEntity<Object>(
                                        CreateResponse(
                                                        ERROR,
                                                        message),
                                        HttpStatus.BAD_REQUEST);
                }

                public static ResponseEntity<Object> ExceptionResponse(String message) {
                        return new ResponseEntity<Object>(
                                        CreateResponse(
                                                        ERROR,
                                                        message),
                                        HttpStatus.INTERNAL_SERVER_ERROR);
                }

                public static ResponseEntity<Object> UnAuthorizedResponse(String message) {
                        return new ResponseEntity<Object>(
                                        CreateResponse(
                                                        ERROR,
                                                        message),
                                        HttpStatus.UNAUTHORIZED);
                }

                public static ResponseEntity<Object> SuccessResponse(String message) {
                        return new ResponseEntity<Object>(
                                        CreateResponse(
                                                        SUCCESS,
                                                        message),
                                        HttpStatus.OK);
                }

                public static ResponseEntity<Object> SuccessResponseData(Object responseObject, String message) {
                        return new ResponseEntity<>(
                                        CreateResponse(responseObject,
                                                        SUCCESS,
                                                        message),
                                        HttpStatus.OK);
                }

                private static Response<Object> CreateResponse(final String status, final String message) {
                        return new Response<Object>(status, message, null);
                }

                private static Response<Object> CreateResponse(Object responseObject, final String status,
                                final String message) {
                        var apiResponse = new Response<Object>(status, message, null);
                        apiResponse.setResponse(responseObject);
                        return apiResponse;
                }
        }
}
