package com.tariyal.controller;

import com.tariyal.dao.CustomerDAO;
import com.tariyal.dto.CustomerResponse;
import com.tariyal.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private CustomerDAO customerDAO;



    @GetMapping
    public ResponseEntity<CustomerResponse> getProfile(Authentication authentication) {

        String email = authentication.getName();

        Customer customer = customerDAO.findByCustomerEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setId(customer.getId());
        customerResponse.setCustomerName(customer.getCustomerName());
        customerResponse.setCustomerAddress(customer.getCustomerAddress());
        customerResponse.setCustomerEmail(email);
        customerResponse.setCustomerPhone(customer.getCustomerPhone());

        return ResponseEntity.ok(customerResponse);
    }
}
