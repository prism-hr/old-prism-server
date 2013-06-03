package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_TASK_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_TASK_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_REMINDER;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.InterviewParticipantDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewParticipantBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.PendingRoleNotificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DigestNotificationType;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class ScheduledMailSendingServiceTest extends MailSendingServiceTest {

    private CommentDAO commentDAOMock;

    private SupervisorDAO supervisorDAOMock;

    private StageDurationDAO stageDurationDAOMock;

    private RefereeDAO refereeDAOMock;

    private ScheduledMailSendingService service;

    private UserService userServiceMock;

    private RoleDAO roleDAOMock;

    private ApplicationContext applicationContextMock;

    private EncryptionUtils encryptionUtilsMock;

    private InterviewParticipantDAO interviewParticipantDAOMock;

    private static final String HOST = "http://localhost:8080";


    @Before
    public void prepare() {
        commentDAOMock = createMock(CommentDAO.class);
        applicationContextMock = createMock(ApplicationContext.class);
        supervisorDAOMock = createMock(SupervisorDAO.class);
        stageDurationDAOMock = createMock(StageDurationDAO.class);
        refereeDAOMock = createMock(RefereeDAO.class);
        userServiceMock = createMock(UserService.class);
        userDAOMock = createMock(UserDAO.class);
        roleDAOMock = createMock(RoleDAO.class);
        encryptionUtilsMock = createMock(EncryptionUtils.class);
        interviewParticipantDAOMock = createMock(InterviewParticipantDAO.class);
        service = new ScheduledMailSendingService(mockMailSender, applicationFormDAOMock, commentDAOMock, supervisorDAOMock,
                stageDurationDAOMock, configurationServiceMock,
                refereeDAOMock, userServiceMock, userDAOMock, roleDAOMock, encryptionUtilsMock,
                HOST, applicationContextMock, interviewParticipantDAOMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSendDigestToUsers() {
        RegisteredUser user1 = new RegisteredUserBuilder().id(1).digestNotificationType(DigestNotificationType.TASK_NOTIFICATION).build();
        RegisteredUser user2 = new RegisteredUserBuilder().id(2).digestNotificationType(DigestNotificationType.TASK_REMINDER).build();
        RegisteredUser user3 = new RegisteredUserBuilder().id(3).digestNotificationType(DigestNotificationType.UPDATE_NOTIFICATION).build();
        RegisteredUser user4 = new RegisteredUserBuilder().id(4).digestNotificationType(DigestNotificationType.NONE).build();
        Map<String, Object> model1 = new HashMap<String, Object>();
        model1.put("user", user1);
        model1.put("host", HOST);
        Map<String, Object> model2 = new HashMap<String, Object>();
        model2.putAll(model1);
        model2.put("user", user2);
        Map<String, Object> model3 = new HashMap<String, Object>();
        model3.putAll(model1);
        model3.put("user", user3);
        Map<String, Object> model4 = new HashMap<String, Object>();
        model4.putAll(model1);
        model4.put("user", user4);

        String subjectToReturn1 = "Prism Digest Task Notification";
        String subjectToReturn2 = "Prism Digest Task Reminder";
        String subjectToReturn3 = "Prism Digest Update Notification";

        expect(mockMailSender.resolveSubject(DIGEST_TASK_NOTIFICATION, (Object[]) null)).andReturn(subjectToReturn1);
        expect(mockMailSender.resolveSubject(DIGEST_TASK_REMINDER, (Object[]) null)).andReturn(subjectToReturn2);
        expect(mockMailSender.resolveSubject(DIGEST_UPDATE_NOTIFICATION, (Object[]) null)).andReturn(subjectToReturn3);

        expect(userServiceMock.getAllUsersInNeedOfADigestNotification()).andReturn(asList(1, 2, 3, 4));
        expect(userServiceMock.getUser(1)).andReturn(user1);
        expect(userServiceMock.getUser(2)).andReturn(user2);
        expect(userServiceMock.getUser(3)).andReturn(user3);
        expect(userServiceMock.getUser(4)).andReturn(user4);

        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
        expectLastCall().times(3);

        expect(applicationContextMock.getBean(isA(Class.class))).andReturn(service).anyTimes();

        replay(mockMailSender, applicationContextMock, userServiceMock);
        service.sendDigestsToUsers();
        verify(mockMailSender, applicationContextMock, userServiceMock);

        List<PrismEmailMessage> messages = messageCaptor.getValues();
        assertEquals(3, messages.size());

        assertNotNull(messages.get(0).getTo());
        assertEquals(1, messages.get(0).getTo().size());
        assertEquals((Integer) 1, messages.get(0).getTo().get(0).getId());
        assertEquals(subjectToReturn1, messages.get(0).getSubjectCode());
        assertModelEquals(model1, messages.get(0).getModel());

        assertNotNull(messages.get(1).getTo());
        assertEquals(1, messages.get(1).getTo().size());
        assertEquals((Integer) 2, messages.get(1).getTo().get(0).getId());
        assertEquals(subjectToReturn2, messages.get(1).getSubjectCode());
        assertModelEquals(model2, messages.get(1).getModel());

        assertNotNull(messages.get(2).getTo());
        assertEquals(1, messages.get(2).getTo().size());
        assertEquals((Integer) 3, messages.get(2).getTo().get(0).getId());
        assertEquals(subjectToReturn3, messages.get(2).getSubjectCode());
        assertModelEquals(model3, messages.get(2).getModel());
    }

    @Test
    public void shouldScheduleApprovalRequest() {

        DateTime date = new DateTime(2100, 1, 2, 3, 4);
        RegisteredUser approver1 = new RegisteredUserBuilder().build();
        RegisteredUser approver2 = new RegisteredUserBuilder().build();
        ApplicationForm form = getSampleApplicationForm();
        Supervisor supervisor = new SupervisorBuilder().id(234).confirmedSupervision(true).isPrimary(true).build();
        ApprovalRound round1 = new ApprovalRoundBuilder().application(form).supervisors(supervisor).createdDate(date.toDate()).build();
        form.getProgram().setApprovers(asList(approver1, approver2));
        form.setLatestApprovalRound(round1);

        expect(applicationFormDAOMock.getApplicationsDueMovedToApprovalNotifications()).andReturn(asList(form));
        
        applicationFormDAOMock.save(form);
        userDAOMock.save(approver1);
        userDAOMock.save(approver2);
        replay(stageDurationDAOMock, applicationFormDAOMock, userDAOMock);
        service.scheduleApprovalRequest();
        verify(stageDurationDAOMock, applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.TASK_NOTIFICATION, approver1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, approver2.getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION));
    }
    
    @Test
    public void shouldScheduleApprovalRequestAndNoReminder2() {
        
        DateTime date = new DateTime(2100, 1, 2, 3, 4);
        StageDuration stageDuration = new StageDurationBuilder().unit(DurationUnitEnum.HOURS).duration(1000).build();
        RegisteredUser approver1 = new RegisteredUserBuilder().build();
        RegisteredUser approver2 = new RegisteredUserBuilder().build();
        ApplicationForm form = getSampleApplicationForm();
        Supervisor supervisor = new SupervisorBuilder().id(234).confirmedSupervision(true).isPrimary(true).build();
        ApprovalRound round1 = new ApprovalRoundBuilder().application(form).supervisors(supervisor).createdDate(date.toDate()).build();
        form.getProgram().setApprovers(asList(approver1, approver2));
        form.setLatestApprovalRound(round1);
        
        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(stageDuration);
        
        expect(applicationFormDAOMock.getApplicationsDueMovedToApprovalNotifications()).andReturn(asList(form));
        expect(applicationFormDAOMock.getApplicationsDueApprovalReminder()).andReturn(asList(form));
        
        applicationFormDAOMock.save(form);
        userDAOMock.save(approver1);
        userDAOMock.save(approver2);
        replay(stageDurationDAOMock, applicationFormDAOMock, userDAOMock);
        service.scheduleApprovalReminder();
        verify(stageDurationDAOMock, applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, approver1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, approver2.getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION));
    }
    
    @Test
    public void shouldScheduleApprovalRequestAndReminder() {
        
        DateTime date = new DateTime(2100, 1, 2, 3, 4);
        StageDuration stageDuration = new StageDurationBuilder().unit(DurationUnitEnum.HOURS).duration(1000).build();
        RegisteredUser approver1 = new RegisteredUserBuilder().build();
        RegisteredUser approver2 = new RegisteredUserBuilder().build();
        RegisteredUser approver3 = new RegisteredUserBuilder().build();
        ApplicationForm form1 = getSampleApplicationForm();
        ApplicationForm form2 = getSampleApplicationForm();
        Supervisor supervisor1 = new SupervisorBuilder().id(234).confirmedSupervision(true).isPrimary(true).build();
        Supervisor supervisor2 = new SupervisorBuilder().id(235).confirmedSupervision(true).isPrimary(true).build();
        ApprovalRound round1 = new ApprovalRoundBuilder().application(form1).supervisors(supervisor1).createdDate(date.toDate()).build();
        ApprovalRound round2 = new ApprovalRoundBuilder().application(form2).supervisors(supervisor2).createdDate(date.toDate()).build();
        form1.getProgram().setApprovers(asList(approver1, approver2));
        form1.setLatestApprovalRound(round1);
        form2.setId(543);
        form2.getProgram().setApprovers(asList(approver3));
        form2.setLatestApprovalRound(round2);
        
        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(stageDuration);
        
        expect(applicationFormDAOMock.getApplicationsDueMovedToApprovalNotifications()).andReturn(asList(form1));
        expect(applicationFormDAOMock.getApplicationsDueApprovalReminder()).andReturn(asList(form1, form2));
        
        applicationFormDAOMock.save(form1);
        applicationFormDAOMock.save(form2);
        userDAOMock.save(approver1);
        userDAOMock.save(approver2);
        userDAOMock.save(approver3);
        replay(stageDurationDAOMock, applicationFormDAOMock, userDAOMock);
        service.scheduleApprovalReminder();
        verify(stageDurationDAOMock, applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, approver1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, approver2.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, approver3.getDigestNotificationType());
        assertNotNull(form1.getNotificationForType(NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION));
        assertNotNull(form2.getNotificationForType(NotificationType.APPROVAL_REMINDER));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldScheduleApprovalReminderAndNoRequest() {
        
        DateTime date = new DateTime(2100, 1, 2, 3, 4);
        StageDuration stageDuration = new StageDurationBuilder().unit(DurationUnitEnum.HOURS).duration(1000).build();
        RegisteredUser approver1 = new RegisteredUserBuilder().build();
        RegisteredUser approver2 = new RegisteredUserBuilder().build();
        RegisteredUser approver3 = new RegisteredUserBuilder().build();
        ApplicationForm form1 = getSampleApplicationForm();
        ApplicationForm form2 = getSampleApplicationForm();
        Supervisor supervisor1 = new SupervisorBuilder().id(234).confirmedSupervision(true).isPrimary(true).build();
        Supervisor supervisor2 = new SupervisorBuilder().id(235).confirmedSupervision(true).isPrimary(true).build();
        ApprovalRound round1 = new ApprovalRoundBuilder().application(form1).supervisors(supervisor1).createdDate(date.toDate()).build();
        ApprovalRound round2 = new ApprovalRoundBuilder().application(form2).supervisors(supervisor2).createdDate(date.toDate()).build();
        form1.getProgram().setApprovers(asList(approver1, approver2));
        form1.setLatestApprovalRound(round1);
        form2.setId(543);
        form2.getProgram().setApprovers(asList(approver3));
        form2.setLatestApprovalRound(round2);
        
        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(stageDuration);
        
        expect(applicationFormDAOMock.getApplicationsDueMovedToApprovalNotifications()).andReturn(Collections.EMPTY_LIST);
        expect(applicationFormDAOMock.getApplicationsDueApprovalReminder()).andReturn(asList(form1, form2));
        
        applicationFormDAOMock.save(form1);
        applicationFormDAOMock.save(form2);
        userDAOMock.save(approver1);
        userDAOMock.save(approver2);
        userDAOMock.save(approver3);
        replay(stageDurationDAOMock, applicationFormDAOMock, userDAOMock);
        service.scheduleApprovalReminder();
        verify(stageDurationDAOMock, applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_REMINDER, approver1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, approver2.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, approver3.getDigestNotificationType());
        assertNotNull(form1.getNotificationForType(NotificationType.APPROVAL_REMINDER));
        assertNotNull(form2.getNotificationForType(NotificationType.APPROVAL_REMINDER));
    }

    @Test
    public void shouldScheduleRegistryRevalidationRequestAndNoReminder() {
        RegisteredUser registryContactUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser registryContactUser2 = new RegisteredUserBuilder().id(565).build();
        Person registryContact1 = new PersonBuilder().email("amanda@mail.com").build();
        Person registryContact2 = new PersonBuilder().email("mirkos@mail.com").build();
        
        ApplicationForm form = getSampleApplicationForm();
        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(asList(registryContact1, registryContact2));
        applicationFormDAOMock.save(form);
        expect(applicationFormDAOMock.getApplicationsDueRevalidationRequest()).andReturn(asList(form));
        expect(applicationFormDAOMock.getApplicationsDueRevalidationReminder())
        .andReturn(asList(form));
        
        expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("amanda@mail.com")).andReturn(registryContactUser1);
        expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("mirkos@mail.com")).andReturn(registryContactUser2);
        
        userDAOMock.save(registryContactUser1);
        userDAOMock.save(registryContactUser2);
        
        replay(configurationServiceMock, applicationFormDAOMock, userDAOMock);
        service.scheduleRegistryRevalidationReminder();
        verify(applicationFormDAOMock, configurationServiceMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, registryContactUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, registryContactUser2.getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.REPEAT_VALIDATION_REQUEST));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldScheduleRegistryRevalidationReminderAndnoRequest() {
        RegisteredUser registryContactUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser registryContactUser2 = new RegisteredUserBuilder().id(565).build();
        Person registryContact1 = new PersonBuilder().email("amanda@mail.com").build();
        Person registryContact2 = new PersonBuilder().email("mirkos@mail.com").build();
        
        ApplicationForm form = getSampleApplicationForm();
        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(asList(registryContact1, registryContact2));
        applicationFormDAOMock.save(form);
        expect(applicationFormDAOMock.getApplicationsDueRevalidationRequest()).andReturn(Collections.EMPTY_LIST);
        expect(applicationFormDAOMock.getApplicationsDueRevalidationReminder())
        .andReturn(asList(form));
        
        expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("amanda@mail.com")).andReturn(registryContactUser1);
        expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("mirkos@mail.com")).andReturn(registryContactUser2);
        
        userDAOMock.save(registryContactUser1);
        userDAOMock.save(registryContactUser2);
        
        replay(configurationServiceMock, applicationFormDAOMock, userDAOMock);
        service.scheduleRegistryRevalidationReminder();
        verify(applicationFormDAOMock, configurationServiceMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_REMINDER, registryContactUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, registryContactUser2.getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.REPEAT_VALIDATION_REMINDER));
    }
    
    @Test
    public void shouldScheduleRegistryRevalidationReminderAndnoRequest2() {
        RegisteredUser registryContactUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser registryContactUser2 = new RegisteredUserBuilder().id(565).build();
        Person registryContact1 = new PersonBuilder().email("amanda@mail.com").build();
        Person registryContact2 = new PersonBuilder().email("mirkos@mail.com").build();
        
        ApplicationForm form1 = getSampleApplicationForm();
        ApplicationForm form2 = getSampleApplicationForm();
        form2.setId(986786);
        expect(configurationServiceMock.getAllRegistryUsers()).andReturn(asList(registryContact1, registryContact2));
        applicationFormDAOMock.save(form1);
        applicationFormDAOMock.save(form2);
        expect(applicationFormDAOMock.getApplicationsDueRevalidationRequest()).andReturn(asList(form1));
        expect(applicationFormDAOMock.getApplicationsDueRevalidationReminder()).andReturn(asList(form1, form2));
        
        expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("amanda@mail.com")).andReturn(registryContactUser1).times(2);
        expect(userDAOMock.getUserByEmailIncludingDisabledAccounts("mirkos@mail.com")).andReturn(registryContactUser2).times(2);
        
        userDAOMock.save(registryContactUser1);
        expectLastCall().times(2);
        userDAOMock.save(registryContactUser2);
        expectLastCall().times(2);
        
        replay(configurationServiceMock, applicationFormDAOMock, userDAOMock);
        service.scheduleRegistryRevalidationReminder();
        verify(applicationFormDAOMock, configurationServiceMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_REMINDER, registryContactUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, registryContactUser2.getDigestNotificationType());
        assertNotNull(form1.getNotificationForType(NotificationType.REPEAT_VALIDATION_REQUEST));
        assertNotNull(form2.getNotificationForType(NotificationType.REPEAT_VALIDATION_REMINDER));
    }
    
    @Test
    public void shouldScheduleInterviewFeedbackRequestAndNoReminder() {
        RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(565).build();
        Interviewer interviewer1 = new InterviewerBuilder().user(interviewerUser1).build();
        Interviewer interviewer2 = new InterviewerBuilder().user(interviewerUser2).build();

        ApplicationForm form = getSampleApplicationForm();
        form.setLatestInterview(new InterviewBuilder().interviewers(interviewer1, interviewer2).build());
        NotificationRecord record = new NotificationRecordBuilder().id(1).notificationType(NotificationType.INTERVIEW_FEEDBACK_REQUEST).build();
        record.setApplication(form);
        applicationFormDAOMock.save(form);
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueInterviewFeedbackNotification()).andReturn(asList(form));
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_FEEDBACK_REMINDER, ApplicationFormStatus.INTERVIEW))
                .andReturn(asList(form));

        userDAOMock.save(interviewerUser1);
        userDAOMock.save(interviewerUser2);

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewFeedbackReminder();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.TASK_NOTIFICATION, interviewerUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, interviewerUser2.getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.INTERVIEW_FEEDBACK_REQUEST));
    }

    @Test
    public void shouldScheduleInterviewFeedbackRequestAndReminderForDifferentApplications() {
        RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(565).build();
        Interviewer interviewer1 = new InterviewerBuilder().user(interviewerUser1).build();
        Interviewer interviewer2 = new InterviewerBuilder().user(interviewerUser2).build();

        ApplicationForm form1 = getSampleApplicationForm();
        form1.setLatestInterview(new InterviewBuilder().interviewers(interviewer1).build());

        ApplicationForm form2 = getSampleApplicationForm();
        form2.setId(8459);
        form2.setLatestInterview(new InterviewBuilder().interviewers(interviewer2).build());

        NotificationRecord record = new NotificationRecordBuilder().id(1).notificationType(NotificationType.INTERVIEW_FEEDBACK_REQUEST).build();
        record.setApplication(form1);
        applicationFormDAOMock.save(form1);
        applicationFormDAOMock.save(form2);
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueInterviewFeedbackNotification()).andReturn(asList(form1));
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_FEEDBACK_REMINDER, ApplicationFormStatus.INTERVIEW))
                .andReturn(asList(form1, form2));

        userDAOMock.save(interviewerUser1);
        userDAOMock.save(interviewerUser2);

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewFeedbackReminder();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.TASK_NOTIFICATION, interviewerUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, interviewerUser2.getDigestNotificationType());
        assertNotNull(form1.getNotificationForType(NotificationType.INTERVIEW_FEEDBACK_REQUEST));
        assertNotNull(form2.getNotificationForType(NotificationType.INTERVIEW_FEEDBACK_REMINDER));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotScheduleInterviewFeedbackReminderIfInterviewerProvidedFeedback() {
        RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(565).build();
        Interviewer interviewer1 = new InterviewerBuilder().user(interviewerUser1).build();
        Interviewer interviewer2 = new InterviewerBuilder().interviewComment(new InterviewCommentBuilder().build()).user(interviewerUser2).build();
        
        ApplicationForm form1 = getSampleApplicationForm();
        form1.setLatestInterview(new InterviewBuilder().interviewers(interviewer1).build());
        
        ApplicationForm form2 = getSampleApplicationForm();
        form2.setId(8459);
        form2.setLatestInterview(new InterviewBuilder().interviewers(interviewer2).build());
        
        NotificationRecord record = new NotificationRecordBuilder().id(1).notificationType(NotificationType.INTERVIEW_FEEDBACK_REQUEST).build();
        record.setApplication(form1);
        applicationFormDAOMock.save(form1);
        applicationFormDAOMock.save(form2);
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueInterviewFeedbackNotification()).andReturn(Collections.EMPTY_LIST);
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_FEEDBACK_REMINDER, ApplicationFormStatus.INTERVIEW))
        .andReturn(asList(form1, form2));
        
        userDAOMock.save(interviewerUser1);
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewFeedbackReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_REMINDER, interviewerUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.NONE, interviewerUser2.getDigestNotificationType());
        assertNotNull(form1.getNotificationForType(NotificationType.INTERVIEW_FEEDBACK_REMINDER));
        assertNotNull(form2.getNotificationForType(NotificationType.INTERVIEW_FEEDBACK_REMINDER));
    }

    @Test
    public void shouldNotScheduleInterviewFeedbackEvaluationReminderIfNotEveryInterviewerHaveProvidedFeedback() {
        RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(565).build();
        Interviewer interviewer1 = new InterviewerBuilder().user(interviewerUser1).build();
        Interviewer interviewer2 = new InterviewerBuilder().user(interviewerUser2).interviewComment(new InterviewComment()).build();
        
        ApplicationForm form = getSampleApplicationForm();
        form.setLatestInterview(new InterviewBuilder().interviewers(interviewer1, interviewer2).build());
        expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_EVALUATION_REMINDER, ApplicationFormStatus.INTERVIEW))
        .andReturn(asList(form));
        
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        
        replay(applicationFormDAOMock);
        service.scheduleInterviewFeedbackEvaluationReminder();
        verify(applicationFormDAOMock);
        
        assertEquals(DigestNotificationType.NONE, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.NONE, admins.get(1).getDigestNotificationType());
        assertNull(form.getNotificationForType(NotificationType.INTERVIEW_EVALUATION_REMINDER));
    }
    
    @Test
    public void shouldScheduleInterviewFeedbackEvaluationReminderIfAllInterviewersHaveProvidedFeedback() {
        RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(565).build();
        Interviewer interviewer1 = new InterviewerBuilder().user(interviewerUser1).interviewComment(new InterviewComment()).build();
        Interviewer interviewer2 = new InterviewerBuilder().user(interviewerUser2).interviewComment(new InterviewComment()).build();

        ApplicationForm form = getSampleApplicationForm();
        form.setLatestInterview(new InterviewBuilder().interviewers(interviewer1, interviewer2).build());
        NotificationRecord record = new NotificationRecordBuilder().id(1).notificationType(NotificationType.INTERVIEW_EVALUATION_REMINDER).build();
        record.setApplication(form);
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_EVALUATION_REMINDER, ApplicationFormStatus.INTERVIEW))
                .andReturn(asList(form));

        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));

        applicationFormDAOMock.save(form);
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewFeedbackEvaluationReminder();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(1).getDigestNotificationType());
    }

    @Test
    public void shouldScheduleInterviewFeedbackEvaluationReminderAndIncludeInterviewAdministrationDelegate() {
        RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(565).build();
        Interviewer interviewer1 = new InterviewerBuilder().user(interviewerUser1).interviewComment(new InterviewComment()).build();
        Interviewer interviewer2 = new InterviewerBuilder().user(interviewerUser2).interviewComment(new InterviewComment()).build();
        RegisteredUser delegate = new RegisteredUserBuilder().id(875).build();
        
        ApplicationForm form = getSampleApplicationForm();
        form.setApplicationAdministrator(delegate);
        form.setLatestInterview(new InterviewBuilder().interviewers(interviewer1, interviewer2).build());
        NotificationRecord record = new NotificationRecordBuilder().id(1).notificationType(NotificationType.INTERVIEW_EVALUATION_REMINDER).build();
        record.setApplication(form);
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_EVALUATION_REMINDER, ApplicationFormStatus.INTERVIEW))
        .andReturn(asList(form));
        
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));
        userDAOMock.save(delegate);
        
        applicationFormDAOMock.save(form);
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewFeedbackEvaluationReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(1).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, delegate.getDigestNotificationType());
    }


    @Test
    public void shouldScheduleReviewRequestAndNoReminder() {
        RegisteredUser reviewerUser1 = new RegisteredUserBuilder().build();
        RegisteredUser reviewerUser2 = new RegisteredUserBuilder().build();
        Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).build();
        Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).id(1).build();
        ReviewRound round = new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build();
        ApplicationForm form = getSampleApplicationForm();
        form.setLatestReviewRound(round);

        expect(
                applicationFormDAOMock.getApplicationsDueNotificationForStateChangeEvent(NotificationType.REVIEW_REQUEST, ApplicationFormStatus.REVIEW))
                .andReturn(asList(form));
        expect(
                applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW))
                .andReturn(asList(form));

        applicationFormDAOMock.save(form);
        userDAOMock.save(reviewerUser1);
        userDAOMock.save(reviewerUser2);

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleReviewReminder();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.TASK_NOTIFICATION, reviewerUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, reviewerUser2.getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.REVIEW_REQUEST));
    }
    
    @Test
    public void shouldScheduleReviewRequestAndReminder() {
        RegisteredUser reviewerUser1 = new RegisteredUserBuilder().build();
        RegisteredUser reviewerUser2 = new RegisteredUserBuilder().build();
        RegisteredUser reviewerUser3 = new RegisteredUserBuilder().build();
        Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).build();
        Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).id(2).build();
        Reviewer reviewer3 = new ReviewerBuilder().user(reviewerUser3).id(3).build();
        ReviewRound round1 = new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build();
        ReviewRound round2 = new ReviewRoundBuilder().reviewers(reviewer3).build();
        ApplicationForm form1 = getSampleApplicationForm();
        ApplicationForm form2 = getSampleApplicationForm();
        form2.setId(345345);
        form1.setLatestReviewRound(round1);
        form2.setLatestReviewRound(round2);
        
        expect(
                applicationFormDAOMock.getApplicationsDueNotificationForStateChangeEvent(NotificationType.REVIEW_REQUEST, ApplicationFormStatus.REVIEW))
                .andReturn(asList(form1));
        expect(
                applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW))
                .andReturn(asList(form1, form2));
        
        applicationFormDAOMock.save(form1);
        applicationFormDAOMock.save(form2);
        userDAOMock.save(reviewerUser1);
        userDAOMock.save(reviewerUser2);
        userDAOMock.save(reviewerUser3);
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleReviewReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, reviewerUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, reviewerUser2.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, reviewerUser3.getDigestNotificationType());
        assertNotNull(form1.getNotificationForType(NotificationType.REVIEW_REQUEST));
        assertNotNull(form2.getNotificationForType(NotificationType.REVIEW_REMINDER));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldScheduleReviewReminderAndNoRequest() {
        RegisteredUser reviewerUser1 = new RegisteredUserBuilder().build();
        RegisteredUser reviewerUser2 = new RegisteredUserBuilder().build();
        RegisteredUser reviewerUser3 = new RegisteredUserBuilder().build();
        Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).build();
        Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).id(2).build();
        Reviewer reviewer3 = new ReviewerBuilder().user(reviewerUser3).id(3).build();
        ReviewRound round1 = new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build();
        ReviewRound round2 = new ReviewRoundBuilder().reviewers(reviewer3).build();
        ApplicationForm form1 = getSampleApplicationForm();
        ApplicationForm form2 = getSampleApplicationForm();
        form2.setId(345345);
        form1.setLatestReviewRound(round1);
        form2.setLatestReviewRound(round2);
        
        expect(
                applicationFormDAOMock.getApplicationsDueNotificationForStateChangeEvent(NotificationType.REVIEW_REQUEST, ApplicationFormStatus.REVIEW))
                .andReturn(Collections.EMPTY_LIST);
        expect(
                applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW))
                .andReturn(asList(form1, form2));
        
        applicationFormDAOMock.save(form1);
        applicationFormDAOMock.save(form2);
        userDAOMock.save(reviewerUser1);
        userDAOMock.save(reviewerUser2);
        userDAOMock.save(reviewerUser3);
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleReviewReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_REMINDER, reviewerUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, reviewerUser2.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, reviewerUser3.getDigestNotificationType());
        assertNotNull(form1.getNotificationForType(NotificationType.REVIEW_REMINDER));
        assertNotNull(form2.getNotificationForType(NotificationType.REVIEW_REMINDER));
    }

    @Test
    public void shouldScheduleUpdateConfirmation() {
        ApplicationForm form = getSampleApplicationForm();

        expect(applicationFormDAOMock.getApplicationsDueUpdateNotification()).andReturn(asList(form));

        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        applicationFormDAOMock.save(form);
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleUpdateConfirmation();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.UPDATE_NOTIFICATION, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.UPDATE_NOTIFICATION, admins.get(1).getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.UPDATED_NOTIFICATION));
    }

    @Test
    public void shouldScheduleValidationRequestAndNoReminder() {
        ApplicationForm form = getSampleApplicationForm();

        expect(
                applicationFormDAOMock.getApplicationsDueNotificationForStateChangeEvent(NotificationType.UPDATED_NOTIFICATION,
                        ApplicationFormStatus.VALIDATION)).andReturn(asList(form));
        expect(
                applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.VALIDATION_REMINDER,
                        ApplicationFormStatus.VALIDATION)).andReturn(asList(form));

        applicationFormDAOMock.save(form);
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleValidationReminder();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.TASK_NOTIFICATION, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, admins.get(1).getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.UPDATED_NOTIFICATION));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldScheduleValidationReminderAndNoRequest() {
        ApplicationForm form = getSampleApplicationForm();
        
        expect(
                applicationFormDAOMock.getApplicationsDueNotificationForStateChangeEvent(NotificationType.UPDATED_NOTIFICATION,
                        ApplicationFormStatus.VALIDATION)).andReturn(Collections.EMPTY_LIST);
        expect(
                applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.VALIDATION_REMINDER,
                        ApplicationFormStatus.VALIDATION)).andReturn(asList(form));
        
        applicationFormDAOMock.save(form);
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleValidationReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(1).getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.VALIDATION_REMINDER));
    }
    
    @Test
    public void shouldScheduleValidationRequestAndReminder() {
        ApplicationForm form1 = getSampleApplicationForm();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(32).build();
        RegisteredUser admin2 = new RegisteredUserBuilder().id(23).build();
        Program program = new ProgramBuilder().administrators(admin1, admin2).build();
        ApplicationForm form2 = getSampleApplicationForm();
        form2.setId(87687);
        form2.setProgram(program);
        
        expect(
                applicationFormDAOMock.getApplicationsDueNotificationForStateChangeEvent(NotificationType.UPDATED_NOTIFICATION,
                        ApplicationFormStatus.VALIDATION)).andReturn(asList(form1));
        expect(
                applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.VALIDATION_REMINDER,
                        ApplicationFormStatus.VALIDATION)).andReturn(asList(form1, form2));
        
        applicationFormDAOMock.save(form1);
        List<RegisteredUser> admins1 = form1.getProgram().getAdministrators();
        userDAOMock.save(admins1.get(0));
        userDAOMock.save(admins1.get(1));
        applicationFormDAOMock.save(form2);
        List<RegisteredUser> admins2 = form2.getProgram().getAdministrators();
        userDAOMock.save(admins2.get(0));
        userDAOMock.save(admins2.get(1));
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleValidationReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, admins1.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, admins1.get(1).getDigestNotificationType());
        assertNotNull(form1.getNotificationForType(NotificationType.UPDATED_NOTIFICATION));
        assertEquals(DigestNotificationType.TASK_REMINDER, admins2.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, admins2.get(1).getDigestNotificationType());
        assertNotNull(form2.getNotificationForType(NotificationType.VALIDATION_REMINDER));
    }
    
    @Test
    public void shouldScheduleRestartApprovalRequestAndNoReminder() {
        ApplicationForm form = getSampleApplicationForm();

        expect(
                applicationFormDAOMock.getApplicationsDueApprovalRequestNotification())
                .andReturn(asList(form));
        expect(
                applicationFormDAOMock.getApplicationDueApprovalRestartRequestReminder())
                .andReturn(asList(form));

        applicationFormDAOMock.save(form);
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleRestartApprovalReminder();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.TASK_NOTIFICATION, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, admins.get(1).getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldScheduleRestartApprovalReminderAndNoRequest() {
        ApplicationForm form = getSampleApplicationForm();
        
        expect(
                applicationFormDAOMock.getApplicationsDueApprovalRequestNotification())
                .andReturn(Collections.EMPTY_LIST);
        expect(
                applicationFormDAOMock.getApplicationDueApprovalRestartRequestReminder())
                .andReturn(asList(form));
        
        applicationFormDAOMock.save(form);
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleRestartApprovalReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(1).getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER));
    }
    
    @Test
    public void shouldScheduleRestartApprovalRequestAndReminder() {
        ApplicationForm form1 = getSampleApplicationForm();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(32).build();
        RegisteredUser admin2 = new RegisteredUserBuilder().id(23).build();
        Program program = new ProgramBuilder().administrators(admin1, admin2).build();
        ApplicationForm form2 = getSampleApplicationForm();
        form2.setId(87687);
        form2.setProgram(program);
        
        expect(
                applicationFormDAOMock.getApplicationsDueApprovalRequestNotification())
                .andReturn(asList(form1));
        expect(
                applicationFormDAOMock.getApplicationDueApprovalRestartRequestReminder())
                .andReturn(asList(form1, form2));
        
        applicationFormDAOMock.save(form1);
        List<RegisteredUser> admins1 = form1.getProgram().getAdministrators();
        userDAOMock.save(admins1.get(0));
        userDAOMock.save(admins1.get(1));
        applicationFormDAOMock.save(form2);
        List<RegisteredUser> admins2 = form2.getProgram().getAdministrators();
        userDAOMock.save(admins2.get(0));
        userDAOMock.save(admins2.get(1));
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleRestartApprovalReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, admins1.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, admins1.get(1).getDigestNotificationType());
        assertNotNull(form1.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION));
        assertEquals(DigestNotificationType.TASK_REMINDER, admins2.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, admins2.get(1).getDigestNotificationType());
        assertNotNull(form2.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER));
    }

    @Test
    public void shouldScheduleApprovedConfirmation() {
        RegisteredUser supervisorUser1 = new RegisteredUserBuilder().build();
        RegisteredUser supervisorUser2 = new RegisteredUserBuilder().build();
        Supervisor supervisor1 = new SupervisorBuilder().id(1).isPrimary(true).user(supervisorUser1).build();
        Supervisor supervisor2 = new SupervisorBuilder().user(supervisorUser2).id(1).build();
        ApprovalRound round = new ApprovalRoundBuilder().supervisors(supervisor1, supervisor2).build();
        ApplicationForm form = getSampleApplicationForm();
        form.setLatestApprovalRound(round);
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueApprovedNotifications()).andReturn(asList(form));

        applicationFormDAOMock.save(form);
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));
        userDAOMock.save(supervisorUser1);

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleApprovedConfirmation();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.UPDATE_NOTIFICATION, supervisor1.getUser().getDigestNotificationType());
        assertEquals(DigestNotificationType.UPDATE_NOTIFICATION, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.UPDATE_NOTIFICATION, admins.get(1).getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.APPROVED_NOTIFICATION));
    }

    @Test
    public void shouldScheduleInterviewAdministrationRequestAndNoReminder() {
        RegisteredUser delegate = new RegisteredUserBuilder().id(564).build();
        ApplicationForm form = getSampleApplicationForm();
        form.setApplicationAdministrator(delegate);
        
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST))
                .andReturn(asList(form));
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER))
                .andReturn(asList(form));
        
        applicationFormDAOMock.save(form);
        userDAOMock.save(delegate);

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewAdministrationReminder();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.TASK_NOTIFICATION, delegate.getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST));
    }
    
    @Test
    public void shouldScheduleInterviewAdministrationRequestAndNoReminderIfItsSecondInterviewRound() {
        RegisteredUser delegate = new RegisteredUserBuilder().id(564).build();
        DateTime interviewDueDate = new DateTime(new Date()).minusMonths(1);
        Interview interview  = new InterviewBuilder().dueDate(interviewDueDate.toDate()).build();
        ApplicationForm form = getSampleApplicationForm();
        form.setLatestInterview(interview);
        form.setApplicationAdministrator(delegate);
        
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST))
                .andReturn(asList(form));
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER))
                .andReturn(asList(form));
        
        applicationFormDAOMock.save(form);
        userDAOMock.save(delegate);
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewAdministrationReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals( DigestNotificationType.TASK_NOTIFICATION, delegate.getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST));
    }
    
    @Test
    public void shouldNotScheduleInterviewAdministrationRequestIfItsCurrentInterviewRound() {
        RegisteredUser delegate = new RegisteredUserBuilder().id(564).build();
        DateTime interviewDueDate = new DateTime(new Date()).plusMonths(1);
        Interview interview  = new InterviewBuilder().dueDate(interviewDueDate.toDate()).build();
        ApplicationForm form = getSampleApplicationForm();
        form.setLatestInterview(interview);
        form.setApplicationAdministrator(delegate);
        
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST))
                .andReturn(asList(form));
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER))
                .andReturn(asList(form));
        
       
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewAdministrationReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.NONE, delegate.getDigestNotificationType());
        assertNull(form.getNotificationForType(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST));
    }
    
    @Test
    public void shouldScheduleInterviewAdministrationRequestAndReminder() {
        RegisteredUser delegate1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser delegate2 = new RegisteredUserBuilder().id(565).build();
        ApplicationForm form1 = getSampleApplicationForm();
        ApplicationForm form2 = getSampleApplicationForm();
        form1.setApplicationAdministrator(delegate1);
        form2.setId(46464);
        form2.setApplicationAdministrator(delegate2);
        
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST))
                .andReturn(asList(form1));
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER))
                .andReturn(asList(form1, form2));
        
        applicationFormDAOMock.save(form1);
        applicationFormDAOMock.save(form2);
        userDAOMock.save(delegate1);
        userDAOMock.save(delegate2);

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewAdministrationReminder();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.TASK_NOTIFICATION, delegate1.getDigestNotificationType());
        assertNotNull(form1.getNotificationForType(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST));
        assertEquals(DigestNotificationType.TASK_REMINDER, delegate2.getDigestNotificationType());
        assertNotNull(form2.getNotificationForType(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldScheduleInterviewAdministrationReminderAndNoRequest() {
        RegisteredUser delegate1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser delegate2 = new RegisteredUserBuilder().id(565).build();
        ApplicationForm form1 = getSampleApplicationForm();
        ApplicationForm form2 = getSampleApplicationForm();
        form1.setApplicationAdministrator(delegate1);
        form2.setId(46464);
        form2.setApplicationAdministrator(delegate2);
        
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST))
                .andReturn(Collections.EMPTY_LIST);
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER))
                .andReturn(asList(form1, form2));
        
        applicationFormDAOMock.save(form1);
        applicationFormDAOMock.save(form2);
        userDAOMock.save(delegate1);
        userDAOMock.save(delegate2);
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewAdministrationReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_REMINDER, delegate1.getDigestNotificationType());
        assertNotNull(form1.getNotificationForType(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER));
        assertEquals(DigestNotificationType.TASK_REMINDER, delegate2.getDigestNotificationType());
        assertNotNull(form2.getNotificationForType(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldScheduleInterviewAdministrationReminderAndNoRequestIfItsSecondInterviewRound() {
        RegisteredUser delegate = new RegisteredUserBuilder().id(564).build();
        DateTime interviewDueDate = new DateTime(new Date()).minusMonths(1);
        Interview interview  = new InterviewBuilder().dueDate(interviewDueDate.toDate()).build();
        ApplicationForm form = getSampleApplicationForm();
        form.setLatestInterview(interview);
        form.setApplicationAdministrator(delegate);
        
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST))
                .andReturn(Collections.EMPTY_LIST);
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER))
                .andReturn(asList(form));
        
        applicationFormDAOMock.save(form);
        userDAOMock.save(delegate);
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewAdministrationReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.TASK_REMINDER, delegate.getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotScheduleInterviewAdministrationReminderIfItsCurrentInterviewRound() {
        RegisteredUser delegate = new RegisteredUserBuilder().id(564).build();
        DateTime interviewDueDate = new DateTime(new Date()).plusMonths(1);
        Interview interview  = new InterviewBuilder().dueDate(interviewDueDate.toDate()).build();
        ApplicationForm form = getSampleApplicationForm();
        form.setLatestInterview(interview);
        form.setApplicationAdministrator(delegate);
        
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REQUEST))
                .andReturn(Collections.EMPTY_LIST);
        expect(
                applicationFormDAOMock.getApplicationsDueInterviewAdministration(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER))
                .andReturn(asList(form));
        
       
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewAdministrationReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(DigestNotificationType.NONE, delegate.getDigestNotificationType());
        assertNull(form.getNotificationForType(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER));
    }

    @Test
    public void shouldScheduleInterviewFeedbackConfirmation() {
        ApplicationForm form = getSampleApplicationForm();
        InterviewComment comment = new InterviewCommentBuilder().application(form).build();

        EasyMock.expect(commentDAOMock.getInterviewCommentsDueNotification()).andReturn(asList(comment));

        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));

        replay(commentDAOMock, userDAOMock);
        service.scheduleInterviewFeedbackConfirmation();
        verify(commentDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.UPDATE_NOTIFICATION, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.UPDATE_NOTIFICATION, admins.get(1).getDigestNotificationType());
        assertTrue(comment.isAdminsNotified());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldScheduleInterviewFeedbackReminderOnly() {
        RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(565).build();
        Interviewer interviewer1 = new InterviewerBuilder().user(interviewerUser1).build();
        Interviewer interviewer2 = new InterviewerBuilder().user(interviewerUser2).build();

        ApplicationForm form = getSampleApplicationForm();
        form.setLatestInterview(new InterviewBuilder().interviewers(interviewer1, interviewer2).build());
        NotificationRecord record = new NotificationRecordBuilder().id(1).notificationType(NotificationType.INTERVIEW_FEEDBACK_REQUEST).build();
        record.setApplication(form);
        applicationFormDAOMock.save(form);
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueInterviewFeedbackNotification()).andReturn(Collections.EMPTY_LIST);
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_FEEDBACK_REMINDER, ApplicationFormStatus.INTERVIEW))
                .andReturn(asList(form));

        userDAOMock.save(interviewerUser1);
        userDAOMock.save(interviewerUser2);

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewFeedbackReminder();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.TASK_REMINDER, interviewerUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, interviewerUser2.getDigestNotificationType());
        assertNotNull(form.getNotificationForType( NotificationType.INTERVIEW_FEEDBACK_REMINDER));
    }

    @Test
    public void shouldScheduleApplicationUnderApprovalNotification() {
        ApplicationForm form = getSampleApplicationForm();

        expect(applicationFormDAOMock.getApplicationsDueMovedToApprovalNotifications()).andReturn(asList(form));

        applicationFormDAOMock.save(form);
        userDAOMock.save(form.getApplicant());

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleApplicationUnderApprovalNotification();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.UPDATE_NOTIFICATION, form.getApplicant().getDigestNotificationType());
        assertNotNull(form.getNotificationForType( NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION));
    }

    @Test
    public void shouldScheduleApplicationUnderReviewNotification() {
        ApplicationForm form = getSampleApplicationForm();

        expect(
                applicationFormDAOMock.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION,
                        ApplicationFormStatus.REVIEW)).andReturn(asList(form));

        applicationFormDAOMock.save(form);
        userDAOMock.save(form.getApplicant());

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleApplicationUnderReviewNotification();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals( DigestNotificationType.UPDATE_NOTIFICATION, form.getApplicant().getDigestNotificationType());
        assertNotNull(form.getNotificationForType( NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSendReferenceReminder() throws Exception {
        service = new ScheduledMailSendingService(mockMailSender, applicationFormDAOMock, commentDAOMock, supervisorDAOMock,
                stageDurationDAOMock, configurationServiceMock,
                refereeDAOMock, userServiceMock, userDAOMock, roleDAOMock, encryptionUtilsMock,
                HOST, applicationContextMock, interviewParticipantDAOMock) {
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
                mockMailSender.resolveSubject(EmailTemplateName.INTERVIEW_VOTE_REMINDER, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME,
                        SAMPLE_APPLICANT_SURNAME)).andReturn(subjectToReturn);

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
        PendingRoleNotification roleNotification = new PendingRoleNotificationBuilder().role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build())
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

   
    @Test
    public void shouldScheduleRejectionConfirmationToAdministrators() {
        ApprovalRound round = new ApprovalRoundBuilder().build();
        ApplicationForm form = getSampleApplicationForm();
        form.setLatestApprovalRound(round);

        expect(applicationFormDAOMock.getApplicationsDueRejectNotifications()).andReturn(asList(form));

        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleRejectionConfirmationToAdministratorsAndSupervisor();
        verify(applicationFormDAOMock, userDAOMock);

        assertNotNull(form.getRejectNotificationDate());
        assertEquals( DigestNotificationType.UPDATE_NOTIFICATION, admins.get(0).getDigestNotificationType());
        assertEquals( DigestNotificationType.UPDATE_NOTIFICATION, admins.get(1).getDigestNotificationType());
        assertNotNull(form.getRejectNotificationDate());
    }
    
    @Test
    public void shouldScheduleRejectionConfirmationToAdministratorsAndSupervisor() {
        RegisteredUser supervisorUser1 = new RegisteredUserBuilder().build();
        RegisteredUser supervisorUser2 = new RegisteredUserBuilder().build();
        Supervisor supervisor1 = new SupervisorBuilder().id(1).isPrimary(true).user(supervisorUser1).build();
        Supervisor supervisor2 = new SupervisorBuilder().user(supervisorUser2).id(1).build();
        ApprovalRound round = new ApprovalRoundBuilder().supervisors(supervisor1, supervisor2).build();
        ApplicationForm form = getSampleApplicationForm();
        form.setLatestApprovalRound(round);
        
        expect(applicationFormDAOMock.getApplicationsDueRejectNotifications()).andReturn(asList(form));
        
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));
        userDAOMock.save(supervisorUser1);
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleRejectionConfirmationToAdministratorsAndSupervisor();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertNotNull(form.getRejectNotificationDate());
        assertEquals( DigestNotificationType.UPDATE_NOTIFICATION, admins.get(0).getDigestNotificationType());
        assertEquals( DigestNotificationType.UPDATE_NOTIFICATION, admins.get(1).getDigestNotificationType());
        assertEquals( DigestNotificationType.UPDATE_NOTIFICATION, supervisorUser1.getDigestNotificationType());
        assertNotNull(form.getRejectNotificationDate());
    }

    @Test
    public void shouldScheduleReviewSubmittedConfirmation() {
        ApplicationForm form = getSampleApplicationForm();
        RegisteredUser commentUser1 = new RegisteredUserBuilder().id(69).build();
        ReviewComment comment1 = new ReviewCommentBuilder().id(1).user(commentUser1).application(form).id(3).build();

        expect(commentDAOMock.getReviewCommentsDueNotification()).andReturn(asList(comment1));
        
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));

        replay(commentDAOMock, userDAOMock);
        service.scheduleReviewSubmittedConfirmation();
        verify(commentDAOMock, userDAOMock);

        assertTrue(comment1.isAdminsNotified());

        assertEquals( DigestNotificationType.UPDATE_NOTIFICATION, admins.get(0).getDigestNotificationType());
        assertEquals( DigestNotificationType.UPDATE_NOTIFICATION, admins.get(1).getDigestNotificationType());
    }

    @Test
    public void shouldScheduleReviewEvaluationReminderToAdministratorsIfAllReviewersHaveProvidedFeedback() {
        RegisteredUser reviewerUser1 = new RegisteredUserBuilder().build();
        RegisteredUser reviewerUser2 = new RegisteredUserBuilder().build();
        Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).review(new ReviewComment()).build();
        Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).id(2).review(new ReviewComment()).build();

        ApplicationForm form = getSampleApplicationForm();

        form.setLatestReviewRound(new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build());

        NotificationRecord record = new NotificationRecordBuilder().id(1).notificationType(NotificationType.REVIEW_EVALUATION_REMINDER).build();
        record.setApplication(form);

        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.REVIEW_EVALUATION_REMINDER, ApplicationFormStatus.REVIEW))
                .andReturn(asList(form));
        applicationFormDAOMock.save(form);
        
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));


        EasyMock.replay(applicationFormDAOMock);

        service.scheduleReviewEvaluationReminder();

        verify(applicationFormDAOMock);

        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(1).getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.REVIEW_EVALUATION_REMINDER));
    }
    
    @Test
    public void shouldScheduleReviewEvaluationReminderToAdministratorsAndDelegateIfAllReviewersHaveProvidedFeedback() {
        RegisteredUser delegate = new RegisteredUserBuilder().build();
        RegisteredUser reviewerUser1 = new RegisteredUserBuilder().build();
        RegisteredUser reviewerUser2 = new RegisteredUserBuilder().build();
        Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).review(new ReviewComment()).build();
        Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).id(2).review(new ReviewComment()).build();
        
        ApplicationForm form = getSampleApplicationForm();
        form.setApplicationAdministrator(delegate);
        
        form.setLatestReviewRound(new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build());
        
        NotificationRecord record = new NotificationRecordBuilder().id(1).notificationType(NotificationType.REVIEW_EVALUATION_REMINDER).build();
        record.setApplication(form);
        
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.REVIEW_EVALUATION_REMINDER, ApplicationFormStatus.REVIEW))
        .andReturn(asList(form));
        applicationFormDAOMock.save(form);
        
        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        userDAOMock.save(admins.get(0));
        userDAOMock.save(admins.get(1));
        userDAOMock.save(delegate);
        
        
        EasyMock.replay(applicationFormDAOMock);
        
        service.scheduleReviewEvaluationReminder();
        
        verify(applicationFormDAOMock);
        
        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, admins.get(1).getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, delegate.getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.REVIEW_EVALUATION_REMINDER));
    }

    @Test
    public void shouldNotScheduleReviewEvaluationReminderIfAllReviewersHaveProvidedFeedback() {
        RegisteredUser reviewerUser1 = new RegisteredUserBuilder().build();
        RegisteredUser reviewerUser2 = new RegisteredUserBuilder().build();
        Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).review(new ReviewComment()).build();
        Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).id(2).build();


        ApplicationForm form = getSampleApplicationForm();
        form.setLatestReviewRound(new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build());

        NotificationRecord record = new NotificationRecordBuilder().id(1).notificationType(NotificationType.REVIEW_EVALUATION_REMINDER).build();
        record.setApplication(form);

        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.REVIEW_EVALUATION_REMINDER, ApplicationFormStatus.REVIEW))
                .andReturn(asList(form));

        EasyMock.replay(applicationFormDAOMock, userDAOMock);

        service.scheduleReviewEvaluationReminder();

        verify(applicationFormDAOMock, userDAOMock);

        List<RegisteredUser> admins = form.getProgram().getAdministrators();
        assertEquals(DigestNotificationType.NONE, admins.get(0).getDigestNotificationType());
        assertEquals(DigestNotificationType.NONE, admins.get(1).getDigestNotificationType());
        assertNull(form.getNotificationForType(NotificationType.REVIEW_EVALUATION_REMINDER));
    }

    @Test
    public void shouldScheduleConfirmSupervisionRequestAndNoReminder() {
        RegisteredUser supervisorUser1 = new RegisteredUserBuilder().build();
        RegisteredUser supervisorUser2 = new RegisteredUserBuilder().build();
        Supervisor supervisor1 = new SupervisorBuilder().id(1).user(supervisorUser1).build();
        Supervisor supervisor2 = new SupervisorBuilder().user(supervisorUser2).id(2).build();

        
        
        expect(supervisorDAOMock.getPrimarySupervisorsDueNotification()).andReturn(asList(supervisor1, supervisor2));
        expect(supervisorDAOMock.getPrimarySupervisorsDueReminder()).andReturn(asList(supervisor1, supervisor2));
        
        userDAOMock.save(supervisorUser1);
        userDAOMock.save(supervisorUser2);

        replay(supervisorDAOMock, userDAOMock);
        service.scheduleConfirmSupervisionReminder();
        verify(supervisorDAOMock, userDAOMock);

        assertNotNull(supervisor1.getLastNotified());
        assertNotNull(supervisor2.getLastNotified());

        assertEquals(DigestNotificationType.TASK_NOTIFICATION, supervisorUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, supervisorUser2.getDigestNotificationType());
    }
    
    @Test
    public void shouldScheduleConfirmSupervisionRequestAndReminder() {
        RegisteredUser supervisorUser1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser supervisorUser2 = new RegisteredUserBuilder().id(2).build();
        Supervisor supervisor1 = new SupervisorBuilder().id(1).user(supervisorUser1).build();
        Supervisor supervisor2 = new SupervisorBuilder().user(supervisorUser2).id(2).build();
        
        
        
        expect(supervisorDAOMock.getPrimarySupervisorsDueNotification()).andReturn(asList(supervisor1));
        expect(supervisorDAOMock.getPrimarySupervisorsDueReminder()).andReturn(asList(supervisor1, supervisor2));
        
        userDAOMock.save(supervisorUser1);
        userDAOMock.save(supervisorUser2);
        
        replay(supervisorDAOMock, userDAOMock);
        service.scheduleConfirmSupervisionReminder();
        verify(supervisorDAOMock, userDAOMock);
        
        assertNotNull(supervisor1.getLastNotified());
        assertNotNull(supervisor2.getLastNotified());
        
        assertEquals(DigestNotificationType.TASK_NOTIFICATION, supervisorUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, supervisorUser2.getDigestNotificationType());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldScheduleConfirmSupervisionReminderAndNoRequest() {
        RegisteredUser supervisorUser1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser supervisorUser2 = new RegisteredUserBuilder().id(2).build();
        Supervisor supervisor1 = new SupervisorBuilder().id(1).user(supervisorUser1).build();
        Supervisor supervisor2 = new SupervisorBuilder().user(supervisorUser2).id(2).build();
        
        
        
        expect(supervisorDAOMock.getPrimarySupervisorsDueNotification()).andReturn(Collections.EMPTY_LIST);
        expect(supervisorDAOMock.getPrimarySupervisorsDueReminder()).andReturn(asList(supervisor1, supervisor2));
        
        userDAOMock.save(supervisorUser1);
        userDAOMock.save(supervisorUser2);
        
        replay(supervisorDAOMock, userDAOMock);
        service.scheduleConfirmSupervisionReminder();
        verify(supervisorDAOMock, userDAOMock);
        
        assertNotNull(supervisor1.getLastNotified());
        assertNotNull(supervisor2.getLastNotified());
        
        assertEquals(DigestNotificationType.TASK_REMINDER, supervisorUser1.getDigestNotificationType());
        assertEquals(DigestNotificationType.TASK_REMINDER, supervisorUser2.getDigestNotificationType());
    }

    @Test
    public void shouldScheduleApplicationUnderInterviewNotification() {
        ApplicationForm form = getSampleApplicationForm();

        expect(
                applicationFormDAOMock.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION,
                        ApplicationFormStatus.INTERVIEW)).andReturn(asList(form));

        applicationFormDAOMock.save(form);
        
        userDAOMock.save(form.getApplicant());

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleApplicationUnderInterviewNotification();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.UPDATE_NOTIFICATION, form.getApplicant().getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION));
    }

    @Test
    public void shouldScheduleApplicationUnderInterviewNotification2() {
        ApplicationForm form = getSampleApplicationForm();
        NotificationRecord record = new NotificationRecordBuilder().notificationType(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION).build();
        form.setNotificationRecords(asList(record));

        EasyMock.expect(
                applicationFormDAOMock.getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION,
                        ApplicationFormStatus.INTERVIEW)).andReturn(asList(form));

        applicationFormDAOMock.save(form);
        userDAOMock.save(form.getApplicant());

        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleApplicationUnderInterviewNotification();
        verify(applicationFormDAOMock, userDAOMock);

        assertEquals(DigestNotificationType.UPDATE_NOTIFICATION, form.getApplicant().getDigestNotificationType());
        assertNotNull(form.getNotificationForType(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION));
    }

    @Test
    public void shouldUpdateDigestNotificationTypeToTaskNotification() {
        RegisteredUser scandura = new RegisteredUserBuilder().email("cls@zuhlke.com").firstName("Claudio").lastName("Scandura").build();
        scandura.setDigestNotificationType(DigestNotificationType.NONE);
        service.setDigestNotificationType(scandura, DigestNotificationType.TASK_NOTIFICATION);
        assertEquals(scandura.getDigestNotificationType(), DigestNotificationType.TASK_NOTIFICATION);
    }

    @Test
    public void shouldUpdateDigestNotificationTypeToTaskReminder() {
        RegisteredUser scandura = new RegisteredUserBuilder().email("cls@zuhlke.com").firstName("Claudio").lastName("Scandura").build();
        scandura.setDigestNotificationType(DigestNotificationType.NONE);
        service.setDigestNotificationType(scandura, DigestNotificationType.TASK_REMINDER);
        assertEquals(scandura.getDigestNotificationType(), DigestNotificationType.TASK_REMINDER);
    }

    @Test
    public void shouldUpdateDigestNotificationTypeToUpdateNotification() {
        RegisteredUser scandura = new RegisteredUserBuilder().email("cls@zuhlke.com").firstName("Claudio").lastName("Scandura").build();
        scandura.setDigestNotificationType(DigestNotificationType.NONE);
        service.setDigestNotificationType(scandura, DigestNotificationType.UPDATE_NOTIFICATION);
        assertEquals(scandura.getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
    }

    @Test
    public void shouldNotUpdateDigestNotificationTypeToUTaskNotification() {
        RegisteredUser scandura = new RegisteredUserBuilder().email("cls@zuhlke.com").firstName("Claudio").lastName("Scandura").build();
        scandura.setDigestNotificationType(DigestNotificationType.TASK_REMINDER);
        service.setDigestNotificationType(scandura, DigestNotificationType.TASK_NOTIFICATION);
        assertEquals(scandura.getDigestNotificationType(), DigestNotificationType.TASK_REMINDER);
    }

    @Test
    public void shouldNotUpdateDigestNotificationTypeToUpdateNotification() {
        RegisteredUser scandura = new RegisteredUserBuilder().email("cls@zuhlke.com").firstName("Claudio").lastName("Scandura").build();
        scandura.setDigestNotificationType(DigestNotificationType.TASK_REMINDER);
        service.setDigestNotificationType(scandura, DigestNotificationType.UPDATE_NOTIFICATION);
        assertEquals(scandura.getDigestNotificationType(), DigestNotificationType.TASK_REMINDER);
    }

    @Test
    public void shouldUpdateDigestNotificationTypeToNone() {
        RegisteredUser scandura = new RegisteredUserBuilder().email("cls@zuhlke.com").firstName("Claudio").lastName("Scandura").build();
        scandura.setDigestNotificationType(DigestNotificationType.TASK_REMINDER);
        service.setDigestNotificationType(scandura, DigestNotificationType.NONE);
        assertEquals(scandura.getDigestNotificationType(), DigestNotificationType.NONE);
    }

}
