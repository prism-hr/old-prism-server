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
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
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

    protected RegisteredUser processRefereeAndGetAsUser(final Referee referee) {
        RegisteredUser user = userDAO.getUserByEmailIncludingDisabledAccounts(referee.getEmail());
        Role refereeRole = roleService.getById(Authority.REFEREE);

        if (userExists(user) && !isUserReferee(user)) {
            user.getRoles().add(refereeRole);
        }

        if (!userExists(user)) {
            user = createAndSaveNewUserWithRefereeRole(referee, refereeRole);
        }

        referee.setUser(user);

        refereeDAO.save(referee);

        return user;
    }

    private RegisteredUser createAndSaveNewUserWithRefereeRole(final Referee referee, final Role refereeRole) {
        RegisteredUser user = new RegisteredUser();
        user.setEmail(referee.getEmail());
        user.setFirstName(referee.getFirstname());
        user.setLastName(referee.getLastname());
        user.setUsername(referee.getEmail());
        user.getRoles().add(refereeRole);
        user.setActivationCode(encryptionUtils.generateUUID());
        user.setEnabled(false);
        user.setDirectToUrl(DirectURLsEnum.ADD_REFERENCE.displayValue() + referee.getApplication().getApplicationNumber());
        userDAO.save(user);
        return user;
    }

    private boolean userExists(final RegisteredUser user) {
        return user != null;
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
        Object[] args = new Object[] { form.getApplicationNumber(), form.getAdvert().getTitle(), applicant.getFirstName(), applicant.getLastName(),
                previousStage.displayValue() };

        return resolveMessage(templateName, args);
    }

    protected String resolveMessage(EmailTemplateName templateName, ApplicationForm applicationForm) {
        RegisteredUser applicant = applicationForm.getApplicant();
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
