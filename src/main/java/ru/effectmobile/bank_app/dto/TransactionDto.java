package ru.effectmobile.bank_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionDto {
    @NotNull
    private Long fromCardId;
    @NotNull
    private Long toCardId;
    @NotNull
    private Long amount;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;
}
