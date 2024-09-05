package org.example.ecommercefashion.entities;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailJob implements Job {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sendFrom;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String email = (String) context.getMergedJobDataMap().get("email");
        System.out.println(sendFrom);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            helper.setTo(email);
            helper.setSubject("Sign in successfully");
            helper.setText("Welcome!", true);
            helper.setFrom(sendFrom);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new JobExecutionException(e);
        }
    }
}
