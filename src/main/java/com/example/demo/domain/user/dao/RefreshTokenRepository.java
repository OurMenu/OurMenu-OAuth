package com.example.demo.domain.user.dao;

import com.example.demo.domain.user.domain.RefreshToken;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findRefreshTokenByEmail(String email);

}
