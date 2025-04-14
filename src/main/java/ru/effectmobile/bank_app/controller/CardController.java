package ru.effectmobile.bank_app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.effectmobile.bank_app.dto.CardDto;
import ru.effectmobile.bank_app.entity.Card;
import ru.effectmobile.bank_app.entity.User;
import ru.effectmobile.bank_app.service.CardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CardController {

    private final CardService service;

    @GetMapping("/card")
    public List<CardDto> getAllCards() {
        return service.getAllCards();
    }

    @GetMapping("/card/{userId}")
    public List<CardDto> getCardsByUserId(@PathVariable("userId") Long userId, Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        if (authenticatedUser.getRole() != User.Role.ADMIN && !authenticatedUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return service.getCardsByUserId(userId);
    }

    @PostMapping("/card")
    public CardDto createCard(@RequestBody CardDto dto) {
        return service.createCard(dto);
    }

    @PatchMapping("/card/{id}")
    public void updateStatus(@PathVariable("id") Long id, @NotNull Card.Status status) {
        if (status.equals(Card.Status.EXPIRED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        service.updateStatus(id, status);
    }

    @DeleteMapping("/card/{id}")
    public void deleteStatus(@PathVariable("id") Long id) {
        service.deleteStatus(id);
    }
}
