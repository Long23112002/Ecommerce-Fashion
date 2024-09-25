package org.example.ecommercefashion.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.schedule.EmailJob;
import org.example.ecommercefashion.schedule.PromotionEmail;
import org.example.ecommercefashion.services.EmailService;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final Scheduler scheduler;

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
//
//        for (String email : emailList) {
//            // Gửi từng email trong một luồng riêng biệt
//            executorService.submit(() -> {
//                try {
//                    sendEmail(email, subject, text);
//                } catch (Exception e) {
//                    System.err.println("Gửi email thất bại cho: " + email);
//                    e.printStackTrace();
//                }
//            });
//        }
//
//        Tắt ExecutorService sau khi các nhiệm vụ đã hoàn tất
//        executorService.shutdown();
    }

    public String generateCronForOneDayBefore(Date promotionDate) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(promotionDate);
        cal.add(Calendar.DAY_OF_YEAR, -1); // Trừ 1 ngày

        Date oneDayBefore = cal.getTime();
        System.out.println("oneDayBefore " + oneDayBefore);

        SimpleDateFormat dayFormat = new SimpleDateFormat("d");
        SimpleDateFormat monthFormat = new SimpleDateFormat("M");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        String day = dayFormat.format(oneDayBefore);
        String month = monthFormat.format(oneDayBefore);
        String year = yearFormat.format(oneDayBefore);

        // Tạo cron cho 8:20 AM trước 1 ngày
        return String.format("0 20 8 %s %s ? %s", day, month, year);
    }
}
