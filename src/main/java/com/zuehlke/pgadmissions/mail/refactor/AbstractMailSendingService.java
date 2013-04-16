package com.zuehlke.pgadmissions.mail.refactor;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.UserService;

@Service
public abstract class AbstractMailSendingService {

    protected final UserService userService;
    
    private final MailSender mailSender;
    
    protected final ApplicationFormDAO applicationDAO;
    
    protected class UpdateDigestNotificationClosure implements Closure {
        private final DigestNotificationType type;

        public UpdateDigestNotificationClosure(final DigestNotificationType type) {
            this.type = type;
        }

        @Override
        public void execute(final Object input) {
            userService.setDigestNotificationType((RegisteredUser) input, type);
        }
    }
    
    public AbstractMailSendingService(final UserService userService,
    		final MailSender mailSender,  final ApplicationFormDAO formDAO) {
        this.userService = userService;
		this.mailSender = mailSender;
		applicationDAO = formDAO;
    }
    
    @SuppressWarnings("unchecked")
    protected Collection<RegisteredUser> getSupervisorsFromLatestApprovalRound(final ApplicationForm form) {
        if (form.getLatestApprovalRound() != null) {
            return CollectionUtils.collect(form.getLatestApprovalRound().getSupervisors(), new Transformer() {
                @Override
                public Object transform(final Object input) {
                    return ((Supervisor) input).getUser();
                }
            });
        }
        return Collections.emptyList();
    }
    
    protected Collection<RegisteredUser> getProgramAdministrators(final ApplicationForm form) {
        return form.getProgram().getAdministrators();
    }
    
    protected String getAdminsEmailsCommaSeparatedAsString(List<RegisteredUser> administrators) {
		Set<String> administratorMails = new LinkedHashSet<String>();
		for (RegisteredUser admin : administrators) {
			administratorMails.add(admin.getEmail());
		}
		return StringUtils.join(administratorMails.toArray(new String[] {}), ";");
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
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected NotificationRecord createNotificationRecordIfNotExists(final ApplicationForm form, final NotificationType type) {
        NotificationRecord notificationRecord = form.getNotificationForType(type);
        if (notificationRecord == null) {
            notificationRecord = new NotificationRecord(type);
            form.addNotificationRecord(notificationRecord);
        }
        notificationRecord.setDate(new Date());
        applicationDAO.save(form);
        return notificationRecord;
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
    
    protected String createRecipientString(List<Person> registryContacts) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Person person : registryContacts) {
			if( !first) {
				sb.append(", ");
			}
			sb.append(person.getFirstname());
			first = false;
		}
		return sb.toString();
	}
    
    protected String getCommentText(List<Person> registryContacts) {
		StringBuilder sb = new StringBuilder();
		sb.append("Referred to UCL Admissions for advice on eligibility and fees status. Referral send to ");
		for (int i = 0; i < registryContacts.size(); i++) {
			Person contact = registryContacts.get(i);
			if (i > 0 && i < registryContacts.size() - 1) {
				sb.append(", ");
			}
			if (registryContacts.size() > 1 && i == (registryContacts.size() - 1)) {
				sb.append(" and ");
			}
			sb.append(contact.getFirstname() + " " + contact.getLastname() + " (" + contact.getEmail() + ")");
		}
		sb.append(".");
		return sb.toString();
	}
}
