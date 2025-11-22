package com.financemanager.financemanager.DTOs.requests;

import org.hibernate.validator.constraints.NotBlank;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransferRequestDto {
    @NotBlank
    private String target_username;   // кому переводим
    @Positive
    private int amount;
}