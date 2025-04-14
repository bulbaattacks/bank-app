package ru.effectmobile.bank_app.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.effectmobile.bank_app.dto.CardDto;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public static class CardBuilder {
        private LocalDate validityPeriod;

        public CardBuilder validityPeriod() {
            this.validityPeriod = LocalDate.now().plusYears(1);
            return this;
        }
    }

    public static Card map(CardDto dto, User user) {
        return Card.builder()
                .number(dto.getNumber())
                .user(user)
                .validityPeriod()
                .status(dto.getStatus())
                .build();
    }

    public enum Status {ACTIVE, BLOCKED, EXPIRED}

}
