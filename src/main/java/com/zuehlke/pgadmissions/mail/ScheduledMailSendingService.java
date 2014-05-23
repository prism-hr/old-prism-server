package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_TASK_REQUEST_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId.APPLICATION_UPDATE_NOTIFICATION;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.DigestNotificationType;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.WorkflowService;

@Service
public class ScheduledMailSendingService extends AbstractMailSendingService {
    // TODO fix tests

    private final Logger log = LoggerFactory.getLogger(ScheduledMailSendingService.class);

    @Autowired
    private RefereeDAO refereeDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Autowired
    private OpportunitiesService opportunitiesService;

    public void sendDigestsToUsers() {
        ScheduledMailSendingService thisProxy = applicationContext.getBean(this.getClass());
        Date baselineDate = new Date();

        log.trace("Sending task reminder to users");
        for (Integer userId : thisProxy.getUsersForTaskReminder(baselineDate)) {
            thisProxy.sendDigestEmail(userId, DigestNotificationType.TASK_REMINDER);
        }
        log.trace("Finished sending task reminder to users");

        log.trace("Sending task notification to users");
        for (Integer userId : thisProxy.getUsersForTaskNotification(baselineDate)) {
            thisProxy.sendDigestEmail(userId, DigestNotificationType.TASK_NOTIFICATION);
        }
        log.trace("Finished sending task notification to users");

        log.trace("Sending update notification to users");
        for (Integer userId : thisProxy.getUsersForUpdateNotification(baselineDate)) {
            thisProxy.sendDigestEmail(userId, DigestNotificationType.UPDATE_NOTIFICATION);
        }
        log.trace("Finished sending update notification to users");

        log.trace("Sending opportunity request notification to users");
        for (Integer userId : thisProxy.getUsersForOpportunityRequestNotification(baselineDate)) {
            thisProxy.sendDigestEmail(userId, DigestNotificationType.OPPORTUNITY_REQUEST_NOTIFICATION);
        }
        log.trace("Finished sending opportunity request notification to users");
    }

    @Transactional
    public List<Integer> getUsersForTaskNotification(Date baselineDate) {
        applicationFormUserRoleService.updateUrgentApplications();
        return userDAO.getUsersDueTaskNotification(baselineDate);
    }

    @Transactional
    public List<Integer> getUsersForTaskReminder(Date baselineDate) {
        applicationFormUserRoleService.updateUrgentApplications();
        return userDAO.getUsersDueTaskReminder(baselineDate);
    }

    @Transactional
    public List<Integer> getUsersForUpdateNotification(Date baselineDate) {
        applicationFormUserRoleService.updateUrgentApplications();
        return userDAO.getUsersDueUpdateNotification(baselineDate);
    }

    @Transactional
    public List<Integer> getUsersForOpportunityRequestNotification(Date baselineDate) {
        // TODO reimplement
        return Lists.newArrayList();
//        if (opportunitiesService.getNewOpportunityRequests().isEmpty()) {
//            return Collections.emptyList();
//        }
//        return userDAO.getUsersDueOpportunityRequestNotification(baselineDate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendDigestEmail(Integer userId, DigestNotificationType digestNotificationType) {
        final User user = userDAO.getById(userId);
        return sendDigest(user, digestNotificationType);
    }

    private boolean sendDigest(final User user, DigestNotificationType digestNotificationType) {
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
            NotificationTemplateId templateName;

            switch (digestNotificationType) {
            case TASK_REMINDER:
                templateName = APPLICATION_TASK_REQUEST_REMINDER;
                break;
            case TASK_NOTIFICATION:
                templateName = APPLICATION_TASK_REQUEST;
                break;
            case UPDATE_NOTIFICATION:
                templateName = APPLICATION_UPDATE_NOTIFICATION;
                break;
            case OPPORTUNITY_REQUEST_NOTIFICATION:
                templateName = NotificationTemplateId.PROGRAM_TASK_REQUEST;
                break;
            default:
                throw new RuntimeException();
            }

            messageBuilder.subject(resolveMessage(templateName, (Object[]) null));
            messageBuilder.emailTemplate(templateName);
            PrismEmailMessage message = messageBuilder.build();
            sendEmail(message);

            // FIXME mark notications using user_batch_notification table
//            if (digestNotificationType == DigestNotificationType.TASK_NOTIFICATION || digestNotificationType == DigestNotificationType.TASK_REMINDER) {
//                user.setLatestTaskNotificationDate(new Date());
//            } else if (digestNotificationType == DigestNotificationType.UPDATE_NOTIFICATION) {
//                user.setLatestUpdateNotificationDate(new Date());
//            } else {
//                user.setLatestOpportunityRequestNotificationDate(new Date());
//            }

            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public void sendReferenceReminder() {
        log.trace("Sending reference reminder to users");
        List<Integer> refereeIds = refereeDAO.getRefereesDueReminder();
        for (Integer refereeId : refereeIds) {
            applicationContext.getBean(this.getClass()).sendReferenceReminder(refereeId);
        }
        log.trace("Finished sending reference reminder to users");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendReferenceReminder(Integer refereeId) {
        final Referee referee = refereeDAO.getRefereeById(refereeId);
        PrismEmailMessage message;
        try {
            String subject = resolveMessage(APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER, referee.getApplication());
            Application application = referee.getApplication();
            String adminsEmails = getAdminsEmailsCommaSeparatedAsString(roleService.getProgramAdministrators(application.getProgram()));
            EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "referee", "application", "applicant", "host" }, new Object[] {
                    adminsEmails, referee, application, application.getUser(), getHostName() });
            message = buildMessage(referee.getUser(), subject, modelBuilder.build(), APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER);
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
        log.trace("Sending interview scheduling reminder to users");
        // TODO get participants due to reminder using query to ApplicationFormUserRole
        // List<Integer> participantIds = interviewParticipantDAO.getInterviewParticipantsDueReminder();
        // for (Integer participantId : participantIds) {
        // applicationContext.getBean(this.getClass()).sendInterviewParticipantVoteReminder(participantId);
        // }
        log.trace("Sending interview scheduling reminder to users");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendInterviewParticipantVoteReminder(Integer userId) {
        // final RegisteredUser user = userService.getUser(userId);
        // try {
        // PrismEmailMessage message;
        // ApplicationForm application = participant.getInterview().getApplication();
        // String subject = resolveMessage(INTERVIEW_VOTE_REMINDER, application);
        // String adminsEmails = getAdminsEmailsCommaSeparatedAsString(application.getProgram().getAdministrators());
        // EmailModelBuilder modelBuilder = getModelBuilder(new String[] { "adminsEmails", "participant", "application", "host" }, new Object[] {
        // adminsEmails, participant, application, getHostName() });
        //
        // message = buildMessage(participant.getUser(), subject, modelBuilder.build(), INTERVIEW_VOTE_REMINDER);
        // sendEmail(message);
        // participant.setLastNotified(new Date());
        // interviewParticipantDAO.save(participant);
        // } catch (Exception e) {
        // log.error("Error while sending interview vote reminder email to participant:", e);
        // return false;
        // }
        return true;
    }


}
