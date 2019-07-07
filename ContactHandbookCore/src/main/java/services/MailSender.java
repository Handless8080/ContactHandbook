package services;

import dao.DataConnection;
import dto.Contact;
import org.apache.log4j.Logger;
import org.stringtemplate.v4.ST;
import utils.StringUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender {
    private final static Logger logger = Logger.getLogger(MailSender.class);

    private final static String from = "matrixcalc204@gmail.com";
    private final static String password = "qWeRt123y";

    public static void send(String to, String subject, String text, String contactId) {
        Properties properties = System.getProperties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.host", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.debug", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            message.setSubject(subject, "UTF-8");

            if (!StringUtils.isEmpty(contactId)) {
                Contact addressee = DataConnection.getContactById(contactId);

                if (addressee != null) {
                    ST st = new ST(text);
                    st.add("name", addressee.getName());
                    st.add("surname", addressee.getSurname());
                    st.add("patronymic", addressee.getPatronymic());

                    text = st.render();
                }
            }

            message.setText(text, "UTF-8");
            Transport.send(message);

            logger.info("Email sent successfully; to = " + to + " from = " + from + " subject = " + subject + " text = " + text);
        } catch (MessagingException e) {
            logger.error("Email sending failed; to = " + to + " from = " + from + " subject = " + subject + " text = " + text, e);

            e.printStackTrace();
        }
    }
}