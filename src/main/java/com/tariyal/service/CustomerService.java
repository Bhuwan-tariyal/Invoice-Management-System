package com.tariyal.service;

import com.tariyal.dao.CustomerDAO;
import com.tariyal.dto.CustomerRequest;
import com.tariyal.dto.CustomerResponse;
import com.tariyal.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerDAO customerDAO;

    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setCustomerName(request.getCustomerName());
        customer.setCustomerEmail(request.getCustomerEmail());
        customer.setCustomerPhone(request.getCustomerPhone());
        customer.setCustomerAddress(request.getCustomerAddress());

        // 🔥 encrypt password
        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        Customer saved = customerDAO.save(customer);

        return mapToResponse(saved);
    }

    public CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setCustomerName(customer.getCustomerName());
        response.setCustomerEmail(customer.getCustomerEmail());
        response.setCustomerPhone(customer.getCustomerPhone());
        response.setCustomerAddress(customer.getCustomerAddress());
        return response;
    }
}
