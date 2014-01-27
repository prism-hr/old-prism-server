package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_TASK_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_TASK_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.INTERVIEW_VOTE_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.NEW_USER_SUGGESTION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_REMINDER;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.dao.InterviewParticipantDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.DigestNotificationType;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
public class ScheduledMailSendingService extends AbstractMailSendingService {

    private final Logger log = LoggerFactory.getLogger(ScheduledMailSendingService.class);

    private final RefereeDAO refereeDAO;

    private final UserDAO userDAO;

    private final ApplicationContext applicationContext;

    private final InterviewParticipantDAO interviewParticipantDAO;
    
    private final ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    public ScheduledMailSendingService() {
        this(null, null, null, null, null, null, null, null, null, null, null);
    }
    
    @Autowired
    public ScheduledMailSendingService(final MailSender mailSender, final ApplicationFormDAO applicationFormDAO,
            final ConfigurationService configurationService, final RefereeDAO refereeDAO, final UserDAO userDAO, final RoleDAO roleDAO,
            final EncryptionUtils encryptionUtils, @Value("${application.host}") final String host, final ApplicationContext applicationContext,
            InterviewParticipantDAO interviewParticipantDAO, ApplicationFormUserRoleDAO applicationFormUserRoleDAO) {
        super(mailSender, applicationFormDAO, configurationService, userDAO, roleDAO, refereeDAO, encryptionUtils, host);
        this.refereeDAO = refereeDAO;
        this.userDAO = userDAO;
        this.applicationContext = applicationContext;
        this.interviewParticipantDAO = interviewParticipantDAO;
        this.applicationFormUserRoleDAO = applicationFormUserRoleDAO;
    }

    public void sendDigestsToUsers() {
        ScheduledMailSendingService thisProxy = applicationContext.getBean(this.getClass());
        Date baselineDate = new Date();
        
        log.trace("Sending task reminder to users");
        for (RegisteredUser user : thisProxy.getUsersForTaskReminder(baselineDate)) {
            thisProxy.sendDigestEmail(user, DigestNotificationType.TASK_REMINDER);
        }
        log.trace("Finished sending task reminder to users");

        log.trace("Sending task notification to users");
        for (RegisteredUser user : thisProxy.getUsersForTaskNotification(baselineDate)) {
            thisProxy.sendDigestEmail(user, DigestNotificationType.TASK_NOTIFICATION);
        }
        log.trace("Finished sending task notification to users");

        log.trace("Sending update notification to users");
        for (RegisteredUser user : thisProxy.getUsersForUpdateNotification(baselineDate)) {
            thisProxy.sendDigestEmail(user, DigestNotificationType.UPDATE_NOTIFICATION);
        }
        log.trace("Finished sending update notification to users");
    }

    @Transactional
    public List<RegisteredUser> getUsersForTaskNotification(Date baselineDate) {
        updateApplicationFormActionUrgentFlag();
        return userDAO.getUsersDueTaskNotification(baselineDate);
    }

    @Transactional
    public List<RegisteredUser> getUsersForTaskReminder(Date baselineDate) {
        updateApplicationFormActionUrgentFlag();
        return userDAO.getUsersDueTaskReminder(baselineDate);
    }

    @Transactional
    public List<RegisteredUser> getUsersForUpdateNotification(Date baselineDate) {
        updateApplicationFormActionUrgentFlag();
        return userDAO.getUsersDueUpdateNotification(baselineDate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendDigestEmail(RegisteredUser user, DigestNotificationType digestNotificationType) {
        return sendDigest(user, digestNotificationType);
    }
    
    private boolean sendDigest(final RegisteredUser user, DigestNotificationType digestNotificationType) {
        try {
            EmailModelBuilder modelBuilder = new EmailModelBuilder() {

                @Override
                public Map<String, Object> build() {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("user", user);
                    model.put("host", getHostName());
                    return model;
                }
            };

            PrismEmailMessageBuilder messageBuilder = new PrismEmailMessageBuilder();
            messageBuilder.model(modelBuilder);
            messageBuilder.to(user);
            EmailTemplateName templateName;
            
            switch (digestNotificationType) {
	            case TASK_REMINDER:
	                templateName = DIGEST_TASK_REMINDER;
	                break;
	            case TASK_NOTIFICATION:
	                templateName = DIGEST_TASK_NOTIFICATION;
	                break;
	            case UPDATE_NOTIFICATION:
	                templateName = DIGEST_UPDATE_NOTIFICATION;
	                break;
	            default:
	                throw new RuntimeException();
            }
            
            messageBuilder.subject(resolveMessage(templateName, (Object[]) null));
            messageBuilder.emailTemplate(templateName);
            PrismEmailMessage message = messageBuilder.build();
            sendEmail(message);
            
            if (digestNotificationType == DigestNotificationType.UPDATE_NOTIFICATION) {
                user.setLatestUpdateNotificationDate(new Date());
            } else {
            	user.setLatestTaskNotificationDate(new Date());
            }
            
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public void sendReferenceReminder() {
        log.trace("Running sendReferenceReminder Task");
        List<Integer> refereesDueAReminder = refereeDAO.getRefereesIdsDueAReminder();
        for (Integer referee : refereesDueAReminder) {
            applicationContext.getBean(this.getClass()).sendReferenceReminder(referee);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendReferenceReminder(Integer refereeId) {
        PrismEmailMessage message;
        try {
            Referee referee = refereeDAO.getRefereeById(refereeId);
            String subject = resolveMessage(REFEREE_REMINDER, referee.getApplication());

            ApplicationForm applicationForm = referee.getApplication();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(applicationForm.getProgram().getAdministrators());
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "referee", "application", "applicant", "host" }, new Object[] {
                    adminsEmails, referee, applicationForm, applicationForm.getApplicant(), getHostName() });

            message = buildMessage(referee.getUser(), subject, modelBuilder.build(), REFEREE_REMINDER);
            sendEmail(message);
            referee.setLastNotified(new Date());
            refereeDAO.save(referee);
        } catch (Exception e) {
            log.error("Error while sending reference reminder email to referee: ", e);
            return false;
        }
        return true;
    }

    @Transactional
    public void sendInterviewParticipantVoteReminder() {
        log.trace("Running interviewParticipantVoteReminder Task");
        List<Integer> participantsDueAReminder = interviewParticipantDAO.getInterviewParticipantsIdsDueAReminder();
        for (Integer participantId : participantsDueAReminder) {
            applicationContext.getBean(this.getClass()).sendInterviewParticipantVoteReminder(participantId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendInterviewParticipantVoteReminder(Integer participantId) {
        PrismEmailMessage message;
        InterviewParticipant participant = interviewParticipantDAO.getParticipantById(participantId);
        ApplicationForm application = participant.getInterview().getApplication();
        try {
            String subject = resolveMessage(INTERVIEW_VOTE_REMINDER, application);

            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(application.getProgram().getAdministrators());
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "participant", "application", "host" }, new Object[] {
                    adminsEmails, participant, application, getHostName() });

            message = buildMessage(participant.getUser(), subject, modelBuilder.build(), INTERVIEW_VOTE_REMINDER);
            sendEmail(message);
            participant.setLastNotified(new Date());
        } catch (Exception e) {
            log.error("Error while sending interview vote reminder email to interview participant: " + participant.getUser().getDisplayName(), e);
            return false;
        }
        return true;
    }

    @Transactional
    public void sendNewUserInvitation() {
        log.trace("Running sendNewUserInvitation Task");
        List<Integer> users = userDAO.getUsersIdsWithPendingRoleNotifications();
        for (Integer user : users) {
            applicationContext.getBean(this.getClass()).sendNewUserInvitation(user);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendNewUserInvitation(Integer userId) {
        PrismEmailMessage message = null;
        RegisteredUser user = userDAO.get(userId);
        String subject = resolveMessage(NEW_USER_SUGGESTION, (Object[]) null);
        for (PendingRoleNotification notification : user.getPendingRoleNotifications()) {
            if (notification.getNotificationDate() == null) {
                notification.setNotificationDate(new Date());
            }
        }
        RegisteredUser admin = user.getPendingRoleNotifications().get(0).getAddedByUser();

        try {
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "newUser", "admin", "host" }, new Object[] { user, admin, getHostName() });
            message = buildMessage(user, subject, modelBuilder.build(), NEW_USER_SUGGESTION);
            sendEmail(message);
            userDAO.save(user);
        } catch (Exception e) {
            log.error("Error while sending reference reminder email to referee: ", e);
            return false;
        }
        return true;
    }
    
    private void updateApplicationFormActionUrgentFlag() {
        applicationFormUserRoleDAO.updateRaisesUrgentFlag();
    }
    
}