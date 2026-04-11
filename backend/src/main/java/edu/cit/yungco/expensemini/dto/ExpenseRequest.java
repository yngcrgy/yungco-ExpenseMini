package edu.cit.yungco.expensemini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    private String title;
    private BigDecimal amount;
    @JsonProperty("category_id")
    private Integer categoryId; // Using number as requested
    private String category; // Fallback
    private String notes;
    @JsonProperty("expense_date")
    private LocalDate expenseDate;
}
