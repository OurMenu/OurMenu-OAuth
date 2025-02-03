package com.example.demo.domain.user.exception;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;

public class InvalidTokenException extends CustomException {

    public InvalidTokenException(){
        super(ErrorCode.INVALID_TOKEN);
    }
}
