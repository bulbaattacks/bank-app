package ru.effectmobile.bank_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectmobile.bank_app.dto.CardDto;
import ru.effectmobile.bank_app.entity.Card;
import ru.effectmobile.bank_app.repository.CardRepository;
import ru.effectmobile.bank_app.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardDto createCard(CardDto dto) {
        var user = userRepository.findById(dto.getOwnerId()).orElseThrow();
        var card = Card.builder()
                .number(dto.getNumber())
                .user(user)
                .validityPeriod(dto.getValidityPeriod())
                .status(dto.getStatus())
                .build();
        cardRepository.save(card);

        dto.setId(card.getId());
        return dto;
    }

    public List<CardDto> getAllCards() {
        return cardRepository.findAll().stream()
                .map(CardDto::map)
                .toList();
    }

    public List<CardDto> getCardsByUserId(Long userId) {
        return cardRepository.getAllByUserId(userId).stream()
                .map(CardDto::map)
                .toList();
    }
}
