package br.com.meli.service_schedule.exception;

import org.springframework.security.access.AccessDeniedException;

public class BadCredentialException extends AccessDeniedException {
    public BadCredentialException(String message) {
        super(message);
    }
}
