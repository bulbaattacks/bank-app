package ru.effectmobile.bank_app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositDto {
    @NotNull
    private Long toCardId;
    @NotNull
    private Long amount;
}
