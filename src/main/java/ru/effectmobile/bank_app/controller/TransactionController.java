package ru.effectmobile.bank_app.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.effectmobile.bank_app.dto.DepositDto;
import ru.effectmobile.bank_app.dto.TransactionDto;
import ru.effectmobile.bank_app.entity.User;
import ru.effectmobile.bank_app.service.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService service;

    @GetMapping("/transaction_history")
    @PageableAsQueryParam
    public List<TransactionDto> getTransactionHistory(Authentication auth,
                                                      @PageableDefault(value = 20, sort = "date", direction = Sort.Direction.ASC)
                                                      @Parameter(hidden = true) Pageable pageable,
                                                      @RequestParam(required = false) Long amountFilter) {
        var authenticatedUser = ((User) auth.getPrincipal());
        if (authenticatedUser.getRole() == User.Role.ADMIN) {
            return service.getAllTransactionHistory(pageable, amountFilter);
        } else {
            return service.getTransactionHistoryByUserId(authenticatedUser.getId(), pageable, amountFilter);
        }
    }

    @PostMapping("/transaction")
    public void createTransaction(@Valid @RequestBody TransactionDto dto, Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        var ownerId = authenticatedUser.getId();
        service.createTransaction(dto, ownerId);
    }

    @PostMapping("/deposit")
    public void createDeposit(@Valid @RequestBody DepositDto dto) {
        service.deposit(dto);
    }
}
