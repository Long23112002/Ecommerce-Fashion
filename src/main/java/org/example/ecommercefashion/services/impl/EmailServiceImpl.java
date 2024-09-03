package org.example.ecommercefashion.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.entities.Email;
import org.example.ecommercefashion.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
//    @Autowired
//    private JavaMailSender mailSender;

    public void sendSimpleEmail(String to, String subject, String text) {
//        log.info("test 1");
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        message.setFrom("thuquyen01012004@gmail.com");
//        log.info("test 2");
//
//        mailSender.send(message);
//        log.info("test 3");
    }

    @Override
    public Email createEmail(String to, String subject, String text) {
        return null;
    }
}
