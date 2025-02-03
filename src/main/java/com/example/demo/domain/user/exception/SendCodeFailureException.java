package com.example.demo.domain.user.exception;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;

public class SendCodeFailureException extends CustomException {

    public SendCodeFailureException(){
        super(ErrorCode.SEND_CODE_FAILURE);
    }
}
