package com.financemanager.financemanager.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financemanager.financemanager.DTOs.requests.TopUpRequestDto;
import com.financemanager.financemanager.DTOs.requests.WithdrawRequestDto;
import com.financemanager.financemanager.DTOs.responses.OperationResponseDto;
import com.financemanager.financemanager.entities.OperationEntity;
import com.financemanager.financemanager.entities.UserEntity;
import com.financemanager.financemanager.entities.WalletEntity;
import com.financemanager.financemanager.exceptions.NotEnoughFundsException;
import com.financemanager.financemanager.services.UserService;
import com.financemanager.financemanager.services.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/opearations")
@RequiredArgsConstructor
public class OperationsController {

    static final Logger logger = Logger.getLogger(OperationsController.class.getName());
    
    private final WalletService wallet_service;
    private final UserService user_service;

    @PostMapping("/top_up")
    public ResponseEntity<?> top_up_wallet(@RequestBody TopUpRequestDto top_up_request_dto, Authentication auth) {
        UserEntity user;
        try {
            user = user_service.loadUser(auth.getName()); 
            logger.log(Level.INFO, "Found user in request {0}", user.toString());        
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        WalletEntity user_wallet = wallet_service.getWalletByUser(user);
        if(user_wallet == null){
            logger.log(Level.WARNING, "Failed to get wallet of user {0}", user.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get user wallet");
        } else {
            logger.log(Level.INFO, "User wallet was found : {0}", user_wallet.toString());
        }

        try {
            logger.log(Level.INFO, "Trying to top up user wallet with {0}", top_up_request_dto.getAmount());
            OperationEntity operation = wallet_service.top_up(top_up_request_dto.getAmount(), user_wallet);

            logger.log(Level.INFO, "Successfully top up user wallet with {0}", operation.getOperation_value());
            return ResponseEntity.ok(new OperationResponseDto(operation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to top up user wallet : " + e.getMessage());
        }

    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw_from_wallet(@RequestBody WithdrawRequestDto withdraw_request_dto, Authentication auth) {
        UserEntity user;
        try {
            user = user_service.loadUser(auth.getName()); 
            logger.log(Level.INFO, "Found user in request {0}", user.toString());        
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        WalletEntity user_wallet = wallet_service.getWalletByUser(user);
        if(user_wallet == null){
            logger.log(Level.WARNING, "Failed to get wallet of user {0}", user.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get user wallet");
        } else {
            logger.log(Level.INFO, "User wallet was found : {0}", user_wallet.toString());
        }

        try {
            logger.log(Level.INFO, "Trying to withdraw {0} from user wallet", withdraw_request_dto.getAmount());
            OperationEntity operation = wallet_service.withdraw(withdraw_request_dto.getAmount(), user_wallet);

            logger.log(Level.INFO, "Successfully withdraw {0} from user wallet with", operation.getOperation_value());
            return ResponseEntity.ok(new OperationResponseDto(operation));
        } catch (NotEnoughFundsException notEnoughFundsException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(notEnoughFundsException.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to top up user wallet : " + e.getMessage());
        }

    }

}
