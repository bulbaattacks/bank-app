package ru.effectmobile.bank_app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import ru.effectmobile.bank_app.TestConfig;
import ru.effectmobile.bank_app.dto.CardDto;
import ru.effectmobile.bank_app.entity.Card;
import ru.effectmobile.bank_app.exception.CardNotFoundException;
import ru.effectmobile.bank_app.exception.UserNotFoundException;
import ru.effectmobile.bank_app.repository.CardRepository;
import ru.effectmobile.bank_app.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EncryptionServiceDes encryptionService;
    @Mock
    private TransactionService transactionService;
    @InjectMocks
    private CardService service;

    @Test
    void getCardsByUserId_throw_ex_user_not_found() {
        Long userId = 1L;
        Exception exception = assertThrows(UserNotFoundException.class, () -> service.getCardsByUserId(userId));
        String expected = UserNotFoundException.MSG.formatted(userId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }

    @Test
    void createCard_throw_ex_user_not_found() {
        Long userId = 1L;
        CardDto dto = new CardDto();
        dto.setOwnerId(userId);
        Exception exception = assertThrows(UserNotFoundException.class, () -> service.createCard(dto));
        String expected = UserNotFoundException.MSG.formatted(userId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }

    @Test
    void updateStatus_throw_ex_card_not_found() {
        Long cardId = 1L;
        Card.Status status = Card.Status.ACTIVE;
        Exception exception = assertThrows(CardNotFoundException.class, () -> service.updateStatus(cardId, status));
        String expected = CardNotFoundException.MSG.formatted(cardId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));

    }

    @Test
    void deleteCard() {
        Long cardId = 1L;
        Exception exception = assertThrows(CardNotFoundException.class, () -> service.deleteCard(cardId));
        String expected = CardNotFoundException.MSG.formatted(cardId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));
    }
}
