package com.example.store.service;

import com.example.store.dto.request.AddressCreationRequest;
import com.example.store.dto.request.AddressUpdateRequest;
import com.example.store.dto.response.AddressResponse;
import com.example.store.entity.Address;
import com.example.store.entity.User;
import com.example.store.exception.AppException;
import com.example.store.exception.ErrorCode;
import com.example.store.mapper.AddressMapper;
import com.example.store.repository.AddressRepository;
import com.example.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {

    AddressRepository addressRepository;
    UserRepository userRepository;
    AddressMapper addressMapper;

    public AddressResponse createAddress(AddressCreationRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Address address = addressMapper.toAddress(request);
        address.setUser(user);

        Address saved = addressRepository.save(address);
        return addressMapper.toAddressResponse(saved);
    }

    public AddressResponse updateAddress(String addressId, AddressUpdateRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!address.getUser().getUsername().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        addressMapper.updateAddress(address, request);

        Address updated = addressRepository.save(address);
        return addressMapper.toAddressResponse(updated);
    }



    public List<AddressResponse> getAddressesByCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Address> addresses = addressRepository.findByUserId(user.getId());
        return addresses.stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    public void deleteAddress(String addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!address.getUser().getUsername().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        addressRepository.delete(address);
    }
}
