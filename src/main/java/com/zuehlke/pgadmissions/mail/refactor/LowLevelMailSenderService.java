package com.zuehlke.pgadmissions.mail.refactor;

import java.io.PrintStream;

import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.services.EmailTemplateService;

@Service
class LowLevelMailSenderService {

private final Logger log = LoggerFactory.getLogger(LowLevelMailSenderService.class);
    
    protected final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
    
    protected final JavaMailSender javaMailSender;
    
    private final MessageSource messageSource;
    
    private final EmailTemplateService emailTemplateService;
    
    @Autowired
    public LowLevelMailSenderService(final MimeMessagePreparatorFactory mimeMessagePreparatorFactory, 
            final JavaMailSender mailSender, final MessageSource messageSource, 
            final EmailTemplateService emailTemplateService) {
        this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
        this.javaMailSender = mailSender;
        this.messageSource = messageSource;
        this.emailTemplateService = emailTemplateService;
        enableLoggingOfSMTPCommands();
    }
    
    private void enableLoggingOfSMTPCommands() {
        if (this.javaMailSender instanceof JavaMailSenderImpl) {
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
    
    private void sendEmail(final PrismEmailMessage emailMessage) {
    
    }
}
