package org.example.ecommercefashion.entities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.enums.email.EmailTypeEnum;
import org.example.ecommercefashion.enums.email.LogStatusEnum;
import org.example.ecommercefashion.repositories.EmailRepository;
import org.example.ecommercefashion.repositories.EmailSendLogRepository;
import org.example.ecommercefashion.repositories.TemplateRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.EmailService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionEmail extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(PromotionEmail.class);
    private MimeMessage mimeMessage;
    private MimeMessageHelper helper;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private EmailSendLogRepository emailSendLogRepository;
    @Value("${spring.mail.username}")
    private String sendFrom;

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        mimeMessage = mailSender.createMimeMessage();
        helper = new MimeMessageHelper(mimeMessage, "utf-8");

        Template template = templateRepository.findTemplateBySubjectIgnoreCase("Notification Promotion");

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        String content = template.getHtml()
                .replace("{{startDate}}", today.toString())
                .replace("{{endDate}}", tomorrow.toString());

        // Lưu email
        Email emailTemplate = createEmail(template.getSubject());
        emailTemplate.setContent(template.getHtml());

        // Log trước khi gửi
        EmailSendLog sentLog = createEmailLog(emailTemplate);
        sentLog.setEmail(emailTemplate);
        log.info("Log created before email is sent.");

        for (String x : listEmail()) {
            try {
                helper.setTo(x);
                helper.setSubject(template.getSubject());
                helper.setText(content, true);
                helper.setFrom(sendFrom);
                mailSender.send(mimeMessage);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            sentLog.setStatus(LogStatusEnum.SUCCESS);
            sentLog.setSendTo(x);
            emailSendLogRepository.save(sentLog);

            log.info("Email sent successfully, log updated.");
        }
    }

    @PostConstruct
    public void checkMailSender() {
        if (mailSender != null) {
            log.info("mailSender has been injected successfully.");
        } else {
            log.error("mailSender injection failed.");
        }
    }

    public EmailSendLog createEmailLog(Email email) {
        EmailSendLog emailSendLog = new EmailSendLog();
        emailSendLog.setStatus(LogStatusEnum.FAILED);
        emailSendLogRepository.save(emailSendLog);
        return emailSendLog;
    }

    public List<String> listEmail() {
        List<String> email = new ArrayList<String>();
        email.add("quyennttph44488@fpt.edu.vn");
        email.add("htuquyenn@gmail.com");
        return email;
    }

    public Email createEmail(String subject) {
        Email email = emailRepository.findEmailBySubjectIgnoreCase(subject);

        if (email == null) {
            email = new Email();
            email.setSendFrom(sendFrom);
            email.setType(EmailTypeEnum.SCHEDULED);
            email.setSubject(subject);
            emailRepository.save(email);
        }
        return email;
    }

}
