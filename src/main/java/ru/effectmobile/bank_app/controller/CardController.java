package ru.effectmobile.bank_app.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.effectmobile.bank_app.dto.CardDto;
import ru.effectmobile.bank_app.entity.Card;
import ru.effectmobile.bank_app.entity.User;
import ru.effectmobile.bank_app.exception.CardStatusException;
import ru.effectmobile.bank_app.service.CardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CardController {

    private final CardService service;

    @GetMapping("/cards")
    @PageableAsQueryParam
    public List<CardDto> getAllCards(@PageableDefault(value = 20, sort = "id", direction = Sort.Direction.ASC)
                                     @Parameter(hidden = true) Pageable pageable,
                                     @RequestParam(required = false) Card.Status statusFilter) {
        return service.getAllCards(pageable, statusFilter);
    }

    @GetMapping("/cards/{userId}")
    public List<CardDto> getCardsByUserId(@PathVariable("userId") Long userId, Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        if (authenticatedUser.getRole() != User.Role.ADMIN && !authenticatedUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return service.getCardsByUserId(userId);
    }

    @PostMapping("/cards")
    public CardDto createCard(@Valid @RequestBody CardDto dto) {
        return service.createCard(dto);
    }

    @PatchMapping("/cards/{id}")
    public void updateStatus(@PathVariable("id") Long id, @NotNull Card.Status status) {
        if (status.equals(Card.Status.EXPIRED)) {
            throw new CardStatusException(id);
        }
        service.updateStatus(id, status);
    }

    @DeleteMapping("/cards/{id}")
    public void deleteStatus(@PathVariable("id") Long id) {
        service.deleteCard(id);
    }
}
