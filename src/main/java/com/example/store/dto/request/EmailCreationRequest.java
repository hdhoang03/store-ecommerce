package com.example.store.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailCreationRequest {
    String recipient; //Người nhận
    String subject; //Tiêu đề
    String content; //Nội dung
    String attachment; //Tệp đính kèm (hình ảnh, file, ...)
}
