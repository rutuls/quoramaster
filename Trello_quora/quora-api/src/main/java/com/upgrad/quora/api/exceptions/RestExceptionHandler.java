package com.upgrad.quora.api.exceptions;

import com.upgrad.quora.api.models.ErrorResponse;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> userSignupError(SignOutRestrictedException r, WebRequest request){
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(r.getCode()).message(r.getErrorMessage()), HttpStatus.FORBIDDEN);
    }
}
