package ru.effectmobile.bank_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import ru.effectmobile.bank_app.entity.Card;

import java.time.LocalDate;

@Setter
@Getter
public class CardDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank
    @Pattern(regexp="^[0-9]{16,16}$")
    private String number;
    @NotNull
    private Long ownerId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate validityPeriod;
    @NotNull
    private Card.Status status;

    public static CardDto map(Card entity) {
        var dto = new CardDto();
        dto.setId(entity.getId());
        dto.setNumber(entity.getNumber());
        dto.setOwnerId(entity.getUser().getId());
        dto.setValidityPeriod(entity.getValidityPeriod());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}