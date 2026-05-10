package com.tariyal.controller;

import com.tariyal.dao.CustomerDAO;
import com.tariyal.dto.LoginRequest;
import com.tariyal.entity.Customer;
import com.tariyal.service.AuthService;
import com.tariyal.service.OtpService;
import com.tariyal.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {
        String token;
        try {
            token = authService.login(request);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("success",false,"error","Invalid username and password"));
        }
        return ResponseEntity.ok(Map.of("success",true,"token", token));
    }

    //  Send OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        // Logic: Check if email exists in DB here...
        Customer customer = customerDAO.findByCustomerEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(Objects.isNull(customer)) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Invalid Email Address"));
        }
        String otp = otpService.generateOtp(email);
        otpService.sendOtpEmail(email, otp);

        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    //  Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (otpService.verifyOtp(email, otp)) {
            String token = jwtUtil.generateToken(email);
            return ResponseEntity.ok(Map.of("message", "OTP verified. Proceed to reset.","token", token));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid OTP"));
        }
    }

}
