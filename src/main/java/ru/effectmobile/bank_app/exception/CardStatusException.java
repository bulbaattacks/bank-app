package ru.effectmobile.bank_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CardStatusException extends ResponseStatusException {
    public static final String MSG = "Card id: %d, status can not be set manually";

    public CardStatusException(Long id) {
        super(HttpStatus.BAD_REQUEST, MSG.formatted(id));
    }
}