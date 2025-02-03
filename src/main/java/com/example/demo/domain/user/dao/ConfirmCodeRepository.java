package com.example.demo.domain.user.dao;

import com.example.demo.domain.user.domain.ConfirmCode;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ConfirmCodeRepository extends CrudRepository<ConfirmCode, Long> {
    Optional<ConfirmCode> findConfirmCodeByEmail(String email);
}
