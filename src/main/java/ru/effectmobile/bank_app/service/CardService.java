package ru.effectmobile.bank_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.effectmobile.bank_app.dto.CardDto;
import ru.effectmobile.bank_app.entity.Card;
import ru.effectmobile.bank_app.exception.EntityNotFoundException;
import ru.effectmobile.bank_app.repository.CardRepository;
import ru.effectmobile.bank_app.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardDto createCard(CardDto dto) {
        var userId = dto.getOwnerId();
        var user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(userId));
        var card = Card.map(dto, user);
        cardRepository.save(card);

        dto.setId(card.getId());
        dto.setValidityPeriod(card.getValidityPeriod());
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

    public void updateStatus(Long id, Card.Status status) {
        var updateResult = cardRepository.updateStatus(id, status);
        if (updateResult == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void deleteStatus(Long id) {
        var card = cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        cardRepository.delete(card);
    }
}
