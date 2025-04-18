package ru.effectmobile.bank_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectmobile.bank_app.dto.DepositDto;
import ru.effectmobile.bank_app.dto.TransactionDto;
import ru.effectmobile.bank_app.entity.Card;
import ru.effectmobile.bank_app.entity.Transaction;
import ru.effectmobile.bank_app.exception.*;
import ru.effectmobile.bank_app.repository.CardRepository;
import ru.effectmobile.bank_app.repository.TransactionCacheRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CardRepository cardRepository;
    private final TransactionCacheRepository transactionRepository;

    public List<TransactionDto> getAllTransactionHistory() {
        return transactionRepository.findAll().stream()
                .map(entity -> {
                    var dto = new TransactionDto();
                    dto.setFromCardId(entity.getFromCard().getId());
                    dto.setToCardId(entity.getToCard().getId());
                    dto.setAmount(entity.getAmount());
                    dto.setEmail(entity.getUser().getEmail());
                    return dto;
                })
                .toList();
    }

    public List<TransactionDto> getTransactionHistoryByUserId(Long userId) {
        return transactionRepository.findAllByUserId(userId).stream()
                .map(entity -> {
                    var dto = new TransactionDto();
                    dto.setFromCardId(entity.getFromCard().getId());
                    dto.setToCardId(entity.getToCard().getId());
                    dto.setAmount(entity.getAmount());
                    dto.setEmail(entity.getUser().getEmail());
                    return dto;
                })
                .toList();
    }

    public void createTransaction(TransactionDto dto, Long ownerId) {
        var fromCard = cardRepository.findByIdAndUserId(dto.getFromCardId(), ownerId)
                .orElseThrow(() -> new CardOwnerException(dto.getFromCardId(), ownerId));
        var toCard = cardRepository.findByIdAndUserId(dto.getToCardId(), ownerId)
                .orElseThrow(() -> new CardOwnerException(dto.getToCardId(), ownerId));

        cardIsActive(fromCard);
        cardIsActive(toCard);

        var balanceFromCard = getBalanceFromCache(fromCard.getId());
        if (balanceFromCard - dto.getAmount() < 0) {
            throw new InsufficientFundsException(fromCard.getId());
        }

        var tx = Transaction.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(dto.getAmount())
                .user(fromCard.getUser())
                .build();
        transactionRepository.save(tx, fromCard.getId(), toCard.getId());
    }

    public void deposit(DepositDto dto) {
        var toCard = cardRepository.findByIdAndIsAtmFalse(dto.getToCardId())
                .orElseThrow(() -> new EntityNotFoundException(dto.getToCardId()));
        cardIsActive(toCard);
        var atm = cardRepository.findFirstByIsAtmTrue().orElseThrow(ATMNotFoundException::new);
        var tx = Transaction.builder()
                .fromCard(atm)
                .toCard(toCard)
                .amount(dto.getAmount())
                .user(toCard.getUser())
                .build();
        transactionRepository.save(tx, toCard.getId());
    }

    public Long getBalanceFromCache(Long cardId) {
        return transactionRepository.getBalanceFromCache(cardId);
    }

    private void cardIsActive(Card card) {
        if (card.getStatus() != Card.Status.ACTIVE) {
            throw new CardStatusException(card.getId());
        }
    }
}
