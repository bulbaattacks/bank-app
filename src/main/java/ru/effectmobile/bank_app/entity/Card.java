package ru.effectmobile.bank_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDate validityPeriod;
    @Setter
    @Enumerated(EnumType.STRING)
    private Status status;
    private boolean isAtm;

    public enum Status {ACTIVE, BLOCKED, EXPIRED}
}
