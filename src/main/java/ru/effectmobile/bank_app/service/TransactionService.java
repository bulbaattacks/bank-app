package ru.effectmobile.bank_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.effectmobile.bank_app.dto.DepositDto;
import ru.effectmobile.bank_app.dto.TransactionDto;
import ru.effectmobile.bank_app.entity.Card;
import ru.effectmobile.bank_app.entity.Limit;
import ru.effectmobile.bank_app.entity.Transaction;
import ru.effectmobile.bank_app.exception.*;
import ru.effectmobile.bank_app.repository.CardRepository;
import ru.effectmobile.bank_app.repository.LimitRepository;
import ru.effectmobile.bank_app.repository.TransactionCacheRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CardRepository cardRepository;
    private final TransactionCacheRepository transactionRepository;
    private final LimitRepository limitRepository;

    public List<TransactionDto> getAllTransactionHistory(Pageable pageable, Long amountFilter) {
        return transactionRepository.findAll(pageable, amountFilter).stream()
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

    public List<TransactionDto> getTransactionHistoryByUserId(Long userId, Pageable pageable, Long amountFilter) {
        return transactionRepository.findAllByUserId(userId, pageable, amountFilter).stream()
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

        checkLimits(fromCard);

        var balanceFromCard = getBalanceFromCache(fromCard.getId());
        if (balanceFromCard - dto.getAmount() < 0) {
            throw new InsufficientFundsException(fromCard.getId());
        }

        var tx = Transaction.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(dto.getAmount())
                .user(fromCard.getUser())
                .date(LocalDate.now())
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
                .date(LocalDate.now())
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

    private void checkLimits(Card card) {
        Optional<Limit> limitOpt = limitRepository.findByCardId(card.getId());
        if (limitOpt.isEmpty()) return;

        var limit = limitOpt.get();
        var dailyLimit = limit.getDailyLimit();
        var monthlyLimit = limit.getMonthlyLimit();

        var date = LocalDate.now();
        if (dailyLimit != null) {
            var dailyWithdraw = transactionRepository.countDailyWithdraw(date, card.getId());
            if (dailyWithdraw >= dailyLimit) {
                throw new DailyLimitException();
            }
        }

        if (monthlyLimit != null) {
            var monthlyWithdraw = transactionRepository.countMonthlyWithdraw(date, card.getId());
            if (monthlyWithdraw >= monthlyLimit) {
                throw new MonthlyLimitException();
            }
        }
    }
}
