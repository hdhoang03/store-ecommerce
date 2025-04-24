package com.example.store.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String address;
    String phoneNum;

//    Boolean isDefault;//đánh dấu địa chỉ mong muốn làm mặc định

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
