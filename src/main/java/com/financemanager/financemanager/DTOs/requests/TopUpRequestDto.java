package com.financemanager.financemanager.DTOs.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopUpRequestDto {
    private Integer amount;
}
