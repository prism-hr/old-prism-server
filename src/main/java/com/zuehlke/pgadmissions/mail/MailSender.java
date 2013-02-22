package com.zuehlke.pgadmissions.mail;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public abstract class MailSender {

    private final Logger log = Logger.getLogger(MailSender.class);
	protected final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
	protected final JavaMailSender javaMailSender;
	private final MessageSource messageSource;
	
	public MailSender(MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailSender, MessageSource messageSource) {
		this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
		this.javaMailSender = mailSender;
		this.messageSource = messageSource;
		
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
			return new InternetAddress(user.getEmail(), user.getFirstName() + " " + user.getLastName());
		} catch (UnsupportedEncodingException uee) {// this shouldn't happen...
			throw new RuntimeException(uee);
		}
	}

}