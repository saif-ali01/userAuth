package com.api.auth.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.api.auth.modals.UserModals;

public interface UserRepo extends MongoRepository<UserModals ,String> {
    Optional<UserModals> findByEmail(String email);
}
