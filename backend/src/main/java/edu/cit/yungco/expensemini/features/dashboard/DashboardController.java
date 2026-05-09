package edu.cit.yungco.expensemini.features.dashboard;

import edu.cit.yungco.expensemini.features.core.ApiResponse;
import edu.cit.yungco.expensemini.features.dashboard.DashboardSummaryResponse;
import edu.cit.yungco.expensemini.features.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary()));
    }
}
