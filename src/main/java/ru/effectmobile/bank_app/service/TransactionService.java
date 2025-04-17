package ru.effectmobile.bank_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectmobile.bank_app.dto.DepositDto;
import ru.effectmobile.bank_app.dto.TransactionDto;
import ru.effectmobile.bank_app.entity.Transaction;
import ru.effectmobile.bank_app.repository.CardRepository;
import ru.effectmobile.bank_app.repository.TransactionRepository;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    private final ConcurrentHashMap<Long, Long> cardBalanceCache = new ConcurrentHashMap<>();

    public void createTransaction(TransactionDto dto, Long ownerId) {
        var fromCard = cardRepository.findByIdAndUserId(dto.getFromCard(), ownerId).orElseThrow();
        var toCard = cardRepository.findByIdAndUserId(dto.getToCard(), ownerId).orElseThrow();

        var balanceFromCard = getBalanceFromCache(fromCard.getId());
        if (balanceFromCard - dto.getAmount() < 0) {
            throw new RuntimeException("Not enough money on card");
        }

       var tx = Transaction.builder()
               .fromCard(fromCard)
               .toCard(toCard)
               .amount(dto.getAmount())
               .build();
        transactionRepository.save(tx);

        findInDbAndUpdateCache(fromCard.getId());
        findInDbAndUpdateCache(toCard.getId());
    }

    public void deposit(DepositDto dto) {
        var toCard = cardRepository.findByIdAndIsAtmFalse(dto.getToCardId()).orElseThrow();
        var atm = cardRepository.findFirstByIsAtmTrue().orElseThrow();
        var tx = Transaction.builder()
                .fromCard(atm)
                .toCard(toCard)
                .amount(dto.getAmount())
                .build();
        transactionRepository.save(tx);
        findInDbAndUpdateCache(toCard.getId());
    }

    public Long getBalanceFromCache(Long cardId) {
        var balance = cardBalanceCache.get(cardId);
        return balance != null ? balance : findInDbAndUpdateCache(cardId);
    }

    private Long findInDbAndUpdateCache(Long cardId) {
        var balance = getBalance(cardId);
        cardBalanceCache.put(cardId, balance);
        return balance;
    }

    private Long getBalance(Long cardId) {
        var depositAmount = transactionRepository.countDepositAmount(cardId);
        var withdrawAmount = transactionRepository.countWithdrawAmount(cardId);
        return depositAmount - withdrawAmount;
    }
}
