package com.example.store.service;

import com.example.store.constaint.PredefinedRole;
import com.example.store.dto.request.*;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserMapper userMapper;
    UserRepository userRepository;
    RoleReponsitory roleReponsitory;
    PasswordEncoder passwordEncoder;
    RedisService redisService;
    EmailService emailService;

    public UserResponse createUser(UserCreationRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        String verificationCode = generateVerificationCode();
        String redisKey = "signup:" + request.getUsername();

        //Lưu vào redis
        TemporaryUser userData = TemporaryUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getName())
                .verificationCode(verificationCode)
                .build();

        redisService.setValue(redisKey, userData, 5);
        //Gửi email
        emailService.sendVerificationCode(SendVerificationEmailRequest.builder()
                .recipientEmail(userData.getEmail())
                .verificationCode(verificationCode)
                .build());

        return UserResponse.builder()
                .username(userData.getUsername())
                .email(userData.getEmail())
                .name(userData.getName())
                .enabled(false)
                .build();
    }

//    public UserResponse createUser(UserCreationRequest request){
//        User user = userMapper.toUser(request);
//        user.setEnabled(false);
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//
//        HashSet<Role> roles = new HashSet<>();
//        roleReponsitory.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
//        user.setRoles(roles);
//
//        try {
//            user = userRepository.save(user);
//
//            //Tạo mã xác minh và lưu vào redis
//            String verificationCode = generateVerificationCode();
//            String redisKey = "verify:" + user.getUsername();
//            redisService.setValue(redisKey, verificationCode, 20);
//
//            //Gửi email
//            emailService.sendVerificationCode(SendVerificationEmailRequest.builder()
//                    .recipientEmail(user.getEmail())
//                    .verificationCode(verificationCode)
//                    .build());
//        } catch (DataIntegrityViolationException e){
//            if(userRepository.existsByUsername(request.getUsername())){
//                throw new AppException(ErrorCode.USER_NOT_EXISTED);
//            }
//        }
//        return userMapper.toUserResponse(user);
//    }

    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found."));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        var roles = roleReponsitory.findAllById(request.getRoles());
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

    @PreAuthorize("hasRole('ADMIN')")
    public void lockOrUnlockUser(LockUserRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setEnabled(!request.isLock());//true = active, false = inactive
        userRepository.save(user);
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

    //Tạo ngẫu nhiên xác thực 6 số
    private String generateVerificationCode(){
        return String.format("%06d", new Random().nextInt(1000000));
    }

    public boolean verifyEmail(String username, String code){
        String redisKey = "signup:" + username;
        TemporaryUser tempUser = (TemporaryUser) redisService.getValue(redisKey, TemporaryUser.class);

        if(tempUser == null || !tempUser.getVerificationCode().equals(code)){
            return false;
        }
        //Tạo user thực
        User user = User.builder()
                .username(tempUser.getUsername())
                .password(tempUser.getPassword())
                .email(tempUser.getEmail())
                .name(tempUser.getName())
                .enabled(true)
                .build();

        //Gán role
        HashSet<Role> roles = new HashSet<>();
        roleReponsitory.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        userRepository.save(user);
        redisService.deleteValue(redisKey);//Xác minh xong xóa khỏi redis

        return true;
    }

//    public boolean verifyEmail(String username, String code){
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        String redisKey = "verify:" + username;
//        Object storedCode = redisService.getValue(redisKey);
//
//        if(storedCode != null && storedCode.equals(code)){
//            user.setEnabled(true);
//            userRepository.save(user);
//            redisService.deleteValue(redisKey); //Xác minh xong xóa khỏi redis
//            return true;
//        }
//        return false;
//    }
}