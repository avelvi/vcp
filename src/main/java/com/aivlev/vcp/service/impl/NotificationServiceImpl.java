package com.aivlev.vcp.service.impl;

import com.aivlev.vcp.model.NotificationReason;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.service.NotificationService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import javax.mail.internet.InternetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by aivlev on 5/9/16.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private static final String RECOVERY_CONTENT = "You received this email because you forgot " +
            "password from \"Video Content Portal\". " +
            "For reset your old password please click on the link below <\n>";

    private static final String ACTIVATION_CONTENT = "You received this email because you began " +
            "use \"Video Content Portal\". " +
            "For further use please activate your account by clicking on the link below <\n>";

    private static final String RECOVERY_PATH = "/#/recovery/code/";
    private static final String ACTIVATION_PATH = "/#/activate/code/";

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.fromEmail}")
    private String fromEmail;

    @Value("${email.fromName}")
    private String fromName;

    @Value("${email.sendTryCount}")
    private int tryCount;

    @Value("${base.url}")
    private String baseUrl;

    @PreDestroy
    private void preDestroy() {
        executorService.shutdown();
    }

    @Override
    public void sendNotification(User user, String code, String reason) {
        String email = user.getEmail();
        if (StringUtils.isNotBlank(email)) {
            String subject = "VCP";
            String content = buildContent(reason, code);
            String fullName = user.getName() + " " + user.getSurname();
            executorService.submit(new EmailItem(subject, content, email, fullName, tryCount));
        } else {
            LOGGER.error("Can't send email to user " + user.getName() + ": email not found!");
        }
    }

    private String buildContent(String reason, String code){
        boolean isActivation = reason.equals(NotificationReason.ACTIVATION.name());

        StringBuffer sb = new StringBuffer();
        sb.append(isActivation ? ACTIVATION_CONTENT : RECOVERY_CONTENT);
        sb.append(baseUrl);
        sb.append(isActivation ? ACTIVATION_PATH : RECOVERY_PATH);
        sb.append(code);
        return sb.toString();

    }

    private class EmailItem implements Runnable {
        private final String subject;
        private final String content;
        private final String destinationEmail;
        private final String name;
        private int tryCount;

        private EmailItem(String subject, String content, String destinationEmail, String name, int tryCount) {
            super();
            this.subject = subject;
            this.content = content;
            this.destinationEmail = destinationEmail;
            this.name = name;
            this.tryCount = tryCount;
        }

        @Override
        public void run() {
            try {
                LOGGER.debug("Send a new email to {}", destinationEmail);
                MimeMessageHelper message = new MimeMessageHelper(javaMailSender.createMimeMessage(), false);
                message.setSubject(subject);
                message.setTo(new InternetAddress(destinationEmail, name));
                message.setFrom(fromEmail, fromName);
                message.setText(content);
                MimeMailMessage msg = new MimeMailMessage(message);
                javaMailSender.send(msg.getMimeMessage());
                LOGGER.debug("Email to {} successful sent", destinationEmail);
            } catch (Exception e) {
                LOGGER.error("Can't send email to " + destinationEmail);
                tryCount--;
                if (tryCount > 0) {
                    LOGGER.debug("Decrement tryCount and try again to send email: tryCount={}, destinationEmail={}", tryCount, destinationEmail);
                    executorService.submit(this);
                } else {
                    LOGGER.error("Email not sent to " + destinationEmail);
                }
            }
        }
    }
}
