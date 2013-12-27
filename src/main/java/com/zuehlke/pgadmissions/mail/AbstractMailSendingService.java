package com.zuehlke.pgadmissions.mail;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public abstract class AbstractMailSendingService {

    protected final ApplicationFormDAO applicationDAO;

    protected final ConfigurationService configurationService;

    protected final UserDAO userDAO;

    protected final RoleDAO roleDAO;

    protected final EncryptionUtils encryptionUtils;

    protected final RefereeDAO refereeDAO;

    protected final String host;

    private MailSender mailSender;

    public AbstractMailSendingService(final MailSender mailSender, final ApplicationFormDAO formDAO, final ConfigurationService configurationService,
            final UserDAO userDAO, final RoleDAO roleDAO, final RefereeDAO refereeDAO, final EncryptionUtils encryptionUtils,
            @Value("${application.host}") final String host) {
        this.mailSender = mailSender;
        this.applicationDAO = formDAO;
        this.configurationService = configurationService;
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.encryptionUtils = encryptionUtils;
        this.refereeDAO = refereeDAO;
        this.host = host;
    }

    protected String getAdminsEmailsCommaSeparatedAsString(final List<RegisteredUser> administrators) {
        Set<String> administratorMails = new LinkedHashSet<String>();
        for (RegisteredUser admin : administrators) {
            administratorMails.add(admin.getEmail());
        }
        return StringUtils.join(administratorMails.toArray(new String[] {}), ", ");
    }

    protected Collection<RegisteredUser> getProgramAdministrators(final ApplicationForm application) {
        return application.getProgram().getAdministrators();
    }

    protected Collection<RegisteredUser> getApplicationOrProgramAdministrators(final ApplicationForm application) {
        if (application.getApplicationAdministrator() != null) {
            return Collections.singleton(application.getApplicationAdministrator());
        }
        return getProgramAdministrators(application);
    }

    protected PrismEmailMessage buildMessage(RegisteredUser recipient, String subject, Map<String, Object> model, EmailTemplateName templateName) {
        return buildMessage(recipient, null, subject, model, templateName);
    }

    protected PrismEmailMessage buildMessage(RegisteredUser recipient, List<RegisteredUser> ccRecipients, String subject, Map<String, Object> model,
            EmailTemplateName templateName) {
        return new PrismEmailMessageBuilder().to(recipient).cc(ccRecipients).subject(subject).model(model).emailTemplate(templateName).build();
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

    protected String resolveMessage(EmailTemplateName templateName, ApplicationForm form, ApplicationFormStatus previousStage) {
        if (previousStage == null) {
            return resolveMessage(templateName, form);
        }
        RegisteredUser applicant = form.getApplicant();
        if (applicant == null) {
            throw new IllegalArgumentException("applicant must be provided!");
        }
        Object[] args = new Object[] { form.getApplicationNumber(), form.getProgram().getTitle(), applicant.getFirstName(), applicant.getLastName(),
                previousStage.displayValue() };

        return resolveMessage(templateName, args);
    }

    protected String resolveMessage(EmailTemplateName templateName, ApplicationForm applicationForm) {
        RegisteredUser applicant = applicationForm.getApplicant();
        if (applicant == null) {
            return mailSender.resolveSubject(templateName, applicationForm.getApplicationNumber(), applicationForm.getProgram().getTitle());
        } else {
            return mailSender.resolveSubject(templateName, applicationForm.getApplicationNumber(), applicationForm.getProgram().getTitle(),
                    applicant.getFirstName(), applicant.getLastName());
        }
    }

    protected String resolveMessage(EmailTemplateName templateName, Object[] args) {
        return mailSender.resolveSubject(templateName, args);
    }

    protected void sendEmail(PrismEmailMessage message) {
        mailSender.sendEmail(message);
    }

    protected void sendEmail(PrismEmailMessage... messages) {
        mailSender.sendEmail(messages);
    }

    protected String getHostName() {
        return host;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public MailSender getMailSender() {
        return mailSender;
    }

}