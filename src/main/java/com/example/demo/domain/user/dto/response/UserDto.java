package com.example.demo.domain.user.dto.response;

import com.example.demo.domain.user.domain.MealTime;
import com.example.demo.domain.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {

    private String email;
    private String signInType;
    private List<Integer> mealTime;

    public static UserDto of(User user, List<MealTime> mealTimes){
        return UserDto.builder()
                .email(user.getEmail())
                .signInType(user.getSignInType().name())
                .mealTime(mealTimes.stream()
                        .map(MealTime::getMealTime)
                        .collect(Collectors.toList()))
                .build();
    }
}
