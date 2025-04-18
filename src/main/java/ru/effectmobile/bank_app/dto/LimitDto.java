package ru.effectmobile.bank_app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitDto {
    @NotNull
    private Long cardId;
    private Long dailyLimit;
    private Long monthlyLimit;
}
