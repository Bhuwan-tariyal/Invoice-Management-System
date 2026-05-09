package com.tariyal.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    // Email -> OTP Storage (In-memory for demo; use Redis for production/expiration)
    // Build a cache that expires entries 5 minutes after they are written
    private final LoadingCache<String, String> otpCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public String load(String key) {
                    return ""; // Return empty string if not found
                }
            });

    public String generateOtp(String email) {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));
        otpCache.put(email, otp);
        return otp;
    }

    public void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Tariyal IT Solution - Your Verification Code");
        message.setText("Your OTP for password reset is: " + otp + "\nThis code will expire in 5 minutes.");
        mailSender.send(message);
    }

    public boolean verifyOtp(String email, String userOtp) {
        try {
            String cachedOtp = otpCache.getUnchecked(email);
            // If cache expired, cachedOtp will be empty or different
            if (cachedOtp.equals(userOtp)) {
                otpCache.invalidate(email); // Clear it so it can't be used again
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
