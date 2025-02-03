package com.example.demo.domain.user.exception;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;

public class TokenExpiredExcpetion extends CustomException {

    public TokenExpiredExcpetion(){
        super(ErrorCode.TOKEN_EXPIRED);
    }
}
