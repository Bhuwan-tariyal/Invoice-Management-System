package com.tariyal.service;

import com.tariyal.dao.CustomerDAO;
import com.tariyal.dao.InvoiceDAO;
import com.tariyal.dao.StudentDAO;
import com.tariyal.dto.DashBoardResponse;
import com.tariyal.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashBoardService {
    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private StudentDAO studentDAO;

    @Autowired
    private InvoiceDAO invoiceDAO;

    public DashBoardResponse getDashBoardDate(Authentication authentication) {
        String email = authentication.getName();
        Customer customer = customerDAO.findByCustomerEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        DashBoardResponse dashBoardResponse = new DashBoardResponse();
        dashBoardResponse.setTotalStudents(studentDAO.countByCustomerId(customer.getId()));
        dashBoardResponse.setTotalRevenue(invoiceDAO.getTotalAmountByCustomerId(customer));
        dashBoardResponse.setTotalInvoices(invoiceDAO.countByCustomerId(customer.getId()));
        dashBoardResponse.setCustomerName(customer.getCustomerName());
        return dashBoardResponse;
    }
}
