package com.example.demo.domain.user.exception;


import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;

public class UserNotFoundException extends CustomException {

    public UserNotFoundException(){
        super(ErrorCode.USER_NOT_FOUND);
    }
}
