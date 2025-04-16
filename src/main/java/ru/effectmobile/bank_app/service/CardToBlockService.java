package ru.effectmobile.bank_app.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectmobile.bank_app.entity.Card;
import ru.effectmobile.bank_app.entity.CardToBlock;
import ru.effectmobile.bank_app.exception.EntityNotFoundException;
import ru.effectmobile.bank_app.repository.CardRepository;
import ru.effectmobile.bank_app.repository.CardToBlockRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardToBlockService {

    private final CardToBlockRepository cardToBlockRepository;
    private final CardRepository cardRepository;

    @PostConstruct
    public void blockCards() {
        List<Card> cardsToBlock = cardToBlockRepository.findAll().stream().map(CardToBlock::getCard).toList();
        cardsToBlock.forEach(card -> card.setStatus(Card.Status.BLOCKED));
        cardRepository.saveAll(cardsToBlock);
        cardToBlockRepository.deleteAll();
    }

    public void addCardToBlock(Long cardId, Long ownerId) {
        Card cardToBlock = cardRepository.findByIdAndUserId(cardId, ownerId)
                .orElseThrow(() -> new EntityNotFoundException(cardId));
        CardToBlock entity = new CardToBlock();
        entity.setCard(cardToBlock);
        cardToBlockRepository.save(entity);
    }
}
