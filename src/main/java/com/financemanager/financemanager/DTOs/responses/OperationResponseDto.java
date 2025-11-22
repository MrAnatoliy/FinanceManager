package com.financemanager.financemanager.DTOs.responses;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.financemanager.financemanager.DTOs.internal.BaseWarning;
import com.financemanager.financemanager.entities.OperationEntity;
import com.financemanager.financemanager.enums.OperationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OperationResponseDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class WarningResponse {
        private String type;
        private String message;
    }

    private String wallet_owner_username;
    private int opeation_value;
    private OperationType operation_type;
    private String operation_category;
    private Instant created_at;
    private List<WarningResponse> warnings;

    public OperationResponseDto(OperationEntity operation) {
        this(operation, new ArrayList<>());   // warnings = пустой список
    }

    public OperationResponseDto(OperationEntity operation, List<BaseWarning> warnings) {
        this.wallet_owner_username = operation.getWallet().getUser().getUsername();
        this.opeation_value = operation.getOperationValue();
        this.operation_type = operation.getOperationType();
        this.created_at = operation.getCreatedAt();
        this.operation_category = operation.getOperationCategory() == null
            ? null
            : operation.getOperationCategory().getCategoryName();
        this.warnings = warnings.stream()
            .map(w -> new WarningResponse(w.getType(), w.getMessage()))
            .toList();
    }
}
