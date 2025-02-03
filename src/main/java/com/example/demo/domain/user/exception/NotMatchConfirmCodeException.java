package com.example.demo.domain.user.exception;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;

public class NotMatchConfirmCodeException extends CustomException {

    public NotMatchConfirmCodeException(){
        super(ErrorCode.NOT_MATCH_CONFIRM_CODE);
    }
}
