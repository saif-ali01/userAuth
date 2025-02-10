package com.api.auth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.auth.modals.UserModals;
import com.api.auth.repositories.OtpRepo;
import com.api.auth.repositories.UserRepo;
import com.api.auth.services.OtpService;

@RestController
@RequestMapping("/api/otp") // Base URL for OTP related APIs
public class OtpController {

    // Autowired dependencies
    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OtpRepo otpRepo;

    /**
     * Endpoint to generate and send an OTP to the user's email.
     * @param request Contains the email of the user for which OTP is generated.
     * @return ResponseEntity with success message.
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest request) {
        // Fetch the user by email
        Optional<UserModals> fetchUser = userRepo.findByEmail(request.getEmail());
        
        // If user exists, generate and send OTP
        if (fetchUser.isPresent()) {
            UserModals user = fetchUser.get();
            otpService.generateAndSendOtp(user.getId(), user.getEmail());
            return ResponseEntity.status(200).body("OTP sent successfully!"); // Success response
        }
        
        // If user does not exist
        return ResponseEntity.status(404).body("User not found with the given email.");
    }

    /**
     * Endpoint to verify OTP entered by the user.
     * @param request Contains the userId and OTP entered by the user.
     * @return ResponseEntity with success or failure message.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest request) {

        // Verify OTP using the service
        boolean isValid = otpService.verifyOtp(request.getUserId(), request.getOtp());
        if (isValid) {
            // OTP is valid, update user verification status
            Optional<UserModals> fetchUser = userRepo.findById(request.getUserId());
            if (fetchUser.isPresent()) {
                UserModals user = fetchUser.get();
                user.setVerified(true); // Mark user as verified
                userRepo.save(user); // Save the updated user details
                
                // Delete the OTP record from the database
                otpRepo.deleteAllById(user.getId());
                
                return ResponseEntity.status(200).body("OTP verified successfully!");
            }
        }
        
        // Return an error if OTP is invalid or expired
        return ResponseEntity.status(404).body("Invalid or expired OTP!");
    }

    /**
     * Endpoint to verify OTP for password change.
     * @param request Contains the userId, OTP, and new password.
     * @return ResponseEntity with success or failure message.
     */
    @PostMapping("/verifyOtpForChangePassword")
    public ResponseEntity<?> passwordOtp(@RequestBody ForgetPasswordOtp request) {

        // Verify OTP for password change
        boolean isValid = otpService.verifyOtp(request.getUserId(), request.getOtp());

        if (isValid) {
            // OTP is valid, change the user's password
            Optional<UserModals> fetchUser = userRepo.findById(request.getUserId());
            if (fetchUser.isPresent()) {
                UserModals user = fetchUser.get();
                user.setPassword(request.getPassword()); // Set the new password
                userRepo.save(user); // Save the updated user details
                
                return ResponseEntity.status(201).body("Password successfully changed!");
            }
        }
        
        // Return an error if OTP is invalid or the user is not found
        return ResponseEntity.status(500).body("Invalid OTP or user not found.");
    }

}

/**
 * DTO for OTP request containing email and userId.
 */
class OtpRequest {
    private String userId;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}

/**
 * DTO for OTP verification request containing userId and OTP.
 */
class OtpVerificationRequest {
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

/**
 * DTO for password change request, extends OtpVerificationRequest to include password.
 */
class ForgetPasswordOtp extends OtpVerificationRequest {

    private String password;

    // Getters and Setters
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
}
