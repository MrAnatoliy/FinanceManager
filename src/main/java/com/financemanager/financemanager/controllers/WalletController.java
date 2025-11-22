package com.financemanager.financemanager.controllers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financemanager.financemanager.DTOs.requests.BudgetRequestDto;
import com.financemanager.financemanager.DTOs.responses.BudgetResponseDto;
import com.financemanager.financemanager.DTOs.responses.OperationHistoryResponseDto;
import com.financemanager.financemanager.DTOs.responses.OperationResponseDto;
import com.financemanager.financemanager.DTOs.responses.WalletResponseDto;
import com.financemanager.financemanager.entities.BudgetEntity;
import com.financemanager.financemanager.entities.CategoryEntity;
import com.financemanager.financemanager.entities.OperationEntity;
import com.financemanager.financemanager.entities.UserEntity;
import com.financemanager.financemanager.entities.WalletEntity;
import com.financemanager.financemanager.exceptions.CategoryNotFoundException;
import com.financemanager.financemanager.repositories.BudgetRepository;
import com.financemanager.financemanager.repositories.CategoryRepository;
import com.financemanager.financemanager.repositories.OperationRepository;
import com.financemanager.financemanager.services.UserService;
import com.financemanager.financemanager.services.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

    private final OperationRepository operationRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    @PostMapping("/budget")
    public ResponseEntity<BudgetResponseDto> create_budget(@RequestBody BudgetRequestDto budgetRequestDto, Authentication auth){
        UserEntity user = userService.loadUser(auth.getName());
        WalletEntity user_wallet = walletService.getWalletByUser(user);

        CategoryEntity categoryEntity = categoryRepository.findByCategoryName(budgetRequestDto.getCategoryName()).orElseThrow(
            () -> new CategoryNotFoundException(budgetRequestDto.getCategoryName())
        );


        BudgetEntity newBudgetEntity = new BudgetEntity();
        newBudgetEntity.setBudgetCategory(categoryEntity);
        newBudgetEntity.setWallet(user_wallet);
        newBudgetEntity.setBudgetLimit(budgetRequestDto.getBudgetLimit());
        newBudgetEntity.setCurrentBudgetAmount(0);

        newBudgetEntity = budgetRepository.save(newBudgetEntity);
        return ResponseEntity.ok(new BudgetResponseDto(newBudgetEntity));
    }

    @GetMapping("/budget/all")
    public ResponseEntity<List<BudgetResponseDto>> get_all_budgets(Authentication auth){
        UserEntity user = userService.loadUser(auth.getName());
        WalletEntity user_wallet = walletService.getWalletByUser(user);

        List<BudgetEntity> wallet_budgets = budgetRepository.findByWallet(user_wallet);
        return ResponseEntity.ok(wallet_budgets.stream()
            .map((BudgetEntity budget) -> new BudgetResponseDto(budget))
            .toList()
        );
    }

    @GetMapping()
    public ResponseEntity<WalletResponseDto> get_wallet(Authentication auth) {
        UserEntity user = userService.loadUser(auth.getName());
        WalletEntity user_wallet = walletService.getWalletByUser(user);

        return ResponseEntity.ok(new WalletResponseDto(user_wallet));
    }

    @GetMapping("/get_history")
    public ResponseEntity<OperationHistoryResponseDto> get_history(Authentication auth) {
        UserEntity user = userService.loadUser(auth.getName());
        WalletEntity user_wallet = walletService.getWalletByUser(user);

        Instant to = Instant.now();
        Instant from = to.minus(30, ChronoUnit.DAYS);
        List<OperationEntity> operations_list = operationRepository.findByWalletAndCreatedAtBetweenOrderByCreatedAtDesc(user_wallet, from, to);

        return ResponseEntity.ok(new OperationHistoryResponseDto(
            operations_list.stream()
                .map((OperationEntity operation) -> new OperationResponseDto(operation))
                .toList()
        ));
    }
}
