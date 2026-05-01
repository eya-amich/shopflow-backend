package com.shopflow.shopflow.service;
import com.shopflow.shopflow. dto.response.DashboardAdminResponse;
import com.shopflow.shopflow. dto.response.DashboardCustomerResponse;
import com.shopflow.shopflow. dto.response.DashboardSellerResponse;

public interface DashboardService {
    DashboardAdminResponse getAdminDashboard();
    DashboardSellerResponse getSellerDashboard(String sellerEmail);
    DashboardCustomerResponse getCustomerDashboard(String customerEmail);
}
