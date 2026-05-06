package com.tariyal.service;

import com.tariyal.dao.CustomerDAO;
import com.tariyal.dto.LoginRequest;
import com.tariyal.dto.LoginResponse;
import com.tariyal.entity.Customer;
import com.tariyal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private JwtUtil jwtUtil;



    public String login(LoginRequest request) {

        Customer customer = customerDAO
                .findByCustomerEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥 password match
        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

          return jwtUtil.generateToken(customer.getCustomerEmail());
        }

}
