package edu.cit.yungco.expensemini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BudgetRequest {
    private Integer month;
    private Integer year;
    @JsonProperty("budget_limit")
    private BigDecimal budgetLimit;
}
