package com.shopflow.shopflow.controller;

import com.shopflow.shopflow.dto.request.AddressRequest;
import com.shopflow.shopflow.dto.response.AddressResponse;
import com.shopflow.shopflow.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {
        return new ResponseEntity<>(addressService.createAddress(request, authentication.getName()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getMyAddresses(Authentication authentication) {
        return ResponseEntity.ok(addressService.getMyAddresses(authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(addressService.updateAddress(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id, Authentication authentication) {
        addressService.deleteAddress(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
