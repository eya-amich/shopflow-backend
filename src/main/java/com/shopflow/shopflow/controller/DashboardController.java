package com.shopflow.shopflow.controller;
import com.shopflow.shopflow. dto.response.DashboardAdminResponse;
import com.shopflow.shopflow. dto.response.DashboardCustomerResponse;
import com.shopflow.shopflow. dto.response.DashboardSellerResponse;
import com.shopflow.shopflow. service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    public ResponseEntity<DashboardAdminResponse> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/seller")
    public ResponseEntity<DashboardSellerResponse> getSellerDashboard(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getSellerDashboard(authentication.getName()));
    }

    @GetMapping("/customer")
    public ResponseEntity<DashboardCustomerResponse> getCustomerDashboard(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getCustomerDashboard(authentication.getName()));
    }
}
