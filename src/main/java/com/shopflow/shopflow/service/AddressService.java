package com.shopflow.shopflow.service;
import com.shopflow.shopflow. dto.request.AddressRequest;
import com.shopflow.shopflow. dto.response.AddressResponse;
import java.util.List;

public interface AddressService {
    AddressResponse createAddress(AddressRequest request, String userEmail);
    AddressResponse updateAddress(Long id, AddressRequest request, String userEmail);
    void deleteAddress(Long id, String userEmail);
    List<AddressResponse> getMyAddresses(String userEmail);
}
