package com.example.demo.global.response.util;

import com.example.demo.global.exception.ErrorResponse;
import com.example.demo.global.response.ApiResponse;

public class ApiUtil {

    private ApiUtil() {
    }

    public static <T> ApiResponse<T> success(T response) {
        return new ApiResponse<>(true, response, null);
    }

    public static ApiResponse<?> error(ErrorResponse errorResponse) {
        return new ApiResponse<>(false, null, errorResponse);
    }

    public static ApiResponse<Void> successOnly() {
        return new ApiResponse<>(true, null, null);
    }
}
