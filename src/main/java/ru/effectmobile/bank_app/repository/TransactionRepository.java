package ru.effectmobile.bank_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.effectmobile.bank_app.entity.Transaction;

@Repository
@Transactional(readOnly = true)
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("select COALESCE(sum(t.amount), 0) from Transaction t where t.toCard.id = :cardId")
    long countDepositAmount(@Param(value = "cardId") Long cardId);

    @Query("select COALESCE(sum(t.amount), 0) from Transaction t where t.fromCard.id = :cardId")
    long countWithdrawAmount(@Param(value = "cardId") Long cardId);
}
