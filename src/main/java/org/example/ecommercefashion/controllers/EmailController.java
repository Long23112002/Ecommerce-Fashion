package org.example.ecommercefashion.controllers;

import org.example.ecommercefashion.services.EmailService;
import org.example.ecommercefashion.services.ExcelService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private ExcelService excelService;

    @GetMapping("/send-email-promotion")
    public String sendEmail() throws SchedulerException {
        emailService.sendPromotionalEmails();
        return "Email sent successfully";
    }

    @GetMapping("/get-listEmail")
    public List<String> getListEmail(@RequestParam MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            System.out.println("jhjh");
        }
        return excelService.getListEmailFromExcel(file);
    }

    @PostMapping("/send-external")
    public String sendEmailForUserFromExcel(@RequestParam MultipartFile file) throws SchedulerException, IOException {
        emailService.sendBulkMailFromExcel(file);
        return "Email sent successfully";
    }
}
