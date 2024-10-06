package org.example.ecommercefashion.schedule;

import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.entities.Email;
import org.example.ecommercefashion.entities.EmailSendLog;
import org.example.ecommercefashion.entities.ProcessSend;
import org.example.ecommercefashion.entities.Template;
import org.example.ecommercefashion.enums.email.EmailTypeEnum;
import org.example.ecommercefashion.enums.email.LogStatusEnum;
import org.example.ecommercefashion.repositories.EmailRepository;
import org.example.ecommercefashion.repositories.EmailSendLogRepository;
import org.example.ecommercefashion.repositories.TemplateRepository;
import org.example.ecommercefashion.services.ExcelService;
import org.example.ecommercefashion.services.ProcessService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
//@RequiredArgsConstructor
@Slf4j
public class PromotionExcelJob implements Job {
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
    @Value("${MAIL_PROCESS_SIZE}")
    private int PROCESS_SIZE;
    @Value("${spring.mail.username}")
    private String sendFrom;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private ProcessService processService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        sendBulkMailFromExcel(jobExecutionContext.);

        // check status = failed thi resend
    }

    public void sendBulkMailFromExcel(MultipartFile file) throws IOException {
        Template template = templateRepository.findTemplateBySubjectIgnoreCase("Notification Promotion");

        Email emailTemplate = createEmail(template.getSubject());
        emailTemplate.setContent(template.getHtml());

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        String content = template.getHtml()
                .replace("{{startDate}}", today.toString())
                .replace("{{endDate}}", tomorrow.toString());


        int pageNumber = 0;
        Pageable pageable;
        Page<String> pageSendTo;

        do {
            pageable = PageRequest.of(pageNumber, PROCESS_SIZE);
            pageSendTo = countPage(pageable, file);

            ProcessSend processSend = processService.initializeProcess(emailTemplate);

            pageSendTo.getContent().forEach(emailUser -> {
                EmailSendLog sentLog = createEmailLog(emailTemplate);
                sentLog.setEmail(emailTemplate);
                sentLog.setSendTo(emailUser);
                sentLog.setProcessSend(processSend);

                log.info("emailUser: {}", emailUser);
                // gửi email
                try {
                    mimeMessage = mailSender.createMimeMessage();
                    helper = new MimeMessageHelper(mimeMessage, true);

                    helper.setTo(emailUser);
                    helper.setSubject(template.getSubject());
                    helper.setText(content, true);
                    helper.setFrom(sendFrom);

                    mailSender.send(mimeMessage);
                    sentLog.setStatus(LogStatusEnum.SUCCESS);

                } catch (MessagingException e) {
                    sentLog.setStatus(LogStatusEnum.FAILED);
                    throw new RuntimeException(e);
                }

                emailSendLogRepository.save(sentLog);

            });
            pageNumber++;
        } while (pageSendTo.hasNext());
    }

    private Page<String> countPage(Pageable pageable, MultipartFile file) throws IOException {
        List<String> listEmail = excelService.getListEmailFromExcel(file);

        listEmail = listEmail.stream()
                .filter(email -> email != null && !email.trim().isEmpty())
                .collect(Collectors.toList());

        int countTotalEmail = listEmail.size();

        // offset = pageNumber * pageSize
        int start = (int) pageable.getOffset();

        int end = Math.min((start + pageable.getPageSize()), countTotalEmail);

        // Nếu start vượt quá kích thước của danh sách, trả về trang rỗng
        if (start > end) {
            return new PageImpl<>(List.of(), pageable, countTotalEmail);
        }

        // lấy từ vị trí start đến (end - 1)
        List<String> subList = listEmail.subList(start, end);
        return new PageImpl<>(subList, pageable, countTotalEmail);
    }

    public EmailSendLog createEmailLog(Email email) {
        EmailSendLog emailSendLog = new EmailSendLog();
        emailSendLog.setStatus(LogStatusEnum.FAILED);
        emailSendLogRepository.save(emailSendLog);
        return emailSendLog;
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
