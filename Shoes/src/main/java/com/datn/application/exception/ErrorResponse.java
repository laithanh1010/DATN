package com.datn.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ErrorResponse {
    // đại diện cho thông tin lỗi trả về từ server khi xử lý các yêu cầu không thành công
    private HttpStatus status;
    private String message;
}
