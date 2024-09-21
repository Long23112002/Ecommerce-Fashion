package org.example.ecommercefashion.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.entities.Email;
import org.example.ecommercefashion.entities.EmailJob;
import org.example.ecommercefashion.entities.PromotionEmail;
import org.example.ecommercefashion.enums.email.EmailTypeEnum;
import org.example.ecommercefashion.repositories.EmailRepository;
import org.example.ecommercefashion.repositories.TemplateRepository;
import org.example.ecommercefashion.services.EmailService;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final Scheduler scheduler;
    private final TemplateRepository templateRepository;
    private final EmailRepository emailRepository;
    @Value("${spring.mail.username}")
    private String sendFrom;

    List<String> emailList = new ArrayList<>();

    @Override
    public void sendingOtpWithEmail(String email) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(EmailJob.class)
                .withIdentity("emailJob", "emailGroup")
                .usingJobData("email", email)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey("emailTrigger", "emailGroup"))
                .startNow()
                .withSchedule(simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    @Override
    public void sendPromotionalEmails() throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(PromotionEmail.class)
                .withIdentity("promotionalEmailJob")
                .storeDurably()
                .build();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date eventDate = null;
        try {
            eventDate = dateFormat.parse("21/09/2024");
            System.out.println("Day khai báo là: " + eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("with schedule " + CronScheduleBuilder.cronSchedule(generateCronForOneDayBefore(eventDate)));
        System.out.println("eventDate " + generateCronForOneDayBefore(eventDate));

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("preEventTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(generateCronForOneDayBefore(eventDate)))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

//        ExecutorService executorService = Executors.newFixedThreadPool(10);

//        for (String email : emailList) {
//            // Gửi từng email trong một luồng riêng biệt
////            executorService.submit(() -> {
//                try {
//                    sendEmail(email, subject, text);
//                } catch (Exception e) {
//                    System.err.println("Gửi email thất bại cho: " + email);
//                    e.printStackTrace();
//                }
////            });
//        }

        // Tắt ExecutorService sau khi các nhiệm vụ đã hoàn tất
//        executorService.shutdown();
    }

    public List<String> getEmailList(){
        emailList.add("quyennttph44488@fpt.edu.vn");
        emailList.add("htuquyenn@gmail.com");
        return emailList;
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

    private void sendEmail(String sendTo, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sendTo);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }


    public String generateCronForOneDayBefore(Date promotionDate) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(promotionDate);
        cal.add(Calendar.DAY_OF_YEAR, -1); // Trừ 1 ngày

        System.out.println("cal " + cal);

        Date oneDayBefore = cal.getTime();
        System.out.println("oneDayBefore " + oneDayBefore);

        SimpleDateFormat dayFormat = new SimpleDateFormat("d");
        SimpleDateFormat monthFormat = new SimpleDateFormat("M");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        String day = dayFormat.format(oneDayBefore);
        String month = monthFormat.format(oneDayBefore);
        String year = yearFormat.format(oneDayBefore);

        // Tạo cron cho 21:00 PM trước 1 ngày
        return String.format("0 20 8 %s %s ? %s", day, month, year);
    }
}
