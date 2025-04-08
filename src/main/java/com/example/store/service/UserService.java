package com.example.store.service;

import com.example.store.constaint.PredefinedRole;
import com.example.store.dto.request.UserCreationRequest;
import com.example.store.dto.request.UserUpdateRequest;
import com.example.store.dto.response.UserResponse;
import com.example.store.entity.Role;
import com.example.store.entity.User;
import com.example.store.exception.AppException;
import com.example.store.exception.ErrorCode;
import com.example.store.mapper.UserMapper;
import com.example.store.repository.RoleReponsitory;
import com.example.store.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserMapper userMapper;
    UserRepository userRepository;
    RoleReponsitory roleReponsitory;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request){
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleReponsitory.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e){
            if(userRepository.existsByUsername(request.getUsername())){
                throw new AppException(ErrorCode.USERNAME_EXISTED);
            }
        }
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found."));

        userMapper.updateUser(user, request);
//        var roles = roleResponsitory.findAllById(request.getRoles());
//        user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUser(){
        return userRepository.findAll()
                .stream()//thay vì dùng for dùng stream sẽ gọn hơn
                .map(userMapper::toUserResponse)
                .toList();
    }

    public UserResponse getMyInfo(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(userName).map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//        var getContext = SecurityContextHolder.getContext();
//        String getName = getContext.getAuthentication().getName();
//
//        User user = userRepository.findByUsername(getName).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        var userResponse = userMapper.toUserResponse(user);
//        return userResponse;
    }
}