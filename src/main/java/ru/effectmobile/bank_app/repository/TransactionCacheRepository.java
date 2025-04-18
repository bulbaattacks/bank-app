package ru.effectmobile.bank_app.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.effectmobile.bank_app.entity.Transaction;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TransactionCacheRepository {

    private final TransactionRepository transactionRepository;
    private final ConcurrentHashMap<Long, Long> cardBalanceCache = new ConcurrentHashMap<>();

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public List<Transaction> findAllByUserId(Long userId) {
        return transactionRepository.findAllByUserId(userId);
    }


    public void save(Transaction tx, Long fromCardId, Long toCardId) {
        transactionRepository.save(tx);
        findInDbAndUpdateCache(fromCardId);
        findInDbAndUpdateCache(toCardId);
    }

    public void save(Transaction tx, Long toCardId) {
        transactionRepository.save(tx);
        findInDbAndUpdateCache(toCardId);
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
