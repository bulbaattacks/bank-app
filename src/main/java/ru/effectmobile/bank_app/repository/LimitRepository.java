package ru.effectmobile.bank_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.effectmobile.bank_app.entity.Limit;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface LimitRepository extends JpaRepository<Limit, Long> {
    Optional<Limit> findByCardId(Long cardId);
}
