package com.example.demo.domain.user.exception;

import com.example.demo.global.exception.CustomException;
import com.example.demo.global.exception.ErrorCode;

public class InvalidMealTimeCountException extends CustomException {

    public InvalidMealTimeCountException(){
        super(ErrorCode.INVALID_MEAL_TIME_COUNT);
    }
}
