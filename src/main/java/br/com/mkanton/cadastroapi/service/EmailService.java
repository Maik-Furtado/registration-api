package br.com.mkanton.cadastroapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

/**
 * Service for sending verification emails using JavaMail API.
 *
 * SMTP credentials must be defined as environment variables:
 * EMAIL_SMTP_USER and EMAIL_SMTP_PASSWORD.
 */
@ApplicationScoped
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Inject
    ServletContext context;



    // Safer alternative for production:
    //you must configure them on your preferred server.
    // (Uncomment the lines below to use system or server-configured environment variables
    //and comment the servlet config in method sendEmail

    // private static final String emailSmpt = System.getenv("EMAIL_USERNAME");
    // private  static final String passwordSmpt = System.getenv("EMAIL_PASSWORD");





    /**
     * Send a Simple verification email with provide code.
     * @param recipient The email address to send to.
     * @param code The verification code to include in the message.
     * @throws RuntimeException If email fails to send.
     */
    public void sendEmail(String recipient, String code) {

        // Default mode: load variables directly from web.xml to facilitate local testing.
        String emailSmpt = context.getInitParameter("email.smtp.user");
        String passwordSmpt = context.getInitParameter("email.smtp.password");

        if ( emailSmpt== null || passwordSmpt == null) {
            throw new IllegalStateException("E-mail credentials are not configured!");
        }
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");


        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( emailSmpt, passwordSmpt);}
        });

        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailSmpt));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Verification Code");
            message.setText("your verification code is: " + code);

            Transport.send(message);
            logger.info("Email sent successfully to {}", recipient);
        }
        catch (MessagingException e) {
            logger.error("Failed to send email to {}", recipient, e);
            throw new RuntimeException("Error sending email", e);
        }
    }
}
