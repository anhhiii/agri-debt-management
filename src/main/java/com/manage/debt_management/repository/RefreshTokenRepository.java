package com.manage.debt_management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manage.debt_management.model.RefreshToken;
import com.manage.debt_management.model.UserAccount;
import java.util.Optional;
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(UserAccount user);
}