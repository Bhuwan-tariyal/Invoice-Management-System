package com.tariyal.service;


import com.itextpdf.html2pdf.HtmlConverter;
import com.tariyal.dao.CustomerDAO;
import com.tariyal.dao.InvoiceDAO;
import com.tariyal.dao.StudentDAO;
import com.tariyal.dto.InvoiceDownloadRequest;
import com.tariyal.dto.InvoiceRequest;
import com.tariyal.entity.Customer;
import com.tariyal.entity.Invoice;
import com.tariyal.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InvoiceService {
    @Autowired
    private  CustomerDAO customerDAO;
    @Autowired
    private  StudentDAO studentDAO;
    @Autowired
    private  InvoiceDAO invoiceDAO;

    public Invoice createInvoice(InvoiceRequest request,String email) {

        // 🔹 Fetch customer
        Customer customer = customerDAO.findByCustomerEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 🔹 Fetch student
        Student student = studentDAO.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 🔥 Validation (VERY IMPORTANT)
        if (!student.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Student does not belong to this customer");
        }

        // 🔹 Create invoice
        Invoice invoice = new Invoice();
        invoice.setAmount(request.getAmount());
        invoice.setDescription(request.getDescription());
        invoice.setHours(request.getHours());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(request.getDate(), formatter);
        invoice.setDate(date);
        invoice.setCustomer(customer);
        invoice.setStudent(student);

        return invoiceDAO.save(invoice);
    }


    public byte[] generatePdfFromHtml(String html) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, outputStream);
        return outputStream.toByteArray();
    }

    public String buildHtml(InvoiceDownloadRequest request, String email) {
        Customer customer = customerDAO.findByCustomerEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Student student = studentDAO.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fromDate = LocalDate.parse(request.getFromDate(), formatter);
        LocalDate toDate = LocalDate.parse(request.getToDate(), formatter);
        List<Invoice> invoices = invoiceDAO.findByCustomerAndStudentAndDateBetween(customer, student, fromDate, toDate);
        String rows = "";
        BigDecimal total = BigDecimal.ZERO;
        String companyName = customer.getCompanyName();
        String studentEmail = student.getEmail();
        String studentPhoneNumber = student.getPhoneNumber();
        for (Invoice item : invoices) {
            rows += "<tr>"
                    + "<td>" + item.getDescription() + " " + item.getDate().format(formatter) + "</td>"
                    + "<td class=\"text-center\">" + item.getHours() + "</td>"
                    + "<td class=\"text-right\">" + item.getAmount() + "</td>"
                    + "</tr>";
            total = total.add(item.getAmount());
        }

        String invoiceHtml = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        /* PDF specific fixes */
        @page { size: A4; margin: 0; }
        body { 
            font-family: Arial, sans-serif; 
            margin: 0; padding: 0; 
            background-color: #ffffff;
        }
        
        .invoice-card { 
            width: 700px; 
            margin: 0 auto; 
            background: white;
            min-height: 1000px; /* Forces the page height for PDF engine */
        }
        
        .top-banner {
            height: 160px;
            background: linear-gradient(to right, #e74c3c, #1a3066, #00adef);
            border-bottom-left-radius: 350px 50px;
            border-bottom-right-radius: 350px 50px;
            padding: 20px 40px;
            color: white;
        }
        
        .header-table { width: 100%; border-collapse: collapse; }
        .invoice-title h1 { font-size: 40px; margin: 0; color: white; }
        .invoice-title p { margin: 0; font-size: 14px; color: white; }
        
        .brand-name { 
            font-size: 22px; 
            border-bottom: 2px solid white; 
            padding-bottom: 5px;
        }

        .recipient-box-wrapper { padding: 0 40px; margin-top: -40px; }
        .recipient-box {
            background: white;
            border-radius: 10px;
            padding: 15px 25px;
            border: 1px solid #eee;
            width: 100%;
        }

        /* Main Content Area - Shrunk to fit footer */
        .main-content { padding: 20px 40px; min-height: 480px; }
        
        .items-table { width: 100%; border-collapse: collapse; font-size: 13px; }
        .items-table th { border-bottom: 2px solid #1a3066; padding: 8px; text-align: left; }
        .items-table td { padding: 8px; border-bottom: 1px solid #f2f2f2; color: #444; }
        
        .text-right { text-align: right !important; }
        .text-center { text-align: center !important; }

        .summary-wrapper { width: 100%; margin-top: 10px; }
        .summary-table { width: 250px; float: right; font-size: 14px; margin-bottom: 20px; }
        .total-line { border-top: 2px solid #1a3066; font-weight: bold; font-size: 18px; }

        .offer-bar {
            clear: both;
            background: #ffeded;
            padding: 10px;
            text-align: center;
            color: #d63031;
            font-weight: bold;
            border-left: 4px solid #e74c3c;
            margin-bottom: 20px;
        }

        /* Footer Fix: Push to bottom using margin-top instead of absolute */
        .footer-wrapper {
            margin-top: 50px; 
            padding: 0 40px 30px 40px;
        }
        .footer-table { 
            width: 100%;
            border-collapse: collapse;
            font-size: 12px; 
        }
        .social-btn { background: #ff7f50; color: white; padding: 4px 10px; border-radius: 3px; text-decoration: none; }
        .web-btn { background: #4caf50; color: white; padding: 4px 10px; border-radius: 3px; text-decoration: none; }
    </style>
</head>
<body>
<div class="invoice-card">
    <div class="top-banner">
        <table class="header-table">
            <tr>
                <td>
                    <div class="invoice-title">
                        <h1>INVOICE</h1>
                        <p>INVOICE NO: 756</p>
                    </div>
                </td>
                <td class="text-right">
                    <span class="brand-name">""" + companyName+ """
                    </span>
                </td>
            </tr>
        </table>
    </div>

    <div class="recipient-box-wrapper">
        <table class="recipient-box">
            <tr>
                <td>
                    <p style="color: #666; margin: 0; font-size: 12px;">Invoice to</p>
                    <h3 style="margin: 2px 0;">Dear,"""+ student.getParentName() +"""
                    </h3>
                </td>
                <td class="text-right" style="font-size: 12px;"> """ + studentEmail +"""
                    <br/>"""+ studentPhoneNumber + """
                </td>
            </tr>
        </table>
    </div>

    <div class="main-content">
        <table class="items-table">
            <thead>
                <tr>
                    <th style="width: 50%;">Description</th>
                    <th class="text-center">Hours</th>
                    <th class="text-center">Cost</th>
                </tr>
            </thead>
            <tbody> """+rows+"""
            </tbody>
        </table>

        <div class="summary-wrapper">
            <table class="summary-table">
                <tr>
                    <td>Reference Bonus</td>
                    <td class="text-right">$0</td>
                </tr>
                <tr class="total-line">
                    <td style="padding-top: 10px;">Total</td>
                    <td class="text-right" style="padding-top: 10px;">$"""+total+"""
                    </td>
                </tr>
            </table>
        </div>

        <div class="offer-bar">
            NOTE:- Get your 3 classes FREE per student referral!!
        </div>
        <div class="footer-wrapper">
            <table class="footer-table">
                <tr>
                    <td style="width: 50%; vertical-align: bottom;">
                        <p style="font-weight: bold; margin: 0 0 5px 0;">FOLLOW US</p>
                        <a href="#" class="social-btn">IG: thestudywave</a><br/>
                        <div style="margin-top: 10px;">
                            <a href="#" class="web-btn">WEB-PAGE: Thestudywave.com</a>
                        </div>
                    </td>
                    <td class="text-right" style="vertical-align: bottom;">
                        <p style="font-weight: bold; margin: 0; font-size: 14px;">Contact Us</p>
                        <p style="margin: 2px 0;">"""+customer.getCustomerPhone()+"""
                        </p>
                        <p style="margin: 2px 0;">"""+customer.getCustomerEmail()+"""
                        </p>
                    </td>
                </tr>
            </table>
        </div>
    </div>
    
</div>
</body>
</html>
""";
return invoiceHtml;
}
}
