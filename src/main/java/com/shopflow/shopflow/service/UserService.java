package com.shopflow.shopflow.service;
import com.shopflow. shopflow. dto.response.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    void activateUser(Long id);
    void deactivateUser(Long id);
}
