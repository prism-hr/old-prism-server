package com.zuehlke.pgadmissions.mail.refactor;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public abstract class AbstractMailSender {

    private final Logger log = LoggerFactory.getLogger(AbstractMailSender.class);
    
    protected final JavaMailSender javaMailSender;
    
    protected final MessageSource messageSource;
    
    protected final boolean emailProductionSwitch;
    
    protected final String emailAddressFrom;
    
    protected final String emailAddressTo;
    
    public AbstractMailSender(
            final JavaMailSender mailSender, 
            final MessageSource messageSource, 
            @Value("${email.prod}") final String production,
            @Value("${email.address.from}") final String emailAddressFrom,  
            @Value("${email.address.to}") final String emailAddressTo) {
        this.javaMailSender = mailSender;
        this.messageSource = messageSource;
        this.emailProductionSwitch = BooleanUtils.toBoolean(production);
        this.emailAddressFrom = emailAddressFrom;
        this.emailAddressTo = emailAddressTo;
        enableLoggingOfSMTPCommands();
    }
    
    private void enableLoggingOfSMTPCommands() {
        if (javaMailSender instanceof JavaMailSenderImpl) {
            JavaMailSenderImpl impl = (JavaMailSenderImpl) this.javaMailSender;
            if (impl.getSession().getDebug()) {
                impl.getSession().setDebugOut(new PrintStream(new LogOutputStream() {
                    @Override
                    protected void processLine(String line, int level) {
                        log.trace(line);
                        if (StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(line), "QUIT")) {
                            log.trace("**********************************************************************");
                        }
                    }
                }));
            }
        }
    }
    
    protected String resolveMessage(final String code, final Object... args) {
        return messageSource.getMessage(code, args, null);
    }
    
    public void sendEmail(final PrismEmailMessage... emailMessage) {
        sendEmail(Arrays.asList(emailMessage));
    }

    public void sendEmail(final Collection<PrismEmailMessage> emailMessages) {
        for (PrismEmailMessage message : emailMessages) {
            if (emailProductionSwitch) {
                sendEmailAsProductionMessage(message);
            } else {
                sendEmailAsDevelopmentMessage(message);
            }
        }
    }
    
    abstract void sendEmailAsProductionMessage(final PrismEmailMessage message);
    
    abstract void sendEmailAsDevelopmentMessage(final PrismEmailMessage message);
}
