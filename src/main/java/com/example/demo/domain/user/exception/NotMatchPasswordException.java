package com.example.demo.domain.user.exception;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;

public class NotMatchPasswordException extends CustomException {

    public NotMatchPasswordException(){
        super(ErrorCode.NOT_MATCH_PASSWORD);
    }
}
