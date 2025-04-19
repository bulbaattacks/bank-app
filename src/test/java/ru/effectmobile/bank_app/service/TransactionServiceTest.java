package ru.effectmobile.bank_app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import ru.effectmobile.bank_app.TestConfig;
import ru.effectmobile.bank_app.dto.DepositDto;
import ru.effectmobile.bank_app.dto.TransactionDto;
import ru.effectmobile.bank_app.entity.Card;
import ru.effectmobile.bank_app.entity.Limit;
import ru.effectmobile.bank_app.exception.*;
import ru.effectmobile.bank_app.repository.CardRepository;
import ru.effectmobile.bank_app.repository.LimitRepository;
import ru.effectmobile.bank_app.repository.TransactionCacheRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private TransactionCacheRepository transactionRepository;
    @Mock
    private LimitRepository limitRepository;

    @InjectMocks
    private TransactionService service;

    @Test
    void createTransaction_throw_ex_card_owner_not_found() {
        Long ownerId = 1L;
        String email = "email";
        Long fromCardId = 1L;
        Long toCardId = 1L;
        TransactionDto dto = new TransactionDto();
        dto.setEmail(email);
        dto.setFromCardId(fromCardId);
        dto.setToCardId(toCardId);
        Exception exception = assertThrows(CardOwnerException.class, () -> service.createTransaction(dto, ownerId));
        String expected = CardOwnerException.MSG.formatted(fromCardId, ownerId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }

    @Test
    void createTransaction_throw_ex_daily_limit() {
        Long ownerId = 1L;
        Long fromCardId = 1L;
        Long toCardId = 1L;
        TransactionDto dto = new TransactionDto();
        dto.setFromCardId(fromCardId);
        dto.setToCardId(toCardId);

        Card fromCard = Card.builder()
                .id(fromCardId)
                .status(Card.Status.ACTIVE)
                .build();
        Card toCard = Card.builder()
                .id(toCardId)
                .status(Card.Status.ACTIVE)
                .build();
        Limit limit = Limit.builder()
                .dailyLimit(100L)
                .build();

        when(cardRepository.findByIdAndUserId(any(), any()))
                .thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserId(any(), any()))
                .thenReturn(Optional.of(toCard));
        when(limitRepository.findByCardId(any()))
                .thenReturn(Optional.of(limit));
        when(transactionRepository.countDailyWithdraw(any(), any()))
                .thenReturn(100L);

        Exception exception = assertThrows(DailyLimitException.class, () -> service.createTransaction(dto, ownerId));
        String expected = DailyLimitException.MSG;
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }

    @Test
    void createTransaction_throw_ex_monthly_limit() {
        Long ownerId = 1L;
        Long fromCardId = 1L;
        Long toCardId = 1L;
        TransactionDto dto = new TransactionDto();
        dto.setFromCardId(fromCardId);
        dto.setToCardId(toCardId);

        Card fromCard = Card.builder()
                .id(fromCardId)
                .status(Card.Status.ACTIVE)
                .build();
        Card toCard = Card.builder()
                .id(toCardId)
                .status(Card.Status.ACTIVE)
                .build();
        Limit limit = Limit.builder()
                .monthlyLimit(100L)
                .build();

        when(cardRepository.findByIdAndUserId(any(), any()))
                .thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserId(any(), any()))
                .thenReturn(Optional.of(toCard));
        when(limitRepository.findByCardId(any()))
                .thenReturn(Optional.of(limit));
        when(transactionRepository.countMonthlyWithdraw(any(), any()))
                .thenReturn(100L);

        Exception exception = assertThrows(MonthlyLimitException.class, () -> service.createTransaction(dto, ownerId));
        String expected = MonthlyLimitException.MSG;
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }

    @Test
    void createTransaction_throw_ex_insufficient_funds() {
        Long ownerId = 1L;
        Long fromCardId = 1L;
        Long toCardId = 1L;
        TransactionDto dto = new TransactionDto();
        dto.setFromCardId(fromCardId);
        dto.setToCardId(toCardId);
        dto.setAmount(100L);

        Card fromCard = Card.builder()
                .id(fromCardId)
                .status(Card.Status.ACTIVE)
                .build();
        Card toCard = Card.builder()
                .id(toCardId)
                .status(Card.Status.ACTIVE)
                .build();
        Limit limit = Limit.builder()
                .build();

        when(cardRepository.findByIdAndUserId(any(), any()))
                .thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserId(any(), any()))
                .thenReturn(Optional.of(toCard));
        when(limitRepository.findByCardId(any()))
                .thenReturn(Optional.of(limit));
        when(transactionRepository.getBalanceFromCache(any()))
                .thenReturn(0L);

        Exception exception = assertThrows(InsufficientFundsException.class, () -> service.createTransaction(dto, ownerId));
        String expected = InsufficientFundsException.MSG.formatted(fromCardId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }

    @Test
    void deposit_throw_ex_card_not_found() {
        Long cardId = 1L;
        DepositDto dto = new DepositDto();
        dto.setToCardId(cardId);
        Exception exception = assertThrows(CardNotFoundException.class, () -> service.deposit(dto));
        String expected = CardNotFoundException.MSG.formatted(cardId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }

    @Test
    void deposit_throw_ex_card_not_active() {
        Long cardId = 1L;
        DepositDto dto = new DepositDto();
        dto.setToCardId(cardId);

        Card card = Card.builder()
                .id(cardId)
                .status(Card.Status.BLOCKED)
                .build();

        when(cardRepository.findByIdAndIsAtmFalse(any()))
                .thenReturn(Optional.of(card));

        Exception exception = assertThrows(CardStatusNotActiveException.class, () -> service.deposit(dto));
        String expected = CardStatusNotActiveException.MSG.formatted(cardId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }
}
