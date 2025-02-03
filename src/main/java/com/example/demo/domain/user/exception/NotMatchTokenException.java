package com.example.demo.domain.user.exception;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;

public class NotMatchTokenException extends CustomException {

    public NotMatchTokenException(){
        super(ErrorCode.NOT_MATCH_TOKEN);
    }
}
