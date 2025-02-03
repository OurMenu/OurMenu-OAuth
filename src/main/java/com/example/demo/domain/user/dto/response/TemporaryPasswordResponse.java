package com.example.demo.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemporaryPasswordResponse {
    private String temporaryPassword;

    public static TemporaryPasswordResponse from(String temporaryPassword){
        return TemporaryPasswordResponse.builder()
                .temporaryPassword(temporaryPassword)
                .build();
    }
}
