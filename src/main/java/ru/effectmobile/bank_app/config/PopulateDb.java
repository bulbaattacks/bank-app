package ru.effectmobile.bank_app.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.effectmobile.bank_app.entity.Card;
import ru.effectmobile.bank_app.entity.User;
import ru.effectmobile.bank_app.repository.CardRepository;
import ru.effectmobile.bank_app.repository.UserRepository;
import ru.effectmobile.bank_app.service.EncryptionServiceDes;

import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PopulateDb {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CardRepository cardRepository;
    private final EncryptionServiceDes encryptionService;

    @PostConstruct
    private void populateUserAndCardTable() {
        var defaultAdmin = User.builder()
                .email("admin")
                .password(passwordEncoder.encode("admin"))
                .role(User.Role.ADMIN)
                .build();
        var defaultUser = User.builder()
                .email("user")
                .password(passwordEncoder.encode("user"))
                .role(User.Role.USER)
                .build();
        var defaultAtm = User.builder()
                .email("atm")
                .password(passwordEncoder.encode("atm"))
                .role(User.Role.ADMIN)
                .build();
        var admin = userRepository.findByEmail(defaultAdmin.getEmail()).orElse(defaultAdmin);
        var user = userRepository.findByEmail(defaultUser.getEmail()).orElse(defaultUser);
        var atm = userRepository.findByEmail(defaultAtm.getEmail()).orElse(defaultAtm);
        userRepository.saveAll(List.of(admin, user, atm));

        var defaultAtmCard = Card.builder()
                .number(encryptionService.encrypt("0000111100001111"))
                .user(atm)
                .validityPeriod(LocalDate.now().plusYears(100))
                .status(Card.Status.ACTIVE)
                .isAtm(true)
                .build();
        var atmCard = cardRepository.findByNumber(defaultAtmCard.getNumber()).orElse(defaultAtmCard);
        cardRepository.save(atmCard);
    }
}
