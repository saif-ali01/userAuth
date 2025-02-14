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
@CrossOrigin(origins = {"http://localhost:5173","https://loginpage01x.netlify.app"})
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
    try {
        // Validate request
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.status(400).body("Email is required");
        }

        // Fetch user by email
        Optional<UserModals> fetchUser = userRepo.findByEmail(request.getEmail());
        if (fetchUser.isPresent()) {
            UserModals user = fetchUser.get();

            // Check if OTP already exists and delete it
            Optional<OtpModal> checkOtpIsAlreadyPresent = otpRepo.findByUserId(user.getId());
            if (checkOtpIsAlreadyPresent.isPresent()) {
                otpRepo.deleteAllByUserId(user.getId());
            }

            // Generate and send OTP
            otpService.generateAndSendOtp(user.getId(), user.getEmail());
            return ResponseEntity.status(200).body("OTP sent successfully!");
        }

        // User not found
        return ResponseEntity.status(404).body("User Not Found in DB");

    } catch (Exception e) {
        e.printStackTrace(); // Log the error
        return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
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
