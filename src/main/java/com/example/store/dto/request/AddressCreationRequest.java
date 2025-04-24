package com.example.store.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressCreationRequest {
    String address;
    String phoneNum;
//    Boolean isDefault;
    //có thể mở rộng bằng cách tách nhỏ address ra thành street, ward, district, city, province, zipCode
}
