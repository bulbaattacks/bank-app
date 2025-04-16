package ru.effectmobile.bank_app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.effectmobile.bank_app.entity.User;
import ru.effectmobile.bank_app.service.CardToBlockService;

@RestController
@RequestMapping("/card_to_block")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CardToBlockController {

    private final CardToBlockService cardToBlockService;

    @GetMapping("/block")
    public void blockCards() {
        cardToBlockService.blockCards();
    }

    @PostMapping("/add/{cardId}")
    public void addCardToBlock(@PathVariable("cardId") Long cardId, Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        var ownerId = authenticatedUser.getId();
        cardToBlockService.addCardToBlock(cardId, ownerId);
    }
}
