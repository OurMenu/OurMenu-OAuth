package com.example.demo.domain.user.exception;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;

public class ConfirmCodeNotFoundException extends CustomException {

    public ConfirmCodeNotFoundException(){
        super(ErrorCode.CONFIRM_CODE_NOT_FOUND);
    }
}
