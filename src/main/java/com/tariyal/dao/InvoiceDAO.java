package com.tariyal.dao;

import com.tariyal.entity.Customer;
import com.tariyal.entity.Invoice;
import com.tariyal.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceDAO extends JpaRepository<Invoice,Long> {

    List<Invoice> findByCustomerAndStudent(Customer customer, Student student);

    List<Invoice> findByCustomerAndStudentAndDateBetween(Customer customer, Student student,
            LocalDate fromDate, LocalDate toDate);

//    @Query("SELECT i.amount FROM Invoice i WHERE i.customerId = :customerId")
//    List<BigDecimal> findAmountsByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.customer = :customer")
    BigDecimal getTotalAmountByCustomerId(@Param("customer") Customer customerId);

    long countByCustomerId(Long customerId);
}
