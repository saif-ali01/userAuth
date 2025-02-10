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
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    // Dependency injection for repositories and services
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpRepo otpRepo;

    // POST endpoint for user registration
    @PostMapping("/register")
    public ResponseEntity<?> registerUserEntity(@RequestBody UserModals request) {
        try {
            Optional<UserModals> findUser = userRepo.findByEmail(request.getEmail());

            if (findUser.isPresent()) {
                if (!findUser.get().getVerified()) {
                    return ResponseEntity.status(403).body("Please Verify your email");
                }
                return ResponseEntity.status(409).body("User is already present with this email.");
            }

            UserModals savedUser = userRepo.save(request);

            User user = new User();
            user.setEmail(savedUser.getEmail());
            user.setName(savedUser.getName());
            user.setUserId(savedUser.getId());
            user.setIsVerified(savedUser.getVerified());

            return ResponseEntity.status(201).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    // POST endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<?> loginUserEntity(@RequestBody UserModals request) {
        try {
            Optional<UserModals> findUser = userRepo.findByEmail(request.getEmail());

            if (!findUser.isPresent()) {
                return ResponseEntity.status(404).body("Email is not registered");
            }

            if (!findUser.get().getVerified()) {
                return ResponseEntity.status(403).body("Please verify your email");
            }

            UserModals getUser = findUser.get();
            if (getUser.getPassword().equals(request.getPassword())) {
                User user = new User();
                user.setEmail(getUser.getEmail());
                user.setName(getUser.getName());
                user.setUserId(getUser.getId());
                user.setIsVerified(getUser.getVerified());

                return ResponseEntity.status(200).body(user);
            }

            return ResponseEntity.status(401).body("Incorrect password! Please enter the correct password.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    // POST endpoint for forgot password
    @PostMapping("/forgetpassword")
    public ResponseEntity<?> forgetPassword(@RequestBody Forgetpassword email) {
        try {
            Optional<UserModals> fetchUser = userRepo.findByEmail(email.getEmail());

            if (fetchUser.isEmpty()) {
                return ResponseEntity.status(404).body("USER NOT FOUND");
            }

            UserModals user = fetchUser.get();
            otpService.generateAndSendOtp(user.getId(), email.getEmail());

            return ResponseEntity.status(200).body("OTP sent to your email");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    // DTO for handling the forgot password request
    class Forgetpassword {
        private String email;

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }

    // DTO for representing user data in the responses
    class User {
        private String userId;
        private String name;
        private String email;
        private boolean isVerified;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Boolean getIsVerified() {
            return isVerified;
        }

        public void setIsVerified(Boolean isVerified) {
            this.isVerified = isVerified;
        }
    }
}
