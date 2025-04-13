package com.example.store.mapper;

import com.example.store.dto.request.AddressCreationRequest;
import com.example.store.dto.request.AddressUpdateRequest;
import com.example.store.dto.response.AddressResponse;
import com.example.store.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toAddress(AddressCreationRequest request);

    void updateAddress(@MappingTarget Address address, AddressUpdateRequest request);

    AddressResponse toAddressResponse(Address address);
}
