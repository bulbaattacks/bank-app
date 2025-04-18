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

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EncryptionServiceDes encryptionService;
    private final TransactionService transactionService;

    public List<CardDto> getAllCards() {
        return cardRepository.findAllByIsAtmFalse().stream()
                .map(this::buildDto)
                .toList();
    }

    public List<CardDto> getCardsByUserId(Long userId) {
        return cardRepository.getAllByUserId(userId).stream()
                .map(this::buildDto)
                .toList();
    }

    public CardDto createCard(CardDto dto) {
        var userId = dto.getOwnerId();
        var user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(userId));

        dto.setNumber(encryptionService.encrypt(dto.getNumber()));
        var card = Card.builder()
                .number(dto.getNumber())
                .user(user)
                .validityPeriod(LocalDate.now().plusYears(1))
                .status(dto.getStatus())
                .build();
        cardRepository.save(card);

        dto.setId(card.getId());
        dto.setValidityPeriod(card.getValidityPeriod());
        dto.setNumber(encryptionService.decrypt(card.getNumber()));
        dto.setBalance(transactionService.getBalanceFromCache(dto.getId()));
        return dto;
    }


    public void updateStatus(Long id, Card.Status status) {
        var updateResult = cardRepository.updateStatus(id, status);
        if (updateResult == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void deleteStatus(Long id) {
        var card = cardRepository.findByIdAndIsAtmFalse(id).orElseThrow(() -> new EntityNotFoundException(id));
        cardRepository.delete(card);
    }

    private CardDto buildDto(Card e) {
        e.setNumber(encryptionService.decrypt(e.getNumber()));
        var dto = CardDto.map(e);
        dto.setBalance(transactionService.getBalanceFromCache(dto.getId()));
        return dto;
    }
}
