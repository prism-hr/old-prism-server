package com.zuehlke.pgadmissions.mail;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public abstract class AbstractMailSendingService {

    @Autowired
    protected ApplicationFormDAO applicationDAO;
    
    @Autowired
    protected ConfigurationService configurationService;

    @Autowired
    protected UserDAO userDAO;

    @Autowired
    protected RoleService roleService;

    @Autowired
    protected EncryptionUtils encryptionUtils;

    @Autowired
    protected RefereeDAO refereeDAO;

    @Autowired
    @Value("${application.host}")
    protected String host;

    @Autowired
    private MailSender mailSender;
    
    protected String getAdminsEmailsCommaSeparatedAsString(final List<User> administrators) {
        Set<String> administratorMails = new LinkedHashSet<String>();
        for (User admin : administrators) {
            administratorMails.add(admin.getEmail());
        }
        return StringUtils.join(administratorMails.toArray(new String[] {}), ", ");
    }

    protected PrismEmailMessage buildMessage(User recipient, String subject, Map<String, Object> model, EmailTemplateName templateName) {
        return buildMessage(recipient, null, subject, model, templateName);
    }

    protected PrismEmailMessage buildMessage(User recipient, List<User> ccRecipients, String subject, Map<String, Object> model,
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

    protected String resolveMessage(EmailTemplateName templateName, ApplicationForm applicationForm) {
        User applicant = applicationForm.getApplicant();
        if (applicant == null) {
            return mailSender.resolveSubject(templateName, applicationForm.getApplicationNumber(), applicationForm.getAdvert().getTitle());
        } else {
            return mailSender.resolveSubject(templateName, applicationForm.getApplicationNumber(), applicationForm.getAdvert().getTitle(),
                    applicant.getFirstName(), applicant.getLastName());
        }
    }

    protected String resolveMessage(EmailTemplateName templateName, Object... args) {
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
