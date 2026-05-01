package com.shopflow.shopflow.mapper;
import com.shopflow.shopflow.dto.response.AddressResponse;
import com.shopflow.shopflow. entity.Address;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse toResponse(Address address);

    List<AddressResponse> toResponseList(List<Address> addresses);
}
