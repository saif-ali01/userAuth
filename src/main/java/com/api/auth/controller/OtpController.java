package com.api.auth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.auth.modals.OtpModal;
import com.api.auth.modals.UserModals;
import com.api.auth.repositories.OtpRepo;
import com.api.auth.repositories.UserRepo;
import com.api.auth.services.OtpService;

@RestController
@RequestMapping("/api/otp")
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

    // /**
    // * Endpoint to verify OTP for password change.
    // *
    // * @param request Contains the userId, OTP, and new password.
    // * @return ResponseEntity with success or failure message.
    // */
    @PostMapping("/verifyOtpForChangePassword")
    public ResponseEntity<?> passwordOtp(@RequestBody ForgetPasswordDto request) {
        try {
            // // Verify OTP for password change
            boolean isValid = otpService.verifyOtp(request.getUserId(), request.getOtp());

            if (isValid) {
                // // OTP is valid, change the user's password
                Optional<UserModals> fetchUser = userRepo.findById(request.getUserId());
                if (fetchUser.isPresent()) {
                    UserModals user = fetchUser.get();
                    user.setPassword(request.getPassword()); // Set the new password
                   UserModals savedUser= userRepo.save(user); // Save the updated user details

                    return ResponseEntity.status(201).body(savedUser);
                }
            }
            return ResponseEntity.status(403).body("Otp is not Correct ");

        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.status(500).body("Internal Server Error.");
        }

        // Return an error if OTP is invalid or the user is not found
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

class ForgetPasswordDto {
    private String userId;
    private String otp;
    private String Password;

    // getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}