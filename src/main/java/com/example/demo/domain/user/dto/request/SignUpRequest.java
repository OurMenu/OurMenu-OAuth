package com.example.demo.domain.user.dto.request;

import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {
    private String email;
    private String password;
    private ArrayList<Integer> mealTime;
    private String signInType;
}
