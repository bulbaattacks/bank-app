package ru.effectmobile.bank_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DailyLimitException extends ResponseStatusException {
    public static final String MSG = "Daily limit exceeded";

    public DailyLimitException() {
        super(HttpStatus.BAD_REQUEST, MSG);
    }
}