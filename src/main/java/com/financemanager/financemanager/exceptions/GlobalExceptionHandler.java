package com.financemanager.financemanager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.financemanager.financemanager.DTOs.responses.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 403
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(
        UsernameNotFoundException ex,
        HttpServletRequest req
    ) {
        return build(HttpStatus.FORBIDDEN, "Invalid user: " + ex.getMessage(), req);
    }

    /* 404 – отдельно для каждого типа */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
        NoResourceFoundException ex,
        HttpServletRequest req
    ) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
        UserNotFoundException ex,
        HttpServletRequest req
    ) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(UserWalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWalletNotFound(
        UserWalletNotFoundException ex,
        HttpServletRequest req
    ) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    /* 409 */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
        UserAlreadyExistsException ex,
        HttpServletRequest req
    ) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    /* 400 */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        ValidationException ex,
        HttpServletRequest req
    ) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(NotEnoughFundsException.class)
    public ResponseEntity<ErrorResponse> handleNotEnough(
        NotEnoughFundsException ex,
        HttpServletRequest req
    ) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(
        CategoryNotFoundException ex,
        HttpServletRequest req
    ) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    /* 500 – все остальные */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {}", req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", req);
    }

    /* helper */
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String msg, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                msg,
                req.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}