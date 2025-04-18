package com.example.store.mapper;

import com.example.store.dto.request.AddressCreationRequest;
import com.example.store.dto.response.AddressResponse;
import com.example.store.dto.response.UserAddressResponse;
import com.example.store.entity.Address;
import com.example.store.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressCreationRequest request);
    AddressResponse toAddressResponse(Address address);

//    @Mapping(source = "id", target = "userId")
//    @Mapping(source = "addresses", target = "addresses")
//    UserAddressResponse toUserAddressResponse(User user);
}
