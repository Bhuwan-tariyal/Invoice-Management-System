package com.tariyal.controller;

import com.tariyal.dto.CustomerRequest;
import com.tariyal.dto.CustomerResponse;
import com.tariyal.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }


    @PostMapping("/updatePassword")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request, Authentication authentication) {
        String email = authentication.getName();
        try {
             customerService.resetPassword(email, request.get("password"));
        } catch (RuntimeException e) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized Access"));
        }
        return ResponseEntity.ok(Map.of("success","Password reset successfully"));
    }
}
