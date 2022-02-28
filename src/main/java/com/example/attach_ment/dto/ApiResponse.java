package com.example.attach_ment.dto;

import lombok.Data;

@Data
public class ApiResponse {
    private String message;
    public boolean isSuccess;

    public ApiResponse(String message, boolean isSuccess) {
        this.message = message;
        this.isSuccess = isSuccess;
    }
}
