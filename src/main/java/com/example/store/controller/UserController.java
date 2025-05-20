package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.LockUserRequest;
import com.example.store.dto.request.UserCreationRequest;
import com.example.store.dto.request.UserUpdateRequest;
import com.example.store.dto.response.UserAddressResponse;
import com.example.store.dto.response.UserResponse;
import com.example.store.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody UserCreationRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping("/me/addresses")
    ApiResponse<UserAddressResponse> getMyAddresses(){
        return ApiResponse.<UserAddressResponse>builder()
                .code(1000)
                .result(userService.getMyAddresses())
                .build();
    }

    @GetMapping("/admin/get-all-users")
    ApiResponse<List<UserResponse>> getAllUser(){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUser())
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/admin/delete/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted.")
                .build();
    }

    @PostMapping("/admin/lock")
    ApiResponse<Void> lockOrUnlockUser(@RequestBody LockUserRequest request){
        userService.lockOrUnlockUser(request);
        String message = request.isLock() ? "User account has been locked." : "User account has been unlocked.";
        return ApiResponse.<Void>builder()
                .message(message)
                .build();
    }
}
