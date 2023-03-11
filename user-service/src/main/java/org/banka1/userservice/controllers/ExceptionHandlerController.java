package org.banka1.userservice.controllers;

import org.banka1.userservice.domains.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public Map<String, String> handleBadRequestException(BadRequestException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return error;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public Map<String, String> handleForbiddenException(ForbiddenException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return error;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerError.class)
    public Map<String, String> handleInternalServerError(InternalServerError exception) {
        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return error;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundExceptions.class)
    public Map<String, String> handleNotFoundException(NotFoundExceptions exception) {
        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public Map<String, String> handleValidationException(ValidationException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("message", exception.getMessage());

        return error;
    }

}
