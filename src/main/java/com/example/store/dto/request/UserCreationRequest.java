package com.example.store.dto.request;

import com.example.store.validator.EmailConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    String username;
    String password;
    String name;
    @EmailConstraint(message = "INVALID_EMAIL")
    String email;
    LocalDate dob;
}
