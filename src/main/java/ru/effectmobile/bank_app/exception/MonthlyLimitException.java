package ru.effectmobile.bank_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MonthlyLimitException extends ResponseStatusException {
    public static final String MSG = "Monthly limit exceeded";

    public MonthlyLimitException() {
        super(HttpStatus.BAD_REQUEST, MSG);
    }
}