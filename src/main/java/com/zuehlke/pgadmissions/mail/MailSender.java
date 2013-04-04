package com.zuehlke.pgadmissions.mail;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.EmailTemplateService;

public abstract class MailSender {

    private final Logger log = LoggerFactory.getLogger(MailSender.class);
	
    protected final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	
    protected final JavaMailSender javaMailSender;
	
    private final MessageSource messageSource;
    
    private final EmailTemplateService emailTemplateService;
	
    @Autowired
	public MailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource messageSource, EmailTemplateService emailTemplateService) {
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.javaMailSender = mailSender;
		this.messageSource = messageSource;
		this.emailTemplateService = emailTemplateService;
		
        if (this.javaMailSender instanceof JavaMailSenderImpl) {
            JavaMailSenderImpl impl = (JavaMailSenderImpl) this.javaMailSender;
            if (impl.getSession().getDebug()) {
                impl.getSession().setDebugOut(new PrintStream(new LogOutputStream() {
                    @Override
                    protected void processLine(String line, int level) {
                        log.debug(line);
                        if (StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(line), "QUIT")) {
                            log.debug("**********************************************************************");
                        }
                    }
                }));
            }
        }
    }

	protected String getAdminsEmailsCommaSeparatedAsString(List<RegisteredUser> administrators) {
		Set<String> administratorMails = new LinkedHashSet<String>();
		for (RegisteredUser admin : administrators) {
			administratorMails.add(admin.getEmail());
		}
		return StringUtils.join(administratorMails.toArray(new String[] {}), ";");
	}

	protected String resolveMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, null);
	}

	protected String resolveMessage(String code, ApplicationForm form, ApplicationFormStatus previousStage) {
		if (previousStage == null) {
			return resolveMessage(code, form);
		}
		RegisteredUser applicant = form.getApplicant();
		if (applicant == null) {
			throw new IllegalArgumentException("applicant must be provided!");
		}
		Object[] args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName(),
				previousStage.displayValue() };

		return messageSource.getMessage(code, args, null);
	}

	protected String resolveMessage(String code, ApplicationForm form) {
		RegisteredUser applicant = form.getApplicant();
		Object[] args;
		if (applicant == null) {
			args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle() };
		} else {
			args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName() };
		}
		return messageSource.getMessage(code, args, null);
	}

	protected final InternetAddress createAddress(RegisteredUser user) {
		try {
	        StringBuilder userNameBuilder = new StringBuilder(user.getFirstName());
	        if(user.getFirstName2() != null){
	            userNameBuilder.append(" " + user.getFirstName2());
	        }
	        if(user.getFirstName3() != null){
	            userNameBuilder.append(" " + user.getFirstName3());
	        }
	        userNameBuilder.append(" " + user.getLastName());
		    
			return new InternetAddress(user.getEmail(), userNameBuilder.toString());
		} catch (UnsupportedEncodingException uee) {// this shouldn't happen...
			throw new RuntimeException(uee);
		}
	}
	
	protected EmailTemplate getDefaultEmailtemplate(EmailTemplateName templateName) {
		return this.emailTemplateService.getActiveEmailTemplate(templateName);
	}

}