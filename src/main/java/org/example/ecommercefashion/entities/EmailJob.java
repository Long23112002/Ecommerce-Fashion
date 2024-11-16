package org.example.ecommercefashion.entities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.enums.email.EmailTypeEnum;
import org.example.ecommercefashion.enums.email.LogStatusEnum;
import org.example.ecommercefashion.repositories.EmailRepository;
import org.example.ecommercefashion.repositories.EmailSendLogRepository;
import org.example.ecommercefashion.repositories.TemplateRepository;
import org.example.ecommercefashion.services.OTPService;
import org.quartz.*;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailJob implements Job {
  @Autowired private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String sendFrom;

  private final OTPService otpService;
  private final TemplateRepository templateRepository;
  private final EmailRepository emailRepository;
  private final EmailSendLogRepository emailSendLogRepository;
  private MimeMessage mimeMessage;
  private MimeMessageHelper helper;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    String email = (String) context.getMergedJobDataMap().get("email");
    mimeMessage = mailSender.createMimeMessage();
    helper = new MimeMessageHelper(mimeMessage, "utf-8");

    Template template = templateRepository.findTemplateBySubjectIgnoreCase("Verification code");
    if (template == null) {
      throw new JobExecutionException("Template for 'Verification code' not found");
    }
    String otp = otpService.generateOTP();
    otpService.saveOtp(email, otp);

    log.info("email user : {}", email);
    log.info("otp user : {}", otp);

    String content = template.getHtml().replace("{{email}}", email).replace("{{OTP}}", otp);

    // Lưu email vào log
    Email emailLog = createEmail(template.getSubject());
    emailLog.setContent(template.getHtml());
    log.info("Email log created.");

    // Log trước khi gửi
    EmailSendLog sentLog = createEmailLog(emailLog);
    sentLog.setEmail(emailLog);
    sentLog.setSendTo(email);
    log.info("Log created before email is sent.");

    try {
      helper.setTo(email);
      helper.setSubject(template.getSubject());
      helper.setText(content, true);
      helper.setFrom(sendFrom);
      mailSender.send(mimeMessage);

      sentLog.setStatus(LogStatusEnum.SUCCESS);
      emailSendLogRepository.save(sentLog);

      log.info("Email sent successfully, log updated.");

    } catch (MessagingException e) {
      throw new JobExecutionException(e);
    }
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

  @Async
  public void sendOtpEmail(String email) throws JobExecutionException {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
      Template template = templateRepository.findTemplateBySubjectIgnoreCase("Verification code");
      if (template == null) {
        throw new JobExecutionException("Template for 'Verification code' not found");
      }

      String otp = otpService.generateOTP();
      otpService.saveOtp(email, otp);

      log.info("Email user: {}", email);
      log.info("OTP user: {}", otp);

      String content = template.getHtml().replace("{{email}}", email).replace("{{OTP}}", otp);

      Email emailLog = createEmail(template.getSubject());
      emailLog.setContent(template.getHtml());

      EmailSendLog sentLog = createEmailLog(emailLog);
      sentLog.setEmail(emailLog);
      sentLog.setSendTo(email);

      helper.setTo(email);
      helper.setSubject(template.getSubject());
      helper.setText(content, true);
      helper.setFrom(sendFrom);
      mailSender.send(mimeMessage);

      sentLog.setStatus(LogStatusEnum.SUCCESS);
      emailSendLogRepository.save(sentLog);
    } catch (MessagingException e) {
      throw new JobExecutionException("Failed to send email", e);
    } catch (Exception e) {
      throw new JobExecutionException("An error occurred while sending OTP email", e);
    }
  }
}
