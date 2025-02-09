package com.api.auth.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.api.auth.modals.OtpModal;

public interface OtpRepo extends MongoRepository<OtpModal, String> {
    OtpModal findByUserId(String userId);

    void deleteAllById(String id);
}
