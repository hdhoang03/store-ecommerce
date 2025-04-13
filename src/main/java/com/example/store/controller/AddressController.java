package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.AddressCreationRequest;
import com.example.store.dto.request.AddressUpdateRequest;
import com.example.store.dto.response.AddressResponse;
import com.example.store.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AddressController {

    AddressService addressService;

    @PostMapping
    ApiResponse<AddressResponse> createAddress(@RequestBody AddressCreationRequest request) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.createAddress(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<AddressResponse> updateAddress(@PathVariable String id, @RequestBody AddressUpdateRequest request) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.updateAddress(id, request))
                .build();
    }


    @GetMapping("/by-user")
    ApiResponse<List<AddressResponse>> getAddressesByCurrentUser() {
        return ApiResponse.<List<AddressResponse>>builder()
                .result(addressService.getAddressesByCurrentUser())
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteAddress(@PathVariable String id) {
        addressService.deleteAddress(id);
        return ApiResponse.<String>builder()
                .result("Address has been deleted.")
                .build();
    }
}
