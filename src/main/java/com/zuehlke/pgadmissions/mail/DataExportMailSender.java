package com.zuehlke.pgadmissions.mail;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;

@Component
public class DataExportMailSender {

    private final UserService userService;
    
    private final MessageSource messageSource;
    
    private final JavaMailSender mailSender;
    
    private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
    
    private final Logger log = LoggerFactory.getLogger(DataExportMailSender.class);
    
    public DataExportMailSender() {
        this(null, null, null, null);
    }
    
    @Autowired
    public DataExportMailSender(final MimeMessagePreparatorFactory mimeMessagePreparatorFactory, 
            final JavaMailSender mailSender, final MessageSource messageSource, final UserService userService) {
        this.userService = userService;
        this.mailSender = mailSender;
        this.messageSource = messageSource;
        this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
    }
    
    Map<String, Object> createModel(final RegisteredUser user, final String message) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        model.put("message", message);
        model.put("time", new Date());
        model.put("host", Environment.getInstance().getApplicationHostName());
        return model;
    }
    
    public void sendErrorMessage(final String message, final Exception exception) {
        StringBuilder builder = new StringBuilder();
        builder.append(message).append("\n").append(exception.getMessage()).append("\n\n")
                .append(ExceptionUtils.getFullStackTrace(exception.getCause()));
        sendErrorMessage(builder.toString());
    }

    public void sendErrorMessage(final String message) {
        try {
            List<RegisteredUser> superadmins = userService.getUsersInRole(Authority.SUPERADMINISTRATOR);
            for (RegisteredUser user : superadmins) {           
                internalSendMail("reference.data.export.error", message, user, "private/mail/export_error.ftl");
            }
        } catch (Exception e) {
            log.warn("Error while sending email", e);
        }
    }

    private void internalSendMail(final String subjectCode, final String message, final RegisteredUser user,
            final String template) throws UnsupportedEncodingException {
        
        InternetAddress toAddress = new InternetAddress(user.getEmail(), user.getFirstName() + " " + user.getLastName());
        String subject = messageSource.getMessage(subjectCode, null, null);
        mailSender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject, template, createModel(user, message), null));
    }
}
