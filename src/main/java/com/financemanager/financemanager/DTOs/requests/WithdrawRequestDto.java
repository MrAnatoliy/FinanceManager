package com.financemanager.financemanager.DTOs.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WithdrawRequestDto {
    private Integer amount;
}
