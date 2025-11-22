package com.financemanager.financemanager.DTOs.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper=true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetLimitExcessedWarning extends BaseWarning {

    private String excessedBudgetLimitCategoryName;

    @Override
    public String getMessage() {
        return "Budget for category " + excessedBudgetLimitCategoryName + " has exceeded its limits";
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}