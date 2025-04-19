package ru.effectmobile.bank_app.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.effectmobile.bank_app.entity.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TransactionCacheRepository {

    private final TransactionRepository transactionRepository;
    private final ConcurrentHashMap<Long, Long> cardBalanceCache = new ConcurrentHashMap<>();

    public List<Transaction> findAll(Pageable pageable, Long amountFilter) {
        return transactionRepository.findAllByAmount(pageable, amountFilter);
    }

    public List<Transaction> findAllByUserId(Long userId, Pageable pageable, Long amountFilter) {
        return transactionRepository.findAllByUserId(userId, pageable, amountFilter);
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

    public long countDailyWithdraw(LocalDate date, Long cardId) {
        return transactionRepository.countDailyWithdraw(date, cardId);
    }

    public long countMonthlyWithdraw(LocalDate date, Long cardId) {
        return transactionRepository.countMonthlyWithdraw(date.withDayOfMonth(1), date, cardId);
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
