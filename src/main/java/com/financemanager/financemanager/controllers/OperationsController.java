package com.financemanager.financemanager.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financemanager.financemanager.DTOs.internal.BaseWarning;
import com.financemanager.financemanager.DTOs.internal.BudgetLimitExcessedWarning;
import com.financemanager.financemanager.DTOs.internal.PaymentResult;
import com.financemanager.financemanager.DTOs.requests.PaymentRequestDto;
import com.financemanager.financemanager.DTOs.requests.TopUpRequestDto;
import com.financemanager.financemanager.DTOs.requests.TransferRequestDto;
import com.financemanager.financemanager.DTOs.requests.WithdrawRequestDto;
import com.financemanager.financemanager.DTOs.responses.OperationResponseDto;
import com.financemanager.financemanager.entities.CategoryEntity;
import com.financemanager.financemanager.entities.OperationEntity;
import com.financemanager.financemanager.entities.UserEntity;
import com.financemanager.financemanager.entities.WalletEntity;
import com.financemanager.financemanager.exceptions.CategoryNotFoundException;
import com.financemanager.financemanager.exceptions.UserNotFoundException;
import com.financemanager.financemanager.repositories.CategoryRepository;
import com.financemanager.financemanager.services.UserService;
import com.financemanager.financemanager.services.WalletService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
public class OperationsController {

    private final WalletService walletService;
    private final UserService userService;
    private final CategoryRepository categoryRepository;

    /* ---------- общий приватный хелпер ---------- */
    private WalletEntity getUserWallet(String username) {
        UserEntity user = userService.loadUser(username); // бросит UserNotFoundException
        return walletService.getWalletByUser(user);    // бросит UserWalletNotFoundException
    }

    /* ---------- TOP-UP ---------- */
    @PostMapping("/top_up")
    public ResponseEntity<OperationResponseDto> top_up(
        @Valid @RequestBody TopUpRequestDto dto,
        Authentication auth
    ) {
        log.info("Top-up request for user [{}] amount [{}]", auth.getName(), dto.getAmount());
        WalletEntity wallet = getUserWallet(auth.getName());

        OperationEntity op = walletService.top_up(dto.getAmount(), wallet);
        log.info("Top-up completed: {}", op);
        return ResponseEntity.ok(new OperationResponseDto(op));
    }

    /* ---------- WITHDRAW ---------- */
    @PostMapping("/withdraw")
    public ResponseEntity<OperationResponseDto> withdraw(
        @Valid @RequestBody WithdrawRequestDto dto,
        Authentication auth
    ) {
        log.info("Withdraw request for user [{}] amount [{}]", auth.getName(), dto.getAmount());
        WalletEntity wallet = getUserWallet(auth.getName());

        OperationEntity op = walletService.withdraw(dto.getAmount(), wallet);
        log.info("Withdraw completed: {}", op);
        return ResponseEntity.ok(new OperationResponseDto(op));
    }

    /* ---------- PAYMENT ---------- */
    @PostMapping("/payment")
    public ResponseEntity<OperationResponseDto> payment(
        @Valid @RequestBody PaymentRequestDto dto,
        Authentication auth
    ) {
        log.info("Payment request for user [{}] amount [{}] category [{}]",
                auth.getName(), dto.getAmount(), dto.getCategory_name());
        WalletEntity wallet = getUserWallet(auth.getName());

        CategoryEntity category = categoryRepository
                .findByCategoryName(dto.getCategory_name())
                .orElseThrow(() -> new CategoryNotFoundException(dto.getCategory_name()));

        PaymentResult op = walletService.execute_payment(dto.getAmount(), category, wallet);
        List<BaseWarning> budgetWarnings = op.getExceededBudgets().stream()
            .<BaseWarning>map(b -> new BudgetLimitExcessedWarning(b.getBudgetCategory().getCategoryName()))
            .toList();
        log.info("Payment completed: {}", op);
        return ResponseEntity.ok(new OperationResponseDto(op.getOperation(), budgetWarnings));
    }

    /* ---------- TRANSFER ---------- */
    @PostMapping("/transfer")
    public ResponseEntity<OperationResponseDto> transfer(
        @Valid @RequestBody TransferRequestDto dto,
        Authentication auth
    ) {
        log.info("Transfer request from [{}] to [{}] amount [{}]",
                auth.getName(), dto.getTarget_username(), dto.getAmount());

        WalletEntity fromWallet = getUserWallet(auth.getName());           // списать

        /* 1. Проверяем получателя */
        if (!userService.userExists(dto.getTarget_username())) {
            log.warn("Transfer target user [{}] not found", dto.getTarget_username());
            throw new UserNotFoundException(dto.getTarget_username());   // ловит GlobalExceptionHandler → 404
        }

        WalletEntity toWallet = getUserWallet(dto.getTarget_username()); // зачислить

        OperationEntity operation = walletService.execute_transaction(
                dto.getAmount(), fromWallet, toWallet);

        log.info("Transfer completed: {}", operation.toString());
        return ResponseEntity.ok(new OperationResponseDto(operation));
    }
}