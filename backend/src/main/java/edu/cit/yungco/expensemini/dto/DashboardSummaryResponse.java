package edu.cit.yungco.expensemini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardSummaryResponse {
    @JsonProperty("monthly_budget")
    private BigDecimal monthlyBudget;

    @JsonProperty("total_expenses")
    private BigDecimal totalExpenses;

    @JsonProperty("remaining_budget")
    private BigDecimal remainingBudget;

    @JsonProperty("top_category")
    private String topCategory;

    @JsonProperty("avg_daily_spending")
    private BigDecimal avgDailySpending;
}
