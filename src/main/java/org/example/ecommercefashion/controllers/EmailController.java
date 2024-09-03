package org.example.ecommercefashion.controllers;

import org.example.ecommercefashion.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

//    @GetMapping("/send-email")
//    public String sendEmail() {
//        emailService.sendSimpleEmail("quyennttph44488@fpt.edu.vn", "Test Email", "This is a test email from Spring Boot using Mailgun.");
//        return "Email sent successfully";
//    }
}
