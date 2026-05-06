package com.tariyal.controller;


import com.tariyal.dao.InvoiceDAO;
import com.tariyal.dto.InvoiceDownloadRequest;
import com.tariyal.dto.InvoiceRequest;
import com.tariyal.entity.Invoice;
import com.tariyal.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/add")
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceRequest request, Authentication authentication) {
        String email = authentication.getName();
        invoiceService.createInvoice(request, email);
        return ResponseEntity.ok("Successfully created invoice");
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadInvoice(@RequestBody InvoiceDownloadRequest invoiceDownloadRequest, Authentication authentication) {
        String email = authentication.getName();
        String html = invoiceService.buildHtml(invoiceDownloadRequest, email);
        byte[] pdf = invoiceService.generatePdfFromHtml(html);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + 100 + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


}

