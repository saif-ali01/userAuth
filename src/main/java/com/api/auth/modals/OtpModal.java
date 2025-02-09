package com.api.auth.modals;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

@Document(collection = "otp")  // Specifies the collection name in MongoDB
public class OtpModal {

    @Id
    private String id;  // Unique identifier for the OTP record

    @NonNull // Ensure that userId is not null
    private String userId;  // User ID to which the OTP is associated

    @NonNull // Ensure that OTP is not null
    private String otp;  // OTP code sent to the user

    private LocalDateTime expiresAt;  // Timestamp for when the OTP expires

    // Default constructor
    public OtpModal() { }

    // Constructor with parameters for easy initialization
    public OtpModal(String userId, String otp, String expiresAt) {
        this.userId = userId;
        this.otp = otp;
        this.expiresAt = LocalDateTime.parse(expiresAt);  // Convert String to LocalDateTime
    }

    // Getter for userId
    public String getUserId() {
        return userId;
    }

    // Setter for userId
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter for otp
    public String getOtp() {
        return otp;
    }

    // Setter for otp
    public void setOtp(String otp) {
        this.otp = otp;
    }

    // Getter for expiresAt
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    // Setter for expiresAt
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

}
