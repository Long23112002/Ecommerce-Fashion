package org.example.ecommercefashion.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.entities.EmailJob;
import org.example.ecommercefashion.services.EmailService;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private Scheduler scheduler;

    @Override
    public void scheduleEmail(String email) throws Exception {
        log.info("test {} ");
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

}
