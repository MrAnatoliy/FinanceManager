package com.financemanager.financemanager.DTOs.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentRequestDto {
    private Integer amount;
    private String category_name;
}
