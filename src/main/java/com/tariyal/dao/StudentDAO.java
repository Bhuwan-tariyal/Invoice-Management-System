package com.tariyal.dao;

import com.tariyal.entity.Customer;
import com.tariyal.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentDAO extends JpaRepository<Student,Long> {
    List<Student> findByCustomer(Customer customer);
    long countByCustomerId(Long customerId);
}
