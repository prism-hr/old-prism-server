package com.zuehlke.pgadmissions.mail;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.Environment;

public abstract class AbstractMailSendingService {
	

    private final MailSender mailSender;
    
    protected final ApplicationFormDAO applicationDAO;
    
    protected final ConfigurationService configurationService;
    
    protected class UpdateDigestNotificationClosure implements Closure {
        private final DigestNotificationType type;

        public UpdateDigestNotificationClosure(final DigestNotificationType type) {
            this.type = type;
        }

        @Override
        public void execute(final Object input) {
           setDigestNotificationType((RegisteredUser) input, type);
        }
    }
    
    protected void setDigestNotificationType(final RegisteredUser user, final DigestNotificationType type) {
        DigestNotificationType currentType = user.getDigestNotificationType();
        if (currentType == null || type == DigestNotificationType.NONE) {
            user.setDigestNotificationType(type);
        } else if (currentType.getPriority() < type.getPriority()) {
            user.setDigestNotificationType(type);
        }
    }
    
    
    public AbstractMailSendingService(final MailSender mailSender,  final ApplicationFormDAO formDAO, final ConfigurationService configurationService) {
		this.mailSender = mailSender;
		applicationDAO = formDAO;
		this.configurationService = configurationService;
    }
    
    
    protected String getAdminsEmailsCommaSeparatedAsString(List<RegisteredUser> administrators) {
		Set<String> administratorMails = new LinkedHashSet<String>();
		for (RegisteredUser admin : administrators) {
			administratorMails.add(admin.getEmail());
		}
		return StringUtils.join(administratorMails.toArray(new String[] {}), ", ");
	}
    
    @SuppressWarnings("unchecked")
    protected Collection<RegisteredUser> getInterviewersFromLatestInterviewRound(final ApplicationForm form) {
        if (form.getLatestInterview() != null) {
            return CollectionUtils.collect(form.getLatestInterview().getInterviewers(), new Transformer() {
                @Override
                public Object transform(final Object input) {
                    return ((Interviewer) input).getUser();
                }
            });
        }
        return Collections.emptyList();
    }
    
    @SuppressWarnings("unchecked")
    protected Collection<RegisteredUser> getReviewersFromLatestReviewRound(final ApplicationForm form) {
        if (form.getLatestReviewRound() != null) {
            return CollectionUtils.collect(form.getLatestReviewRound().getReviewers(), new Transformer() {
                @Override
                public Object transform(final Object input) {
                    return ((Reviewer) input).getUser();
                }
            });
        }
        return Collections.emptyList();
    }
    
    protected PrismEmailMessage buildMessage(RegisteredUser recipient, String subject, Map<String, Object> model, EmailTemplateName templateName) {
        return buildMessage(recipient, null, subject, model, templateName);
    }

    protected PrismEmailMessage buildMessage(RegisteredUser recipient, List<RegisteredUser> ccRecipients, String subject, Map<String, Object> model, EmailTemplateName templateName) {
        return new PrismEmailMessageBuilder().to(recipient).cc(ccRecipients).subject(subject).model(model).emailTemplate(templateName).build();
    }
    
    protected PrismEmailMessage buildMessage(List<RegisteredUser> recipients, List<RegisteredUser> ccRecipients, String subject, Map<String, Object> model, EmailTemplateName templateName) {
    	return new PrismEmailMessageBuilder().to(recipients).cc(ccRecipients).subject(subject).model(model).emailTemplate(templateName).build();
    }

    protected EmailModelBuilder getModelBuilder(final String[] keys, final Object[] values) {
        return new EmailModelBuilder() {
            @Override
            public Map<String, Object> build() {
                Map<String, Object> model = new HashMap<String, Object>();
                for (int i = 0; i < keys.length; i++) {
                    model.put(keys[i], values[i]);
                }
                return model;
            }
        };
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

		return resolveMessage(code, args);
	}
    
    protected String resolveMessage(String subjectCode, ApplicationForm applicationForm) {
        RegisteredUser applicant = applicationForm.getApplicant();
        if (applicant == null) {
            return mailSender.resolveMessage(subjectCode, applicationForm.getApplicationNumber(), applicationForm.getProgram().getTitle());
        } else {
            return mailSender.resolveMessage(subjectCode, applicationForm.getApplicationNumber(), applicationForm
                    .getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName());
        }
    }
    
    protected String resolveMessage(String subjectCode, Object[] args) {
    	return mailSender.resolveMessage(subjectCode, args);
    }
    
    protected void sendEmail(PrismEmailMessage message) {
    	mailSender.sendEmail(message);
    }
    
    protected void sendEmail(PrismEmailMessage... messages) {
    	mailSender.sendEmail(messages);
    }
    
    protected String getHostName() {
    	return Environment.getInstance().getApplicationHostName();
    }
}
