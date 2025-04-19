package ru.effectmobile.bank_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectmobile.bank_app.dto.LimitDto;
import ru.effectmobile.bank_app.entity.Card;
import ru.effectmobile.bank_app.entity.Limit;
import ru.effectmobile.bank_app.exception.CardNotFoundException;
import ru.effectmobile.bank_app.repository.CardRepository;
import ru.effectmobile.bank_app.repository.LimitRepository;

@Service
@RequiredArgsConstructor
public class LimitService {

    private final CardRepository cardRepository;
    private final LimitRepository limitRepository;

    public void addLimit(LimitDto dto) {
        Card cardToLimit = cardRepository.findByIdAndIsAtmFalse(dto.getCardId())
                .orElseThrow(() -> new CardNotFoundException(dto.getCardId()));
        Limit limit = Limit.builder()
                .card(cardToLimit)
                .dailyLimit(dto.getDailyLimit())
                .monthlyLimit(dto.getMonthlyLimit())
                .build();
        limitRepository.save(limit);
    }
}
