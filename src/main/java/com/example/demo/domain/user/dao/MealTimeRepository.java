package com.example.demo.domain.user.dao;

import com.example.demo.domain.user.domain.MealTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MealTimeRepository extends JpaRepository<MealTime, Long> {
    void deleteAllByUserId(Long userId);

    List<MealTime> findAllByUserId(Long userId);
}
