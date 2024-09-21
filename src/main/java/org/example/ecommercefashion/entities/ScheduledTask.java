package org.example.ecommercefashion.entities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.enums.email.EmailTypeEnum;
import org.example.ecommercefashion.enums.email.LogStatusEnum;
import org.example.ecommercefashion.repositories.EmailRepository;
import org.example.ecommercefashion.repositories.EmailSendLogRepository;
import org.example.ecommercefashion.repositories.TemplateRepository;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class ScheduledTask {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sendFrom;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private EmailSendLogRepository emailSendLogRepository;
    private MimeMessage mimeMessage;
    private MimeMessageHelper helper;
    @Scheduled(cron = "0 8 22 * * ?")
    public void performTask() throws MessagingException {
        log.info("testtttttttttttt");
        System.out.println("jdcdcfd");
//        String email = (String) context.getMergedJobDataMap().get("email");
        mimeMessage = mailSender.createMimeMessage();
        helper = new MimeMessageHelper(mimeMessage, "utf-8");

        Template template = templateRepository.findTemplateBySubjectIgnoreCase("Notification Promotion");

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        String content = template.getHtml()
                .replace("{{startDate}}", today.toString())
                .replace("{{endDate}}", tomorrow.toString());

        // Lưu email vào log
        Email emailLog = createEmail(template.getSubject());
        emailLog.setContent(template.getHtml());
        log.info("Email log created.");

        // Log trước khi gửi
        EmailSendLog sentLog = createEmailLog(emailLog);
        sentLog.setEmail(emailLog);
        log.info("Log created before email is sent.");

//        try {
            for (String x : listEmail()) {
                helper.setTo(x);
                helper.setSubject(template.getSubject());
                helper.setText(content, true);
                helper.setFrom(sendFrom);
                mailSender.send(mimeMessage);

                sentLog.setStatus(LogStatusEnum.SUCCESS);
                emailSendLogRepository.save(sentLog);

                log.info("Email sent successfully, log updated.");
            }
//        } catch (MessagingException e) {
//            throw new JobExecutionException(e);
//        }
    }
    public List<String> listEmail() {
        List<String> email = new ArrayList<String>();
        email.add("quyennttph44488@fpt.edu.vn");
        email.add("htuquyen@gmail.com");
        return email;
    }

    public Email createEmail(String subject) {
        Email email = emailRepository.findEmailBySubjectIgnoreCase(subject);

        if (email == null) {
            email = new Email();
            email.setSendFrom(sendFrom);
            email.setType(EmailTypeEnum.IMMEDIATE);
            email.setSubject(subject);
            emailRepository.save(email);
        }
        return email;
    }

    public EmailSendLog createEmailLog(Email email) {
        EmailSendLog emailSendLog = new EmailSendLog();
        emailSendLog.setStatus(LogStatusEnum.FAILED);
        emailSendLogRepository.save(emailSendLog);
        return emailSendLog;
    }
}
