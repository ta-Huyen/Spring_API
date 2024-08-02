package com.example.spring.exception;

import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

public class InvalidJwtAuthenticationException extends AuthenticationException {

    @Serial
    private static final long serialVersionUID = -761503632186596342L;

    public InvalidJwtAuthenticationException(String e) {
        super(e);
    }
}