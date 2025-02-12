package com.api.auth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.auth.Dto.OtpVerificationRequestDto;
import com.api.auth.modals.OtpModal;
import com.api.auth.modals.UserModals;
import com.api.auth.repositories.OtpRepo;
import com.api.auth.repositories.UserRepo;
import com.api.auth.services.OtpService;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = "http://localhost:5173")
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
     * 
     * @param request Contains the email of the user for which OTP is generated.
     * @return ResponseEntity with success message.
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateOtp(@RequestBody EmailRequestDto request) {
        try { // Fetch the user by email
            Optional<UserModals> fetchUser = userRepo.findByEmail(request.getEmail());
            Optional<OtpModal> checkOtpIsAlreadyPresent;

            // // If user exists, generate and send OTP
            if (fetchUser.isPresent()) {
                UserModals user = fetchUser.get();
                checkOtpIsAlreadyPresent = otpRepo.findByUserId(user.getId());
                if (checkOtpIsAlreadyPresent.isPresent()) {
                    otpRepo.deleteAllByUserId(user.getId());// remove previous otp in db

                }

                otpService.generateAndSendOtp(user.getId(), user.getEmail());
                return ResponseEntity.status(200).body("OTP sent successfully!"); // Success response
            }

            // If user does not exist
            return ResponseEntity.status(404).body("User Not Found in DB");

        } catch (Exception e) {
            // TODO: handle exception

            return ResponseEntity.status(500).body("Internal Server Error.");

        }

    }
    
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequestDto request) {
        boolean isValid = otpService.verifyOtp(request.getUserId(), request.getOtp());

        if (isValid) {
            // Update user verification status
            Optional<UserModals> fetchUser = userRepo.findById(request.getUserId());
            if (fetchUser.isPresent()) {
                UserModals user = fetchUser.get();
                user.setVerified(true);
                userRepo.save(user); // Save updated user details
                return ResponseEntity.status(200).body("OTP verified successfully!");
            }

            // If user is not found after valid OTP verification
            return ResponseEntity.status(404).body("User not found!");
        }

        // Invalid or expired OTP
        return ResponseEntity.status(400).body("Invalid or expired OTP.");
    }


}

class EmailRequestDto {
    private String email;

    // getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
