package com.example.store.configuration;

import com.example.store.constaint.PredefinedRole;
import com.example.store.entity.Role;
import com.example.store.entity.User;
import com.example.store.repository.RoleReponsitory;
import com.example.store.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Configuration
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USERNAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    @ConditionalOnProperty(prefix = "spring", value = "datasource.driverClassName", havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleReponsitory roleReponsitory){
        return args -> {
            log.info("Initializing application......... Please wait.");

            if(!roleReponsitory.existsByName(PredefinedRole.USER_ROLE)){
                roleReponsitory.save(Role.builder()
                                .name(PredefinedRole.USER_ROLE)
                                .description("User role")
                                .build());
            }
            if(!roleReponsitory.existsByName(PredefinedRole.ADMIN_ROLE)){
                Role adminRole = roleReponsitory.save(Role.builder()
                                .name(PredefinedRole.ADMIN_ROLE)
                                .description("Admin role")
                                .build());

                var roles = new HashSet<Role>();
                roles.add(adminRole);

                User user = User.builder()
                        .username(ADMIN_USERNAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .build();

                userRepository.save(user);

                log.warn("admin user has been created with default password is 'admin', please change it");
            }
            log.info("Application initialization completed........................");
        };
    }
}
