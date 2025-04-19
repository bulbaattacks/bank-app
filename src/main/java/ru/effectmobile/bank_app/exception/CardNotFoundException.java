package ru.effectmobile.bank_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CardNotFoundException extends ResponseStatusException {
    public static final String MSG = "Card id: %d, not found";

    public CardNotFoundException(Long id) {
        super(HttpStatus.BAD_REQUEST, MSG.formatted(id));
    }
}