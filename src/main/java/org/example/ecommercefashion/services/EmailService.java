package org.example.ecommercefashion.services;

import org.quartz.SchedulerException;

import java.util.List;

public interface EmailService {
    void sendingOtpWithEmail(String email) throws Exception;
    void sendPromotionalEmails() throws SchedulerException;

}
