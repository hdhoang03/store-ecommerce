package com.example.store.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)//chỉ serialize các giá trị không null
public class UserUpdateRequest {
    String password;
    String name;
    String email;
    LocalDate dob;
    List<String> roles;
}
