package com.example.store.service;

import com.example.store.dto.request.AddressCreationRequest;
import com.example.store.dto.request.AddressUpdateRequest;
import com.example.store.dto.response.AddressResponse;
import com.example.store.dto.response.UserAddressResponse;
import com.example.store.entity.Address;
import com.example.store.entity.User;
import com.example.store.exception.AppException;
import com.example.store.exception.ErrorCode;
import com.example.store.mapper.AddressMapper;
import com.example.store.repository.AddressRepository;
import com.example.store.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {
    AddressMapper addressMapper;
    AddressRepository addressRepository;
    UserRepository userRepository;

    private User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    //Thêm địa chỉ mới
    public AddressResponse createAddress(AddressCreationRequest request){
        User user = getCurrentUser();

        Address address = addressMapper.toAddress(request);
        address.setUser(user);

        address = addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }

    //Lấy danh sách địa chỉ người dùng hiện tại
    public UserAddressResponse getMyAddresses(){
        User user = getCurrentUser();

        List<Address> addresses = addressRepository.findAllByUser(user);
        List<AddressResponse> addressResponses = addresses.stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());

        return UserAddressResponse.builder()
                .userId(user.getId())
                .addresses(addressResponses)
                .build();
    }

    //Cập nhật địa chỉ
    public AddressResponse updateAddress(AddressUpdateRequest request){
        User user = getCurrentUser();

        //Tìm địa chỉ theo id
        Address address = addressRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if(!address.getUser().getId().equals(user.getId())){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        address.setAddress(request.getAddress());
        return addressMapper.toAddressResponse(addressRepository.save(address));
    }

    //Xóa địa chỉ
    public void deleteAddress(Long addressId){
        User user = getCurrentUser();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if(!address.getUser().getId().equals(user.getId())){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        addressRepository.save(address);
    }
}
