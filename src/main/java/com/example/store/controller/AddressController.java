package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.AddressCreationRequest;
import com.example.store.dto.request.AddressUpdateRequest;
import com.example.store.dto.response.AddressResponse;
import com.example.store.dto.response.UserAddressResponse;
import com.example.store.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {
    AddressService addressService;

    @GetMapping("/my-address")
    ApiResponse<UserAddressResponse> getMyAddresses(){
        return ApiResponse.<UserAddressResponse>builder()
                .code(1000)
                .result(addressService.getMyAddresses())
                .build();
    }

    @PostMapping("/create")
    ApiResponse<AddressResponse> createAddress(@RequestBody AddressCreationRequest request){
        return ApiResponse.<AddressResponse>builder()
                .message("Add new address success!")
                .result(addressService.createAddress(request))
                .build();
    }

    @PutMapping("/update")
    ApiResponse<AddressResponse> updateAddress(@RequestBody AddressUpdateRequest request){
        return ApiResponse.<AddressResponse>builder()
                .message("Update an address success!")
                .result(addressService.updateAddress(request))
                .build();
    }

    @DeleteMapping("/delete")
    ApiResponse<Void> deleteAddress(@PathVariable Long id){
        addressService.deleteAddress(id);
        return ApiResponse.<Void>builder()
                .message("Your address has been deleted!")
                .build();
    }
}
