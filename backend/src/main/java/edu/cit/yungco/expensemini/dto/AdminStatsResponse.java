package edu.cit.yungco.expensemini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AdminStatsResponse {
    @JsonProperty("total_users")
    private long totalUsers;
    
    @JsonProperty("total_expenses_count")
    private long totalExpensesCount;
    
    @JsonProperty("total_system_money")
    private BigDecimal totalSystemMoney;
}
