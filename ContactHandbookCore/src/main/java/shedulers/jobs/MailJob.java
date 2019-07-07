package shedulers.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import services.ContactService;
import services.MailSender;
import utils.StringUtils;

public class MailJob implements Job {
    private final static Logger logger = Logger.getLogger(MailJob.class);

    private final static String to = "vacya9@gmail.com";
    private final static String subject = "Оповещение о днях рождения контактов";
    private final static String text = "Сегодня у следующих контактов день рождения:\n";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        String contacts = ContactService.getContactsByBornDate();

        if (!StringUtils.isEmpty(contacts)) {
            MailSender.send(to, subject, text + contacts, null);

            logger.info("Mail scheduler sent email successfully; contacts = " + contacts);
        }
    }
}