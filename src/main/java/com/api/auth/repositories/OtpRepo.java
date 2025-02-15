package com.api.auth.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.api.auth.modals.OtpModal;

public interface OtpRepo extends MongoRepository<OtpModal, String> {
    Optional<OtpModal> findByUserId(String userId);
    void deleteAllByUserId(String userId);
    

}
