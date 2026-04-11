package edu.cit.yungco.expensemini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ExpenseResponse {
    @JsonProperty("expense_id")
    private Long expenseId;
    private String title;
    private BigDecimal amount;
    private String category;
    @JsonProperty("expense_date")
    private LocalDate expenseDate;
}
