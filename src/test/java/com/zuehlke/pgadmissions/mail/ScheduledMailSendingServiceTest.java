package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_REMINDER;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.dao.InterviewParticipantDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DigestNotificationType;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class ScheduledMailSendingServiceTest extends MailSendingServiceTest {

    private RefereeDAO refereeDAOMock;

    private ScheduledMailSendingService service;

    private RoleDAO roleDAOMock;

    private ApplicationContext applicationContextMock;

    private EncryptionUtils encryptionUtilsMock;

    private InterviewParticipantDAO interviewParticipantDAOMock;

    private ApplicationFormListDAO applicationFormListDAOMock;

    private ApplicationFormUserRoleService applicationFormUserRoleService;

    private static final String HOST = "http://localhost:8080";

    @Before
    public void prepare() {
        applicationContextMock = createMock(ApplicationContext.class);
        refereeDAOMock = createMock(RefereeDAO.class);
        userDAOMock = createMock(UserDAO.class);
        roleDAOMock = createMock(RoleDAO.class);
        encryptionUtilsMock = createMock(EncryptionUtils.class);
        interviewParticipantDAOMock = createMock(InterviewParticipantDAO.class);
        applicationFormListDAOMock = createMock(ApplicationFormListDAO.class);
        service = new ScheduledMailSendingService(mockMailSender, applicationFormDAOMock, configurationServiceMock, refereeDAOMock, userDAOMock, roleDAOMock,
                encryptionUtilsMock, HOST, applicationContextMock, interviewParticipantDAOMock, applicationFormUserRoleService);
    }

    @Test
    public void shouldSendDigestToUsers() {
        
        List<Integer> potentialUsersForTaskReminder = new ArrayList<Integer>(2);
        List<Integer> potentialUsersForTaskNotification = new ArrayList<Integer>(2);
        List<Integer> usersForUpdateNotification = new ArrayList<Integer>(2);
   
        for (Integer i = 1; i < 7; i++) {
            RegisteredUser user = new RegisteredUserBuilder().id(i).username("user" + i.toString()).build();
            if (i < 3) {    
                potentialUsersForTaskReminder.add(user.getId());
            } else if (i < 5) {
                potentialUsersForTaskNotification.add(user.getId());
            } else if (i < 7) {
                usersForUpdateNotification.add(user.getId());
            }
        }
       
        ScheduledMailSendingService thisServiceMock = createMock(ScheduledMailSendingService.class);

        expect(applicationContextMock.getBean(ScheduledMailSendingService.class)).andReturn(thisServiceMock);
        
        expect(thisServiceMock.getUsersForTaskReminder(EasyMock.isA(Date.class))).andReturn(potentialUsersForTaskReminder);
        expect(thisServiceMock.getUsersForTaskNotification(EasyMock.isA(Date.class))).andReturn(potentialUsersForTaskNotification);
        expect(thisServiceMock.getUsersForUpdateNotification(EasyMock.isA(Date.class))).andReturn(usersForUpdateNotification);
        
        expect(thisServiceMock.sendDigestEmail(userDAOMock.get(potentialUsersForTaskReminder.get(0)), DigestNotificationType.TASK_REMINDER)).andReturn(true);
        expect(thisServiceMock.sendDigestEmail(userDAOMock.get(potentialUsersForTaskReminder.get(1)), DigestNotificationType.TASK_REMINDER)).andReturn(true);
        expect(thisServiceMock.sendDigestEmail(userDAOMock.get(potentialUsersForTaskNotification.get(0)), DigestNotificationType.TASK_NOTIFICATION)).andReturn(true);
        expect(thisServiceMock.sendDigestEmail(userDAOMock.get(potentialUsersForTaskNotification.get(1)), DigestNotificationType.TASK_NOTIFICATION)).andReturn(true);
        expect(thisServiceMock.sendDigestEmail(userDAOMock.get(usersForUpdateNotification.get(0)), DigestNotificationType.UPDATE_NOTIFICATION)).andReturn(true);
        expect(thisServiceMock.sendDigestEmail(userDAOMock.get(usersForUpdateNotification.get(1)), DigestNotificationType.UPDATE_NOTIFICATION)).andReturn(true);

        replay(userDAOMock, applicationContextMock, thisServiceMock);
        service.sendDigestsToUsers();
        verify(userDAOMock, applicationContextMock, thisServiceMock);
    }

    @Test
    public void shouldSendUpdateEmail() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).username("bebok").build();
        expect(mockMailSender.resolveSubject(EmailTemplateName.DIGEST_UPDATE_NOTIFICATION, (Object) null)).andReturn("Ahoj!");
        Capture<PrismEmailMessage> messageCapture = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(capture(messageCapture));

        replay(userDAOMock, applicationFormListDAOMock, mockMailSender);
        boolean result = service.sendDigestEmail(user, DigestNotificationType.UPDATE_NOTIFICATION);
        verify(userDAOMock, applicationFormListDAOMock, mockMailSender);

        assertTrue(result);
        PrismEmailMessage message = messageCapture.getValue();
        assertEquals(HOST, message.getModel().get("host"));
        assertSame(user, message.getModel().get("user"));
        assertEquals("Ahoj!", message.getSubjectCode());
        assertEquals(EmailTemplateName.DIGEST_UPDATE_NOTIFICATION, message.getTemplateName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSendReferenceReminder() throws Exception {
        service = new ScheduledMailSendingService(mockMailSender, applicationFormDAOMock, configurationServiceMock, refereeDAOMock, userDAOMock, roleDAOMock,
                encryptionUtilsMock, HOST, applicationContextMock, interviewParticipantDAOMock, applicationFormUserRoleService) {
            @Override
            protected RegisteredUser processRefereeAndGetAsUser(final Referee referee) {
                return null;
            }
        };

        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        String adminMails = SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS;
        ApplicationForm form = getSampleApplicationForm();
        Referee referee = new RefereeBuilder().id(0).user(user).application(form).build();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", adminMails);
        model.put("referee", referee);
        model.put("application", form);
        model.put("applicant", form.getApplicant());
        model.put("host", HOST);

        String subjectToReturn = "REMINDER: " + SAMPLE_APPLICANT_NAME + " " + SAMPLE_APPLICANT_SURNAME + " " + "Application " + SAMPLE_APPLICATION_NUMBER
                + " for UCL " + SAMPLE_PROGRAM_TITLE + " - Reference Request";

        expect(refereeDAOMock.getRefereesDueReminder()).andReturn(asList(referee.getId()));

        expect(
                mockMailSender.resolveSubject(REFEREE_REMINDER, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME)).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        expect(applicationContextMock.getBean(isA(Class.class))).andReturn(service);
        refereeDAOMock.save(referee);

        replay(mockMailSender, applicationContextMock, refereeDAOMock);
        service.sendReferenceReminder();
        verify(mockMailSender, applicationContextMock, refereeDAOMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());

        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());

        assertNotNull(referee.getLastNotified());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSendInterviewParticipantReminder() throws Exception {
        RegisteredUser user = new RegisteredUserBuilder().id(1).build();
        String adminMails = SAMPLE_ADMIN1_EMAIL_ADDRESS + ", " + SAMPLE_ADMIN2_EMAIL_ADDRESS;
        ApplicationForm application = getSampleApplicationForm();
        Interview interview = new InterviewBuilder().application(application).build();
        InterviewParticipant participant = new InterviewParticipantBuilder().id(0).user(user).interview(interview).build();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("adminsEmails", adminMails);
        model.put("participant", participant);
        model.put("application", application);
        model.put("host", HOST);

        String subjectToReturn = "REMINDER: " + SAMPLE_APPLICANT_NAME + " " + SAMPLE_APPLICANT_SURNAME + " " + "Application " + SAMPLE_APPLICATION_NUMBER
                + " for UCL " + SAMPLE_PROGRAM_TITLE + " - Reference Request";

        expect(interviewParticipantDAOMock.getInterviewParticipantsDueReminder()).andReturn(Arrays.asList(participant.getId()));


        expect(
                mockMailSender.resolveSubject(EmailTemplateName.INTERVIEW_VOTE_REMINDER, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE,
                        SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME)).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        expect(applicationContextMock.getBean(isA(Class.class))).andReturn(service);
        interviewParticipantDAOMock.save(participant);

        replay(mockMailSender, applicationContextMock, interviewParticipantDAOMock);
        service.sendInterviewParticipantVoteReminder();
        verify(mockMailSender, applicationContextMock, interviewParticipantDAOMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().size());
        assertEquals((Integer) 1, message.getTo().get(0).getId());

        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());

        assertNotNull(participant.getLastNotified());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSendNewUserInvitation() {
        ApplicationForm form = getSampleApplicationForm();
        PendingRoleNotification roleNotification = new PendingRoleNotificationBuilder().role(new RoleBuilder().id(Authority.ADMINISTRATOR).build())
                .addedByUser(form.getProgram().getAdministrators().get(0)).program(form.getProgram()).build();
        RegisteredUser user = new RegisteredUserBuilder().id(1).pendingRoleNotifications(roleNotification).build();

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("newUser", user);
        model.put("admin", form.getProgram().getAdministrators().get(0));
        model.put("host", HOST);

        expect(userDAOMock.getUsersIdsWithPendingRoleNotifications()).andReturn(asList(1));
        expect(userDAOMock.get(1)).andReturn(user);
        String subjectToReturn = "Invitation to Join UCL Prism";
        expect(mockMailSender.resolveSubject(EmailTemplateName.NEW_USER_SUGGESTION, (Object[]) null)).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        userDAOMock.save(user);

        expect(applicationContextMock.getBean(isA(Class.class))).andReturn(service);

        replay(userDAOMock, applicationContextMock, mockMailSender);
        service.sendNewUserInvitation();
        verify(userDAOMock, mockMailSender, applicationContextMock);

        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertNotNull(message.getCc());
        assertEquals(1, message.getTo().size());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());

        assertNotNull(roleNotification.getNotificationDate());

    }

}