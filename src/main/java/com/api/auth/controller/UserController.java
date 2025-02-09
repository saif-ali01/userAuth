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
@RequestMapping("/auth") // Base URL for authentication-related routes
public class UserController {

    // Dependency injection for repositories and services
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpRepo otpRepo;

    // POST endpoint for user registration
    // This method registers a new user and returns user data if successfully registered
    @PostMapping("/register")
    public ResponseEntity<?> registerUserEntity(@RequestBody UserModals request) {
        try {
            // Check if the user already exists by email
            Optional<UserModals> findUser = userRepo.findByEmail(request.getEmail());

            // If user exists, check if they are verified
            if (findUser.isPresent()) {
                if (!findUser.get().getVerified()) {
                    return ResponseEntity.ok().body("Please Verify your email");
                }
                return ResponseEntity.badRequest().body("User is already present with this email.");
            }

            // If user does not exist, save the new user
            UserModals savedUser = userRepo.save(request);

            // Prepare a custom response DTO for the registered user
            User user = new User();
            user.setEmail(savedUser.getEmail());
            user.setName(savedUser.getName());
            user.setUserId(savedUser.getId());
            user.setIsVerified(savedUser.getVerified());

            // Return a successful response with the user details
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            // Handle exceptions by returning an error message
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    // POST endpoint for user login
    // This method authenticates the user based on email and password
    @PostMapping("/login")
    public ResponseEntity<?> loginUserEntity(@RequestBody UserModals request) {
        try {
            // Check if the user exists in the system by email
            Optional<UserModals> findUser = userRepo.findByEmail(request.getEmail());

            // If the user does not exist, return an error message
            if (!findUser.isPresent()) {
                return ResponseEntity.badRequest().body("Email is not registered");
            }

            // If the user exists, check if they are verified
            if (!findUser.get().getVerified()) {
                return ResponseEntity.badRequest().body("Please verify your email");
            }

            // If the user is verified, check if the password is correct
            UserModals getUser = findUser.get();
            if (getUser.getPassword().equals(request.getPassword())) {
                // Prepare a custom response DTO for the logged-in user
                User user = new User();
                user.setEmail(getUser.getEmail());
                user.setName(getUser.getName());
                user.setUserId(getUser.getId());
                user.setIsVerified(getUser.getVerified());

                // Return a successful response with the user details
                return ResponseEntity.ok(user);
            }

            // If password does not match, return an error message
            return ResponseEntity.ok().body("Incorrect password! Please enter the correct password.");

        } catch (Exception e) {
            // Handle exceptions by returning an error message
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    // POST endpoint for forgot password
    // This method generates and sends an OTP to the user's email to allow password reset
    @PostMapping("/forgetpassword")
    public String forgetPassword(@RequestBody Forgetpassword email) {
        // Fetch user by email to validate if the user exists
        Optional<UserModals> fetchUser = userRepo.findByEmail(email.getEmail());

        // If the user does not exist, return an error message
        if (fetchUser.isEmpty()) {
            return "USER NOT FOUND";
        }

        // If the user exists, generate and send OTP
        UserModals user = fetchUser.get();
        otpService.generateAndSendOtp(user.getId(), email.getEmail());

        // Return a success message after sending the OTP
        return "OTP sent to your email";
    }
}

// DTO for handling the forgot password request
// Contains only the email field
class Forgetpassword {
    private String email;

    // Getter and setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}

// DTO for representing user data in the responses
// Contains fields like userId, name, email, and verification status
class User {
    private String userId;
    private String name;
    private String email;
    private boolean isVerified;

    // Getter and setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and setter for isVerified
    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
}
