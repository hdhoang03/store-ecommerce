package com.example.store.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class EmailResponse {
    String recipient; //Người nhận
    String subject; //Tiêu đề
}
