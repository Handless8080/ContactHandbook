package shedulers;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import shedulers.jobs.MailJob;

public class MailScheduler {
    private final static Logger logger = Logger.getLogger(MailScheduler.class);

    public MailScheduler() {
        try {
            startSchedule();

            logger.info("Mail scheduler started successfully");
        } catch (SchedulerException e) {
            logger.error("Mail scheduler starting failed", e);

            e.printStackTrace();
        }
    }

    private static void startSchedule() throws SchedulerException {
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("mailSend")
                .startAt(DateBuilder.todayAt(23, 59, 0))
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ? *"))
                .build();

        JobDetail job = new JobDetailImpl();
        ((JobDetailImpl) job).setName("mailJob");
        ((JobDetailImpl) job).setJobClass(MailJob.class);

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }
}