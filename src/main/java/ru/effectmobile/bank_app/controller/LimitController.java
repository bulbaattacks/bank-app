package ru.effectmobile.bank_app.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.effectmobile.bank_app.dto.LimitDto;
import ru.effectmobile.bank_app.service.LimitService;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class LimitController {

    private final LimitService limitService;

    @PostMapping("/add_limit")
    public void addLimit(@Valid @RequestBody LimitDto dto) {
        limitService.addLimit(dto);
    }
}
