package org.example.ecommercefashion.entities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.enums.email.EmailTypeEnum;
import org.example.ecommercefashion.enums.email.LogStatusEnum;
import org.example.ecommercefashion.repositories.EmailRepository;
import org.example.ecommercefashion.repositories.EmailSendLogRepository;
import org.example.ecommercefashion.repositories.OrderRepository;
import org.example.ecommercefashion.repositories.TemplateRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.OTPService;
import org.quartz.*;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
  private UserRepository userRepository;
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
  public void OrdersuccessfulEmail(Order order) throws JobExecutionException {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
      Template template = templateRepository.findTemplateBySubjectIgnoreCase("Order Confirmation");
      if (template == null) {
        throw new JobExecutionException("Template for 'Order Confirmation' not found");
      }
      String email = order.getUser().getEmail();
      String fullName = order.getUser().getFullName();
      String orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreatedAt());
//      String productName = String.valueOf(order.getOrderDetails().get(0).getProductDetail().getProduct().getName()) ;
//      String quantity = String.valueOf(order.getOrderDetails().get(0).getQuantity());
//      String price = String.valueOf(order.getOrderDetails().get(0).getPrice());
      String finalPrice = String.valueOf(order.getFinalPrice());
      String shipping = String.valueOf(order.getMoneyShip());

      StringBuilder productDetailsHtml = new StringBuilder();
      for (OrderDetail detail : order.getOrderDetails()) {
        String productName = detail.getProductDetail().getProduct().getName();
        String quantity = String.valueOf(detail.getQuantity());
        String price = String.valueOf(detail.getPrice());

        productDetailsHtml.append("<div class='item'>")
                .append("<div class='item-details'>")
                .append("<div>").append(productName).append("</div>")
                .append("<div>Số lượng: ").append(quantity).append("</div>")
                .append("</div>")
                .append("<div class='item-price'>").append(price).append(" đ</div>")
                .append("</div>");
      }
      log.info("Sending order confirmation email - fullName: {}, orderDate: {}, finalPrice: {}, shipping: {}, email: {}",
              fullName, orderDate, finalPrice, shipping, email);
      String content = template.getHtml()
              .replace("{{fullName}}", fullName)
              .replace("{{orderDate}}", orderDate)
              .replace("{{productDetails}}", productDetailsHtml.toString())
              .replace("{{finalPrice}}", finalPrice)
              .replace("{{shipping}}", shipping);

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
      throw new JobExecutionException("Không gửi được email", e);
    } catch (Exception e) {
      throw new JobExecutionException("Đã xảy ra lỗi khi gửi email xác nhận đơn hàng", e);
    }
  }
}
