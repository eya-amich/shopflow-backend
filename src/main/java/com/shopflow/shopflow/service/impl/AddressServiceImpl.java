package com.shopflow.shopflow.service.impl;
import com.shopflow.shopflow.dto.request.AddressRequest;
import com.shopflow.shopflow. dto.response.AddressResponse;
import com.shopflow.shopflow. entity.Address;
import com.shopflow.shopflow. entity.User;
import com.shopflow.shopflow. exception.ResourceNotFoundException;
import com.shopflow.shopflow. exception.UnauthorizedException;
import com.shopflow.shopflow. mapper.AddressMapper;
import com.shopflow.shopflow. repository.AddressRepository;
import com.shopflow.shopflow. repository.UserRepository;
import com.shopflow.shopflow. service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressResponse createAddress(AddressRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Si c'est la première adresse ou si principal = true, mettre les autres à false
        if (request.isPrincipal()) {
            addressRepository.findByUserIdAndPrincipal(user.getId(), true)
                    .ifPresent(addr -> {
                        addr.setPrincipal(false);
                        addressRepository.save(addr);
                    });
        }

        Address address = Address.builder()
                .user(user)
                .rue(request.getRue())
                .ville(request.getVille())
                .codePostal(request.getCodePostal())
                .pays(request.getPays())
                .principal(request.isPrincipal())
                .build();

        address = addressRepository.save(address);
        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest request, String userEmail) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse non trouvée"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Cette adresse ne vous appartient pas");
        }

        if (request.isPrincipal()) {
            addressRepository.findByUserIdAndPrincipal(user.getId(), true)
                    .ifPresent(addr -> {
                        if (!addr.getId().equals(id)) {
                            addr.setPrincipal(false);
                            addressRepository.save(addr);
                        }
                    });
        }

        address.setRue(request.getRue());
        address.setVille(request.getVille());
        address.setCodePostal(request.getCodePostal());
        address.setPays(request.getPays());
        address.setPrincipal(request.isPrincipal());

        address = addressRepository.save(address);
        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id, String userEmail) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse non trouvée"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Cette adresse ne vous appartient pas");
        }

        addressRepository.delete(address);
    }

    @Override
    public List<AddressResponse> getMyAddresses(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return addressMapper.toResponseList(addressRepository.findByUserId(user.getId()));
    }
}
