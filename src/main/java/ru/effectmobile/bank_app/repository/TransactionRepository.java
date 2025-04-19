package ru.effectmobile.bank_app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.effectmobile.bank_app.entity.Transaction;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("select COALESCE(sum(t.amount), 0) from Transaction t where t.toCard.id = :cardId")
    long countDepositAmount(@Param(value = "cardId") Long cardId);

    @Query("select COALESCE(sum(t.amount), 0) from Transaction t where t.fromCard.id = :cardId")
    long countWithdrawAmount(@Param(value = "cardId") Long cardId);

    @Query("select COALESCE(sum(t.amount), 0) from Transaction t where t.date = :date and t.fromCard.id = :cardId")
    long countDailyWithdraw(@Param(value = "date") LocalDate date, @Param(value = "cardId") Long cardId);

    @Query("select COALESCE(sum(t.amount), 0) from Transaction t " +
            "where t.date between :start and :end " +
            "and t.fromCard.id = :cardId")
    long countMonthlyWithdraw(@Param(value = "start") LocalDate start,
                              @Param(value = "end") LocalDate end,
                              @Param(value = "cardId") Long cardId);

    @Query("select t from Transaction t where t.user.id = :userId and " +
            "(:amountFilter is null or t.amount >= :amountFilter)")
    List<Transaction> findAllByUserId(Long userId, Pageable pageable, Long amountFilter);

    @Query("select t from Transaction t where " +
            "(:amountFilter is null or t.amount >= :amountFilter)")
    List<Transaction> findAllByAmount(Pageable pageable, Long amountFilter);
}
