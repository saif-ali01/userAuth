package com.api.auth.Dto;

public class OtpVerificationRequestDto {

    private String userId;
    private String otp;

    // Getters and Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }
}