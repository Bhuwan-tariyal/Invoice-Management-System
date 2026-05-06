package com.tariyal.service;

import com.tariyal.dao.CustomerDAO;
import com.tariyal.dao.StudentDAO;
import com.tariyal.dto.StudentDropDown;
import com.tariyal.dto.StudentRequest;
import com.tariyal.entity.Customer;
import com.tariyal.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private StudentDAO studentDAO;

    public Student addStudent(StudentRequest request, String customerEmail) {

        // 🔥 find logged-in customer from token email
        Customer customer = customerDAO.findByCustomerEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Student student = new Student();
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhoneNumber(request.getPhoneNumber());

        // 🔥 IMPORTANT: link student with customer
        student.setCustomer(customer);

        return studentDAO.save(student);
    }


    public List<StudentDropDown> getAllStudents(String customerEmail) {
        Customer customer = customerDAO.findByCustomerEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return studentDAO.findByCustomer(customer).stream()
                .map(s -> new StudentDropDown(
                        s.getId(),
                        s.getFirstName() + " " + s.getLastName()
                ))
                .toList();
    }
}
