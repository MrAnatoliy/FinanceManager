package com.financemanager.financemanager.DTOs.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperationHistoryResponseDto {
    private List<OperationResponseDto> operation_history;
}
