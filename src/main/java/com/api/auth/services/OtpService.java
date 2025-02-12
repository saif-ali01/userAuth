package com.api.auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import com.api.auth.modals.OtpModal;
import com.api.auth.repositories.OtpRepo;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpRepo otpRepo;

    /**
     * Generate a 6-digit OTP
     * 
     * @return generated OTP
     */
    public String generateOtp(String userId) {
            return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    /**
     * Send the generated OTP to the user's email
     * 
     * @param email User's email
     * @param otp   OTP to be sent
     */
    public void sendOtpMail(String email, String otp) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Email Verification OTP");
            msg.setText("Your OTP is: " + otp);
            mailSender.send(msg);
            logger.info("OTP sent to: " + email);
        } catch (Exception e) {
            logger.error("Error sending OTP to {}: {}", email, e.getMessage(), e);
        }
    }

    /**
     * Generate and send OTP to the user's email
     * 
     * @param userId User ID for the OTP
     * @param email  User's email to send the OTP
     */
    public void generateAndSendOtp(String userId, String email) {
       try  {
     
        String otp = generateOtp(userId);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        OtpModal otpEntity = new OtpModal();
        otpEntity.setUserId(userId);
        otpEntity.setOtp(otp);
        otpEntity.setExpiresAt(expiresAt);

        sendOtpMail(email, otp); // Send OTP
        otpRepo.save(otpEntity); // Save OTP in the database
        
       } catch (Exception e) {
        // TODO: handle exception
       }
    }

    /**
     * Verify if the provided OTP is valid
     * 
     * @param userId User ID to fetch the OTP record
     * @param otp    OTP provided for verification
     * @return true if OTP matches and is valid, false otherwise
     */
    
public boolean verifyOtp(String userId, String otp) {
    try {
        Optional<OtpModal> dbOtp = otpRepo.findByUserId(userId);

        if (dbOtp.isPresent()) {
            OtpModal otpRecord = dbOtp.get();

            // Check if OTP is expired
            if (otpRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
                System.out.println("OTP has expired. Deleting record...");
                otpRepo.deleteAllByUserId(userId); // Delete expired OTP
                return false;
            }

            // Check if OTP matches
            if (otpRecord.getOtp().equals(otp)) {
                System.out.println("OTP is valid. Verifying and deleting record...");
                otpRepo.deleteAllByUserId(userId); // Delete OTP after successful verification
                return true;
            }

            System.out.println("OTP does not match.");
            return false; // OTP does not match
        } else {
            System.out.println("No OTP record found for the given user.");
            return false;
        }
    } catch (Exception e) {
        // Handle any unexpected exceptions
        System.err.println("An error occurred while verifying OTP: " + e.getMessage());
        e.printStackTrace(); // Optional: Print stack trace for debugging
        return false; // Return false in case of an error
    }
}

    /**
     * Delete OTP by User ID
     * 
     * @param userId User ID to delete OTP records
     */
    public void deleteOtpByUserId(String userId) {
        otpRepo.deleteAllByUserId(userId);
    }
}
