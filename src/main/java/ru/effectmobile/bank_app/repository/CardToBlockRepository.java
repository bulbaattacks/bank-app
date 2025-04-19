package ru.effectmobile.bank_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effectmobile.bank_app.entity.CardToBlock;

@Repository
public interface CardToBlockRepository extends JpaRepository<CardToBlock, Long> {
}
