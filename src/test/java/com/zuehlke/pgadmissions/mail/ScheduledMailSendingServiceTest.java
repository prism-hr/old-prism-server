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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.Capture;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
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
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class ScheduledMailSendingServiceTest extends MailSendingServiceTest {

    private RefereeDAO refereeDAOMock;

    private ScheduledMailSendingService service;

    private RoleDAO roleDAOMock;

    private ApplicationContext applicationContextMock;

    private EncryptionUtils encryptionUtilsMock;

    private InterviewParticipantDAO interviewParticipantDAOMock;

    private ApplicationFormListDAO applicationFormListDAOMock;

    private ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

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
                encryptionUtilsMock, HOST, applicationContextMock, interviewParticipantDAOMock, applicationFormListDAOMock, applicationFormUserRoleDAO);
    }

    @Test
    public void shouldSendDigestToUsers() {
        List<Integer> potentialUsersForTaskReminder = Lists.newArrayList(1, 2);
        List<Integer> potentialUsersForTaskNotification = Lists.newArrayList(3, 4);
        List<Integer> usersForUpdateNotification = Lists.newArrayList(5, 6);

        ScheduledMailSendingService thisServiceMock = createMock(ScheduledMailSendingService.class);

        expect(thisServiceMock.getPotentialUsersForTaskReminder()).andReturn(potentialUsersForTaskReminder);
        expect(thisServiceMock.getPotentialUsersForTaskNotification()).andReturn(potentialUsersForTaskNotification);
        expect(thisServiceMock.getUsersForUpdateNotification()).andReturn(usersForUpdateNotification);

        expect(applicationContextMock.getBean(ScheduledMailSendingService.class)).andReturn(thisServiceMock);

        expect(thisServiceMock.sendTaskEmailIfNecessary(1, DigestNotificationType.TASK_REMINDER)).andReturn(true);
        expect(thisServiceMock.sendTaskEmailIfNecessary(2, DigestNotificationType.TASK_REMINDER)).andReturn(true);
        expect(thisServiceMock.sendTaskEmailIfNecessary(3, DigestNotificationType.TASK_NOTIFICATION)).andReturn(true);
        expect(thisServiceMock.sendTaskEmailIfNecessary(4, DigestNotificationType.TASK_NOTIFICATION)).andReturn(true);
        expect(thisServiceMock.sendUpdateEmail(5)).andReturn(true);
        expect(thisServiceMock.sendUpdateEmail(6)).andReturn(true);

        replay(userDAOMock, applicationContextMock, thisServiceMock);
        service.sendDigestsToUsers();
        verify(userDAOMock, applicationContextMock, thisServiceMock);
    }

    @Test
    @Ignore
    // FIXME amend or remove
    public void shouldSendTaskEmailIfNecessary() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).username("bebok").build();
        List<ApplicationForm> applicationRequiringAttention = Lists.newArrayList(new ApplicationForm());

        expect(userDAOMock.get(8)).andReturn(user);
        // expect(applicationFormListDAOMock.getApplicationsWorthConsideringForAttentionFlag(eq(user), isA(ApplicationsFiltering.class),
        // eq(-1))).andReturn(applicationRequiringAttention);
        expect(mockMailSender.resolveSubject(EmailTemplateName.DIGEST_TASK_NOTIFICATION, (Object) null)).andReturn("Ahoj!");

        Capture<PrismEmailMessage> messageCapture = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(capture(messageCapture));

        replay(userDAOMock, applicationFormListDAOMock, mockMailSender);
        boolean result = service.sendTaskEmailIfNecessary(8, DigestNotificationType.TASK_NOTIFICATION);
        verify(userDAOMock, applicationFormListDAOMock, mockMailSender);

        assertTrue(result);
        assertNotNull(user.getLatestTaskNotificationDate());
        PrismEmailMessage message = messageCapture.getValue();
        assertEquals(HOST, message.getModel().get("host"));
        assertSame(user, message.getModel().get("user"));
        assertEquals("Ahoj!", message.getSubjectCode());
        assertEquals(EmailTemplateName.DIGEST_TASK_NOTIFICATION, message.getTemplateName());
    }

    @Test
    @Ignore
    // FIXME amend or remove
    public void shouldNotSendTaskEmailIfNotNecessary() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).username("bebok").build();
        List<ApplicationForm> applicationRequiringAttention = Collections.emptyList();

        expect(userDAOMock.get(8)).andReturn(user);
        // expect(applicationFormListDAOMock.getApplicationsWorthConsideringForAttentionFlag(eq(user), isA(ApplicationsFiltering.class),
        // eq(-1))).andReturn(applicationRequiringAttention);

        replay(userDAOMock, applicationFormListDAOMock);
        boolean result = service.sendTaskEmailIfNecessary(8, DigestNotificationType.TASK_NOTIFICATION);
        verify(userDAOMock, applicationFormListDAOMock);

        assertFalse(result);
    }

    @Test
    public void shouldSendUpdateEmail() {
        RegisteredUser user = new RegisteredUserBuilder().id(8).username("bebok").build();

        expect(userDAOMock.get(8)).andReturn(user);
        expect(mockMailSender.resolveSubject(EmailTemplateName.DIGEST_UPDATE_NOTIFICATION, (Object) null)).andReturn("Ahoj!");

        Capture<PrismEmailMessage> messageCapture = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(capture(messageCapture));

        replay(userDAOMock, applicationFormListDAOMock, mockMailSender);
        boolean result = service.sendUpdateEmail(8);
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
                encryptionUtilsMock, HOST, applicationContextMock, interviewParticipantDAOMock, applicationFormListDAOMock, applicationFormUserRoleDAO) {
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

        expect(refereeDAOMock.getRefereesIdsDueAReminder()).andReturn(asList(0));

        expect(refereeDAOMock.getRefereeById(0)).andReturn(referee);

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

        expect(interviewParticipantDAOMock.getInterviewParticipantsIdsDueAReminder()).andReturn(Arrays.asList(0));

        expect(interviewParticipantDAOMock.getParticipantById(0)).andReturn(participant);

        expect(
                mockMailSender.resolveSubject(EmailTemplateName.INTERVIEW_VOTE_REMINDER, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE,
                        SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME)).andReturn(subjectToReturn);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

        expect(applicationContextMock.getBean(isA(Class.class))).andReturn(service);

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
