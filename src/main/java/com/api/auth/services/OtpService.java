package com.api.auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
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
     * @return generated OTP
     */
    public String generateOtp() {
        // Using ThreadLocalRandom for better performance in multi-threaded environments
        int generateOtp = ThreadLocalRandom.current().nextInt(100000, 1000000); // 6-digit OTP
        return String.valueOf(generateOtp);
    }

    /**
     * Send the generated OTP to the user's email
     * @param email User's email
     * @param otp OTP to be sent
     */
    public void sentOtpMail(String email, String otp) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Email Verification OTP");
            msg.setText("Your OTP is: " + otp);
            mailSender.send(msg);
            logger.info("OTP sent to: " + email); // Log successful OTP sending
        } catch (Exception e) {
            logger.error("Error sending OTP to {}: {}", email, e.getMessage(), e); // Log error
        }
    }

    /**
     * Generate and send OTP to the user's email
     * @param userId User ID for the OTP
     * @param email User's email to send the OTP
     */
    public void generateAndSendOtp(String userId, String email) {
        String otp = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10); // OTP expiration time (10 minutes)

        // Create a new OtpModal instance to store OTP details
        OtpModal otpEntity = new OtpModal();
        otpEntity.setUserId(userId);
        otpEntity.setOtp(otp);
        otpEntity.setExpiresAt(expiresAt);

        sentOtpMail(email, otp); // Send OTP via email
        otpRepo.save(otpEntity); // Save OTP in database for future verification
    }

    /**
     * Verify if the provided OTP is valid
     * @param userId User ID to fetch the OTP record
     * @param otp OTP provided for verification
     * @return true if OTP matches and is valid, false otherwise
     */
    public boolean verifyOtp(String userId, String otp) {
        OtpModal otpRecord = otpRepo.findByUserId(userId);
        if (otpRecord == null) {
            return false; // OTP record not found
        }

        // Check if OTP is expired
        if (otpRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepo.delete(otpRecord); // Delete expired OTP record
            return false;
        }

        // Check if OTP matches
        if (otpRecord.getOtp().equals(otp)) {
            otpRepo.delete(otpRecord); // Delete OTP after successful verification
            return true;
        }

        return false; // OTP does not match
    }
}
