package ru.effectmobile.bank_app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import ru.effectmobile.bank_app.TestConfig;
import ru.effectmobile.bank_app.dto.LimitDto;
import ru.effectmobile.bank_app.exception.CardNotFoundException;
import ru.effectmobile.bank_app.repository.CardRepository;
import ru.effectmobile.bank_app.repository.LimitRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
class LimitServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private LimitRepository limitRepository;

    @InjectMocks
    private LimitService service;

    @Test
    void addLimit_throw_ex_card_not_found() {
        Long cardId = 1L;
        LimitDto dto = new LimitDto();
        dto.setCardId(cardId);
        Exception exception = assertThrows(CardNotFoundException.class, () -> service.addLimit(dto));
        String expected = CardNotFoundException.MSG.formatted(cardId);
        String actual = exception.getMessage();
        assertTrue(actual.contains(expected));

    }
}
