package org.example.ecommercefashion.services;

import org.quartz.SchedulerException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EmailService {
    void sendingOtpWithEmail(String email) throws Exception;

    void sendPromotionalEmails() throws SchedulerException;

    void sendBulkMailFromExcel(MultipartFile file) throws IOException, SchedulerException;
}
