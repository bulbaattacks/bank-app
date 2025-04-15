package ru.effectmobile.bank_app.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.effectmobile.bank_app.dto.CardDto;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Setter(AccessLevel.NONE)
    private LocalDate validityPeriod;
    @Enumerated(EnumType.STRING)
    private Status status;

    public void setValidityPeriod() {
        this.validityPeriod = LocalDate.now().plusYears(1);
    }

    public static Card map(CardDto dto, User user) {
        var card = new Card();
        card.setNumber(dto.getNumber());
        card.setUser(user);
        card.setValidityPeriod();
        card.setStatus(dto.getStatus());
        return card;
    }

    public enum Status {ACTIVE, BLOCKED, EXPIRED}

}
