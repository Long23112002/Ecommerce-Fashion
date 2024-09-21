package org.example.ecommercefashion.controllers;

import org.example.ecommercefashion.services.EmailService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/send-email")
    public String sendEmail() throws SchedulerException {
        emailService.sendPromotionalEmails();
        return "Email sent successfully";
    }
}
