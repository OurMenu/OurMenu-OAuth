package com.example.demo.domain.user.dto.request;


import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MealTimeRequest {
    ArrayList<Integer> mealTime;
}
