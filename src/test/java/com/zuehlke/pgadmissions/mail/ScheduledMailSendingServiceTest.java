package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_TASK_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_TASK_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.DIGEST_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REFEREE_REMINDER;
import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.REGISTRY_VALIDATION_REQUEST;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

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
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.NotificationRecordDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
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
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
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
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DigestNotificationType;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSourceFactory;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class ScheduledMailSendingServiceTest extends MailSendingServiceTest {

	private NotificationRecordDAO notificationRecordDAOMock;

	private CommentDAO commentDAOMock;

	private SupervisorDAO supervisorDAOMock;

	private StageDurationDAO stageDurationDAOMock;

	private ReviewerDAO reviewerDAOMock;

	private ApplicationsService applicationsServiceMock;

	private CommentFactory commentFactoryMock;

	private CommentService commentServiceMock;

	private PdfAttachmentInputSourceFactory pdfAttachmentInputSourceFactoryMock;

	private PdfDocumentBuilder pdfDocumentBuilderMock;

	private RefereeDAO refereeDAOMock;
	
	private ScheduledMailSendingService service;

	private UserService userServiceMock;
	
    private RoleDAO roleDAOMock;
    
    private EncryptionUtils encryptionUtilsMock;
    
    private static final String HOST = "http://localhost:8080";
    
    private static final String SERVICE_OFFER = "5 working days";

	@Before
	public void prepare() {
		notificationRecordDAOMock = createMock(NotificationRecordDAO.class);
		commentDAOMock = createMock(CommentDAO.class);
		supervisorDAOMock = createMock(SupervisorDAO.class);
		stageDurationDAOMock = createMock(StageDurationDAO.class);
		applicationsServiceMock = createMock(ApplicationsService.class);
		commentFactoryMock = createMock(CommentFactory.class);
		commentServiceMock = createMock(CommentService.class);
		pdfAttachmentInputSourceFactoryMock = createMock(PdfAttachmentInputSourceFactory.class);
		pdfDocumentBuilderMock = createMock(PdfDocumentBuilder.class);
		refereeDAOMock = createMock(RefereeDAO.class);
		userServiceMock = createMock(UserService.class);
		userDAOMock = createMock(UserDAO.class);
        roleDAOMock = createMock(RoleDAO.class);
        encryptionUtilsMock = createMock(EncryptionUtils.class);
		service = new ScheduledMailSendingService(
				mockMailSender,
				applicationFormDAOMock,
				notificationRecordDAOMock,
				commentDAOMock,
				supervisorDAOMock,
				stageDurationDAOMock,
				applicationsServiceMock,
				configurationServiceMock,
				commentFactoryMock,
				commentServiceMock,
				pdfAttachmentInputSourceFactoryMock,
				pdfDocumentBuilderMock,
				refereeDAOMock,
				userServiceMock,
				userDAOMock, 
				roleDAOMock,
		        encryptionUtilsMock,
		        HOST,
		        SERVICE_OFFER);
	}
	
	@Test
	public void shouldSendDigestToUsers() {
		RegisteredUser user1 = new RegisteredUserBuilder().id(1)
				.digestNotificationType(DigestNotificationType.TASK_NOTIFICATION)
				.build();
		RegisteredUser user2 = new RegisteredUserBuilder().id(2)
				.digestNotificationType(DigestNotificationType.TASK_REMINDER)
				.build();
		RegisteredUser user3 = new RegisteredUserBuilder().id(3)
				.digestNotificationType(DigestNotificationType.UPDATE_NOTIFICATION)
				.build();
		RegisteredUser user4 = new RegisteredUserBuilder().id(4)
				.digestNotificationType(DigestNotificationType.NONE)
				.build();
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
		
		expect(mockMailSender.resolveSubject(DIGEST_TASK_NOTIFICATION, (Object[])null))
		.andReturn(subjectToReturn1);
		expect(mockMailSender.resolveSubject(DIGEST_TASK_REMINDER, (Object[])null))
		.andReturn(subjectToReturn2);
		expect(mockMailSender.resolveSubject(DIGEST_UPDATE_NOTIFICATION, (Object[])null))
		.andReturn(subjectToReturn3);
		
		expect(userServiceMock.getAllUsersInNeedOfADigestNotification())
		.andReturn(asList(1, 2, 3, 4));
		expect(userServiceMock.getUser(1)).andReturn(user1);
		expect(userServiceMock.getUser(2)).andReturn(user2);
		expect(userServiceMock.getUser(3)).andReturn(user3);
		expect(userServiceMock.getUser(4)).andReturn(user4);
		
		userServiceMock.resetDigestNotificationsForAllUsers();
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>(CaptureType.ALL);
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		expectLastCall().times(3);
		
		
		replay(mockMailSender, userServiceMock);
		service.sendDigestsToUsers();
		verify(mockMailSender, userServiceMock);
		
		List<PrismEmailMessage> messages = messageCaptor.getValues();
		assertEquals(3, messages.size());
		
		assertNotNull(messages.get(0).getTo());
		assertEquals(1, messages.get(0).getTo().size());
		assertEquals((Integer)1, messages.get(0).getTo().get(0).getId());
		assertEquals(subjectToReturn1, messages.get(0).getSubjectCode());
		assertModelEquals(model1, messages.get(0).getModel());
		
		assertNotNull(messages.get(1).getTo());
		assertEquals(1, messages.get(1).getTo().size());
		assertEquals((Integer)2, messages.get(1).getTo().get(0).getId());
		assertEquals(subjectToReturn2, messages.get(1).getSubjectCode());
		assertModelEquals(model2, messages.get(1).getModel());
		
		assertNotNull(messages.get(2).getTo());
		assertEquals(1, messages.get(2).getTo().size());
		assertEquals((Integer)3, messages.get(2).getTo().get(0).getId());
		assertEquals(subjectToReturn3, messages.get(2).getSubjectCode());
		assertModelEquals(model3, messages.get(2).getModel());
	}
	
	@Ignore
    @Test
    public void shouldScheduleApprovalReminder() {

        DateTime date = new DateTime(2100, 1, 2, 3, 4);
        StageDuration stageDuration = new StageDurationBuilder().unit(DurationUnitEnum.HOURS).duration(1000).build();
        RegisteredUser approver1 = new RegisteredUserBuilder().build();
        RegisteredUser approver2 = new RegisteredUserBuilder().build();
        ApplicationForm form = getSampleApplicationForm();
        ApprovalRound round1 = new ApprovalRoundBuilder().application(form).createdDate(date.toDate()).build();
        form.getProgram().setApprovers(asList(approver1, approver2));
        form.setLatestApprovalRound(round1);

        expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(stageDuration);

        EasyMock.expect(applicationFormDAOMock.getApplicationsDueApprovalReminder()).andReturn(asList(form));

        applicationFormDAOMock.save(form);
        replay(stageDurationDAOMock, applicationFormDAOMock);
        service.scheduleApprovalRequestAndReminder();
        verify(stageDurationDAOMock, applicationFormDAOMock);

        assertEquals(approver1.getDigestNotificationType(), DigestNotificationType.TASK_REMINDER);
        assertEquals(approver2.getDigestNotificationType(), DigestNotificationType.TASK_REMINDER);
    }

    @Test
	public void shouldScheduleInterviewFeedbackRequestAndNoReminder() {
	    RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(564).build();
	    RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(565).build();
	    Interviewer interviewer1 = new InterviewerBuilder().user(interviewerUser1).build();
	    Interviewer interviewer2 = new InterviewerBuilder().user(interviewerUser2).build();

		ApplicationForm form = getSampleApplicationForm();
		form.setLatestInterview(new InterviewBuilder().interviewers(interviewer1, interviewer2).build());
		NotificationRecord record = new NotificationRecordBuilder().id(1)
				.notificationType(NotificationType.INTERVIEW_FEEDBACK_REQUEST)
				.build();
		record.setApplication(form);
		applicationFormDAOMock.save(form);
		EasyMock.expect(
				applicationFormDAOMock.getApplicationsDueInterviewFeedbackNotification())
				.andReturn(asList(form));
		EasyMock.expect(
		        applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_FEEDBACK_REMINDER, ApplicationFormStatus.INTERVIEW))
		        .andReturn(asList(form));
		
		userDAOMock.save(interviewerUser1);
		userDAOMock.save(interviewerUser2);
		
		replay(applicationFormDAOMock, userDAOMock);
		service.scheduleInterviewFeedbackRequestAndReminder();
		verify(applicationFormDAOMock, userDAOMock);
		
		assertEquals(interviewerUser1.getDigestNotificationType(),  DigestNotificationType.TASK_NOTIFICATION);
		assertEquals(interviewerUser2.getDigestNotificationType(),  DigestNotificationType.TASK_NOTIFICATION);
	}
    
    @Test
    public void shouldScheduleInterviewFeedbackRequestAndReminderforDifferentApplications() {
        RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(565).build();
        Interviewer interviewer1 = new InterviewerBuilder().user(interviewerUser1).build();
        Interviewer interviewer2 = new InterviewerBuilder().user(interviewerUser2).build();
        
        ApplicationForm form1 = getSampleApplicationForm();
        form1.setLatestInterview(new InterviewBuilder().interviewers(interviewer1).build());
        
        ApplicationForm form2 = getSampleApplicationForm();
        form2.setId(8459);
        form2.setLatestInterview(new InterviewBuilder().interviewers(interviewer2).build());
        
        NotificationRecord record = new NotificationRecordBuilder().id(1)
                .notificationType(NotificationType.INTERVIEW_FEEDBACK_REQUEST)
                .build();
        record.setApplication(form1);
        applicationFormDAOMock.save(form1);
        applicationFormDAOMock.save(form2);
        EasyMock.expect(
                applicationFormDAOMock.getApplicationsDueInterviewFeedbackNotification())
                .andReturn(asList(form1));
        EasyMock.expect(
                applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_FEEDBACK_REMINDER, ApplicationFormStatus.INTERVIEW))
                .andReturn(asList(form1, form2));
        
        userDAOMock.save(interviewerUser1);
        userDAOMock.save(interviewerUser2);
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewFeedbackRequestAndReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(interviewerUser1.getDigestNotificationType(),  DigestNotificationType.TASK_NOTIFICATION);
        assertEquals(interviewerUser2.getDigestNotificationType(),  DigestNotificationType.TASK_REMINDER);
    }
	
	@Test
	public void shouldScheduleInterviewFeedbackEvaluationReminderIfAllInterviewersHaveProvidedFeedback() {
	    RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(564).build();
        RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(565).build();
        Interviewer interviewer1 = new InterviewerBuilder()
                .user(interviewerUser1)
                .interviewComment(new InterviewComment())
                .build();
        Interviewer interviewer2 = new InterviewerBuilder()
                .user(interviewerUser2)
                .interviewComment(new InterviewComment())
                .build();		
        
        ApplicationForm form = getSampleApplicationForm();
        form.setLatestInterview(new InterviewBuilder().interviewers(interviewer1, interviewer2).build());
		NotificationRecord record = new NotificationRecordBuilder().id(1)
				.notificationType(NotificationType.INTERVIEW_EVALUATION_REMINDER)
				.build();
		record.setApplication(form);
		EasyMock.expect(
				applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_EVALUATION_REMINDER, ApplicationFormStatus.INTERVIEW))
				.andReturn(asList(form));
		
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		userDAOMock.save(admins.get(0));
		userDAOMock.save(admins.get(1));
		
		applicationFormDAOMock.save(form);
		replay(applicationFormDAOMock, userDAOMock);
		service.scheduleInterviewFeedbackEvaluationReminder();
		verify(applicationFormDAOMock, userDAOMock);
		
		assertEquals(admins.get(0).getDigestNotificationType(),  DigestNotificationType.TASK_REMINDER);
		assertEquals(admins.get(1).getDigestNotificationType(),  DigestNotificationType.TASK_REMINDER);
	}
	
	@Test
	public void shouldNotScheduleInterviewFeedbackEvaluationReminderIfNotAllInterviewersHaveProvidedFeedback() {
	    RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(564).build();
	    RegisteredUser interviewerUser2 = new RegisteredUserBuilder().id(565).build();
	    Interviewer interviewer1 = new InterviewerBuilder()
	    .user(interviewerUser1)
	    .interviewComment(null)
	    .build();
	    Interviewer interviewer2 = new InterviewerBuilder()
	    .user(interviewerUser2)
	    .interviewComment(new InterviewComment())
	    .build();		
	    
	    ApplicationForm form = getSampleApplicationForm();
	    form.setLatestInterview(new InterviewBuilder().interviewers(interviewer1, interviewer2).build());
	    NotificationRecord record = new NotificationRecordBuilder().id(1)
	            .notificationType(NotificationType.INTERVIEW_EVALUATION_REMINDER)
	            .build();
	    record.setApplication(form);
	    EasyMock.expect(
	            applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_EVALUATION_REMINDER, ApplicationFormStatus.INTERVIEW))
	            .andReturn(asList(form));
	    
	    List<RegisteredUser> admins = form.getProgram().getAdministrators();
	    
	    replay(applicationFormDAOMock, userDAOMock);
	    service.scheduleInterviewFeedbackEvaluationReminder();
	    verify(applicationFormDAOMock, userDAOMock);
	    
	    assertEquals(admins.get(0).getDigestNotificationType(),  DigestNotificationType.NONE);
	    assertEquals(admins.get(1).getDigestNotificationType(),  DigestNotificationType.NONE);
	}
	
	@Test
	public void shouldScheduleReviewReminder() {
		RegisteredUser reviewerUser1 = new RegisteredUserBuilder().build();
		RegisteredUser reviewerUser2 = new RegisteredUserBuilder().build();
		Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).build();
		Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).id(1).build();
		ReviewRound round = new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build();
		ApplicationForm form = getSampleApplicationForm();
		form.setLatestReviewRound(round);
		NotificationRecord record = new NotificationRecordBuilder().notificationType(NotificationType.REVIEW_REMINDER).build();
		form.setNotificationRecords(asList(record));
		
		EasyMock.expect(
				applicationFormDAOMock.
				getApplicationsDueUserReminder(NotificationType.REVIEW_REMINDER, ApplicationFormStatus.REVIEW))
				.andReturn(asList(form));
		
		applicationFormDAOMock.save(form);
		
		replay(applicationFormDAOMock);
		service.scheduleReviewReminder();
		verify(applicationFormDAOMock);
		
		assertEquals(reviewerUser1.getDigestNotificationType(), DigestNotificationType.TASK_REMINDER);
		assertEquals(reviewerUser2.getDigestNotificationType(), DigestNotificationType.TASK_REMINDER);
	}
	
	@Test
	public void shouldScheduleReviewRequest() {
		RegisteredUser reviewerUser1 = new RegisteredUserBuilder().build();
		RegisteredUser reviewerUser2 = new RegisteredUserBuilder().build();
		Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).build();
		Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).id(1).build();
		ReviewRound round = new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build();
		ApplicationForm form = getSampleApplicationForm();
		form.setLatestReviewRound(round);
		NotificationRecord record = new NotificationRecordBuilder().notificationType(NotificationType.REVIEW_REQUEST).build();
		form.setNotificationRecords(asList(record));
		
		EasyMock.expect(
				applicationFormDAOMock.
				getApplicationsDueNotificationForStateChangeEvent(NotificationType.REVIEW_REQUEST, ApplicationFormStatus.REVIEW))
				.andReturn(asList(form));
		
		applicationFormDAOMock.save(form);
		
		replay(applicationFormDAOMock, userServiceMock);
		service.scheduleReviewRequest();
		verify(applicationFormDAOMock, userServiceMock);
		
		assertEquals(reviewerUser1.getDigestNotificationType(), DigestNotificationType.TASK_NOTIFICATION);
		assertEquals(reviewerUser2.getDigestNotificationType(), DigestNotificationType.TASK_NOTIFICATION);
	}
	
	@Test
	public void shouldScheduleUpdateConfirmation() {
		ApplicationForm form = getSampleApplicationForm();
		NotificationRecord record = new NotificationRecordBuilder().notificationType(NotificationType.UPDATED_NOTIFICATION).build();
		form.setNotificationRecords(asList(record));
		
		EasyMock.expect(
				applicationFormDAOMock.
				getApplicationsDueUpdateNotification())
				.andReturn(asList(form));
		
		applicationFormDAOMock.save(form);
		
		replay(applicationFormDAOMock);
		service.scheduleUpdateConfirmation();
		verify(applicationFormDAOMock);
		
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		assertEquals(admins.get(0).getDigestNotificationType(),  DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(admins.get(1).getDigestNotificationType(),  DigestNotificationType.UPDATE_NOTIFICATION);
	}
	
	@Test
	public void shouldScheduleValidationRequest() {
		ApplicationForm form = getSampleApplicationForm();
		NotificationRecord record = new NotificationRecordBuilder().notificationType(NotificationType.UPDATED_NOTIFICATION).build();
		form.setNotificationRecords(asList(record));
		
		EasyMock.expect(
				applicationFormDAOMock.
				getApplicationsDueNotificationForStateChangeEvent(NotificationType.UPDATED_NOTIFICATION, ApplicationFormStatus.VALIDATION))
				.andReturn(asList(form));
		
		applicationFormDAOMock.save(form);
		
		replay(applicationFormDAOMock);
		service.scheduleValidationRequest();
		verify(applicationFormDAOMock);
		
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		assertEquals(admins.get(0).getDigestNotificationType(),  DigestNotificationType.TASK_NOTIFICATION);
		assertEquals(admins.get(1).getDigestNotificationType(),  DigestNotificationType.TASK_NOTIFICATION);
	}
	
	@Test
	public void shouldScheduleValidationReminder() {
		ApplicationForm form = getSampleApplicationForm();
		NotificationRecord record = new NotificationRecordBuilder().notificationType(NotificationType.VALIDATION_REMINDER).build();
		form.setNotificationRecords(asList(record));
		
		EasyMock.expect(
				applicationFormDAOMock.
				getApplicationsDueUserReminder(NotificationType.VALIDATION_REMINDER, ApplicationFormStatus.VALIDATION))
				.andReturn(asList(form));
		
		applicationFormDAOMock.save(form);
		
		replay(applicationFormDAOMock);
		service.scheduleValidationReminder();
		verify(applicationFormDAOMock);
		
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		assertEquals(admins.get(0).getDigestNotificationType(),  DigestNotificationType.TASK_REMINDER);
		assertEquals(admins.get(1).getDigestNotificationType(),  DigestNotificationType.TASK_REMINDER);
	}
	
	@Test
	public void shouldScheduleRestartApprovalRequest() {
		ApplicationForm form = getSampleApplicationForm();
		NotificationRecord record = new NotificationRecordBuilder().notificationType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).build();
		form.setNotificationRecords(asList(record));
		
		EasyMock.expect(
				applicationFormDAOMock.
				getApplicationsDueApprovalRequestNotification())
				.andReturn(asList(form));
		
		applicationFormDAOMock.save(form);
		
		replay(applicationFormDAOMock);
		service.scheduleRestartApprovalRequest();
		verify(applicationFormDAOMock);
		
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		assertEquals(admins.get(0).getDigestNotificationType(),  DigestNotificationType.TASK_NOTIFICATION);
		assertEquals(admins.get(1).getDigestNotificationType(),  DigestNotificationType.TASK_NOTIFICATION);
	}
	
	@Test
	public void shouldScheduleRestartApprovalReminder() {
		ApplicationForm form = getSampleApplicationForm();
		NotificationRecord record = new NotificationRecordBuilder().notificationType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER).build();
		form.setNotificationRecords(asList(record));
		
		EasyMock.expect(
				applicationFormDAOMock.
				getApplicationDueApprovalRestartRequestReminder())
				.andReturn(asList(form));
		
		applicationFormDAOMock.save(form);
		
		replay(applicationFormDAOMock);
		service.scheduleRestartApprovalReminder();
		verify(applicationFormDAOMock);
		
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		assertEquals(admins.get(0).getDigestNotificationType(),  DigestNotificationType.TASK_REMINDER);
		assertEquals(admins.get(1).getDigestNotificationType(),  DigestNotificationType.TASK_REMINDER);
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
		NotificationRecord record = new NotificationRecordBuilder().id(1)
				.notificationType(NotificationType.APPROVED_NOTIFICATION)
				.build();
		form.setNotificationRecords(asList(record));
		EasyMock.expect(
				applicationFormDAOMock.
				getApplicationsDueApprovedNotifications())
				.andReturn(asList(form));
		
		applicationFormDAOMock.save(form);
		
		replay(applicationFormDAOMock);
		service.scheduleApprovedConfirmation();
		verify(applicationFormDAOMock);
		
		assertNotNull(record.getDate());
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		assertEquals(admins.get(0).getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(admins.get(1).getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(supervisor1.getUser().getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
	}
	
	
	@Test
	public void shouldScheduleInterviewAdministrationReminder() {
		RegisteredUser delegate = new RegisteredUserBuilder().id(564).build();
		ApplicationForm form = getSampleApplicationForm();
		form.setApplicationAdministrator(delegate);
		NotificationRecord record = new NotificationRecordBuilder().id(1)
				.notificationType(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER)
				.build();
		record.setApplication(form);
		EasyMock.expect(
				notificationRecordDAOMock.
				getNotificationsWithTimeStampGreaterThan(isA(Date.class), eq(NotificationType.INTERVIEW_ADMINISTRATION_REMINDER)))
				.andReturn(asList(record));
		
		replay(notificationRecordDAOMock);
		service.scheduleInterviewAdministrationReminder();
		verify(notificationRecordDAOMock);
		
		assertNotNull(record.getDate());
		assertEquals(delegate.getDigestNotificationType(), DigestNotificationType.TASK_REMINDER);
	}
	
	@Test
	public void shouldScheduleInterviewFeedbackConfirmation() {
		ApplicationForm form = getSampleApplicationForm();
		InterviewComment comment = new InterviewCommentBuilder().application(form).build();
		
		EasyMock.expect(
				commentDAOMock.
				getInterviewCommentsDueNotification())
				.andReturn(asList(comment));
		
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
		userDAOMock.save(admins.get(0));
		userDAOMock.save(admins.get(1));
		
		replay(commentDAOMock, userDAOMock);
		service.scheduleInterviewFeedbackConfirmation();
		verify(commentDAOMock, userDAOMock);
		
		assertEquals(admins.get(0).getDigestNotificationType(),  DigestNotificationType.UPDATE_NOTIFICATION);
		assertEquals(admins.get(1).getDigestNotificationType(),  DigestNotificationType.UPDATE_NOTIFICATION);
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
        NotificationRecord record = new NotificationRecordBuilder().id(1)
                .notificationType(NotificationType.INTERVIEW_FEEDBACK_REQUEST)
                .build();
        record.setApplication(form);
        applicationFormDAOMock.save(form);
        EasyMock.expect(
                applicationFormDAOMock.getApplicationsDueInterviewFeedbackNotification())
                .andReturn(Collections.EMPTY_LIST);
        EasyMock.expect(
                applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.INTERVIEW_FEEDBACK_REMINDER, ApplicationFormStatus.INTERVIEW))
                .andReturn(asList(form));
        
        userDAOMock.save(interviewerUser1);
        userDAOMock.save(interviewerUser2);
        
        replay(applicationFormDAOMock, userDAOMock);
        service.scheduleInterviewFeedbackRequestAndReminder();
        verify(applicationFormDAOMock, userDAOMock);
        
        assertEquals(interviewerUser1.getDigestNotificationType(),  DigestNotificationType.TASK_REMINDER);
        assertEquals(interviewerUser2.getDigestNotificationType(),  DigestNotificationType.TASK_REMINDER);
	}
	
	@Test
	public void shouldScheduleApplicationUnderApprovalNotification() {
		ApplicationForm form = getSampleApplicationForm();
		NotificationRecord record = new NotificationRecordBuilder().notificationType(NotificationType.APPLICATION_MOVED_TO_APPROVAL_NOTIFICATION).build();
		form.setNotificationRecords(asList(record));
		
		EasyMock.expect(
				applicationFormDAOMock.
				getApplicationsDueMovedToApprovalNotifications())
				.andReturn(asList(form));
		
		applicationFormDAOMock.save(form);
		
		replay(applicationFormDAOMock);
		service.scheduleApplicationUnderApprovalNotification();
		verify(applicationFormDAOMock);
		
		assertEquals(form.getApplicant().getDigestNotificationType(),  DigestNotificationType.UPDATE_NOTIFICATION);
	}

	@Test
	public void shouldScheduleApplicationUnderReviewNotification() {
		ApplicationForm form = getSampleApplicationForm();
		NotificationRecord record = new NotificationRecordBuilder().notificationType(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION).build();
		form.setNotificationRecords(asList(record));
		
		EasyMock.expect(
				applicationFormDAOMock.
				getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_REVIEW_NOTIFICATION, ApplicationFormStatus.REVIEW))
				.andReturn(asList(form));
		
		applicationFormDAOMock.save(form);
		
		replay(applicationFormDAOMock);
		service.scheduleApplicationUnderReviewNotification();
		verify(applicationFormDAOMock);
		
		assertEquals(form.getApplicant().getDigestNotificationType(),  DigestNotificationType.UPDATE_NOTIFICATION);
	}
	
	@Test
	public void shouldSendReferenceReminder() throws Exception {
	       service = new ScheduledMailSendingService(
	               mockMailSender,
	               applicationFormDAOMock,
	               notificationRecordDAOMock,
	               commentDAOMock,
	               supervisorDAOMock,
	               stageDurationDAOMock,
	               applicationsServiceMock,
	               configurationServiceMock,
	               commentFactoryMock,
	               commentServiceMock,
	               pdfAttachmentInputSourceFactoryMock,
	               pdfDocumentBuilderMock,
	               refereeDAOMock,
	               userServiceMock,
	               userDAOMock, 
	               roleDAOMock,
	               encryptionUtilsMock,
	               HOST,
	               SERVICE_OFFER) {
	           @Override
	           protected RegisteredUser processRefereeAndGetAsUser(final Referee referee) {
	               return null;
	           }
	       };
	    
		RegisteredUser user = new RegisteredUserBuilder().id(1).build();
		String adminMails = SAMPLE_ADMIN1_EMAIL_ADDRESS+", "+SAMPLE_ADMIN2_EMAIL_ADDRESS;
		ApplicationForm form = getSampleApplicationForm();
		Referee referee = new RefereeBuilder().id(0).user(user).application(form).build();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("adminsEmails", adminMails);
		model.put("referee", referee);
		model.put("application", form);
		model.put("applicant", form.getApplicant());
		model.put("host", HOST);
		
		String subjectToReturn="REMINDER: "+SAMPLE_APPLICANT_NAME+" "+SAMPLE_APPLICANT_SURNAME+" "
		+"Application "+SAMPLE_APPLICATION_NUMBER+" for UCL "+SAMPLE_PROGRAM_TITLE+" - Reference Request";
		
		expect(refereeDAOMock.getRefereesDueAReminder()).andReturn(asList(referee));
		
		expect(mockMailSender.resolveSubject(REFEREE_REMINDER, SAMPLE_APPLICATION_NUMBER, SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME))
				.andReturn(subjectToReturn);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));

		refereeDAOMock.save(referee);
		
		replay(mockMailSender, refereeDAOMock);
		service.sendReferenceReminder();
		verify(mockMailSender, refereeDAOMock);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertEquals(1, message.getTo().size());
		assertEquals((Integer)1, message.getTo().get(0).getId());
		
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
		
		assertNotNull(referee.getLastNotified());
	}
	
    @Test
    public void shouldSendNewUserInvitation() {
        ApplicationForm form = getSampleApplicationForm();
        PendingRoleNotification roleNotification = new PendingRoleNotificationBuilder()
            .role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build())
            .addedByUser(form.getProgram().getAdministrators().get(0))
            .program(form.getProgram())
            .build();
        RegisteredUser user = new RegisteredUserBuilder().id(1)
            .pendingRoleNotifications(roleNotification)
            .build();
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("newUser", user);
        model.put("admin", form.getProgram().getAdministrators().get(0));
        model.put("program", form.getProgram());
        model.put("newRoles", "Administrator for MRes Security Science");
        model.put("host", HOST);

        expect(userDAOMock.getUsersWithPendingRoleNotifications()).andReturn(asList(user));
        String subjectToReturn = "Invitation to Join UCL Prism";
        expect(mockMailSender.resolveSubject(EmailTemplateName.NEW_USER_SUGGESTION, (Object[])null))
            .andReturn(subjectToReturn);
        
        Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
        mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
        
        userDAOMock.save(user);
        
        replay(userDAOMock, mockMailSender);
        service.sendNewUserInvitation();
        verify(userDAOMock, mockMailSender);
        
        PrismEmailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertNotNull(message.getCc());
        assertEquals(1, message.getTo().size());
        assertEquals(subjectToReturn, message.getSubjectCode());
        assertModelEquals(model, message.getModel());
        
        assertNotNull(roleNotification.getNotificationDate());

    }
	
	@Test
	public void shouldSendValidationRequestToRegistry() {
		Person person1 = new PersonBuilder().id(87)
		.email("person1@mail.com")
		.firstname("Ivo")
		.lastname("avido")
		.email("ivo.avido@mail.com")
		.build();
		Person person2 = new PersonBuilder().id(78)
		.email("person2@mail.com")
		.firstname("Ektor")
		.lastname("Baboden")
		.email("ektor.baboden@mail.com")
		.build();
		List<Person> registryUsers = asList(person1, person2);
		ApplicationForm form = getSampleApplicationForm();
		RegisteredUser currentUser = new RegisteredUserBuilder().id(15)
				.firstName("Ennio")
				.lastName("annio")
				.email("ennio.annio@mail.com")
				.build();
		form.setAdminRequestedRegistry(currentUser);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("application", form);
		model.put("sender", form.getAdminRequestedRegistry());
		model.put("host", HOST);
		model.put("recipients", "Ivo, Ektor");
		model.put("admissionsValidationServiceLevel", SERVICE_OFFER);
		
		expect(applicationsServiceMock.getApplicationsDueRegistryNotification()).andReturn(asList(form));
		expect(configurationServiceMock.getAllRegistryUsers()).andReturn(registryUsers);
		
		String subjectToReturn = "Application " + SAMPLE_APPLICATION_NUMBER + " for UCL " + SAMPLE_PROGRAM_TITLE
				+ " - Validation Request";
		expect(
				mockMailSender.resolveSubject(REGISTRY_VALIDATION_REQUEST, SAMPLE_APPLICATION_NUMBER,
						SAMPLE_PROGRAM_TITLE, SAMPLE_APPLICANT_NAME, SAMPLE_APPLICANT_SURNAME)).andReturn(subjectToReturn);

		byte[] document =new byte[] {'a', 'b', 'c'};
		expect(pdfDocumentBuilderMock.build(EasyMock.isA(PdfModelBuilder.class), eq(form))).andReturn(document);
		expect(pdfAttachmentInputSourceFactoryMock.getAttachmentDataSource(SAMPLE_APPLICATION_NUMBER+".pdf", document)).andReturn(null);
		
		Comment comment = new CommentBuilder().id(45).build();
		expect(commentFactoryMock.createComment(
				eq(form), eq(currentUser), isA(String.class), eq(CommentType.GENERIC), EasyMock.isNull(ApplicationFormStatus.class)))
				.andReturn(comment);
		
		Capture<PrismEmailMessage> messageCaptor = new Capture<PrismEmailMessage>();
		mockMailSender.sendEmail(and(isA(PrismEmailMessage.class), capture(messageCaptor)));
		
		applicationsServiceMock.save(form);
		commentServiceMock.save(comment);
		
		replay(mockMailSender, configurationServiceMock, commentServiceMock, applicationsServiceMock, pdfDocumentBuilderMock, commentFactoryMock);
		service.sendValidationRequestToRegistry();
		verify(mockMailSender, configurationServiceMock, commentServiceMock, applicationsServiceMock, pdfDocumentBuilderMock, commentFactoryMock);
		
		PrismEmailMessage message = messageCaptor.getValue();
		assertNotNull(message.getTo());
		assertNotNull(message.getCc());
		assertEquals(2, message.getTo().size());
		assertEquals(subjectToReturn, message.getSubjectCode());
		assertModelEquals(model, message.getModel());
		
		assertFalse(form.getRegistryUsersDueNotification());
	}
	
	@Test
	public void shouldScheduleRejectionConfirmationToAdministrator() {
		RegisteredUser supervisorUser1 = new RegisteredUserBuilder().build();
		RegisteredUser supervisorUser2 = new RegisteredUserBuilder().build();
		Supervisor supervisor1 = new SupervisorBuilder().id(1).isPrimary(true).user(supervisorUser1).build();
		Supervisor supervisor2 = new SupervisorBuilder().user(supervisorUser2).id(1).build();
		ApprovalRound round = new ApprovalRoundBuilder().supervisors(supervisor1, supervisor2).build();
		ApplicationForm form = getSampleApplicationForm();
		form.setLatestApprovalRound(round);
		
		expect(applicationFormDAOMock.getApplicationsDueRejectNotifications()).andReturn(asList(form));
		
		replay(applicationFormDAOMock);
		service.scheduleRejectionConfirmationToAdministrator();
		verify(applicationFormDAOMock);
		
		assertNotNull(form.getRejectNotificationDate());
		assertEquals(supervisorUser1.getDigestNotificationType(), DigestNotificationType.UPDATE_NOTIFICATION);
	}
	
	@Test
	public void shouldScheduleReviewSubmittedConfirmation() {
		ApplicationForm form = getSampleApplicationForm();
		RegisteredUser commentUser1 = new RegisteredUserBuilder().id(69).build();
		RegisteredUser commentUser2 = new RegisteredUserBuilder().id(70).build();
		ReviewComment comment1 = new ReviewCommentBuilder().id(1).user(commentUser1).application(form).build();
		ReviewComment comment2 = new ReviewCommentBuilder().user(commentUser2).application(form).id(2).build();
		
		expect(commentDAOMock.getReviewCommentsDueNotification()).andReturn(asList(comment1, comment2));
		
		
		
		replay(commentDAOMock);
		service.scheduleReviewSubmittedConfirmation();
		verify(commentDAOMock);
		
		assertTrue(comment1.isAdminsNotified());
		assertTrue(comment2.isAdminsNotified());
		
		List<RegisteredUser> admins = form.getProgram().getAdministrators();
        assertEquals(admins.get(0).getDigestNotificationType(),  DigestNotificationType.UPDATE_NOTIFICATION);
        assertEquals(admins.get(1).getDigestNotificationType(),  DigestNotificationType.UPDATE_NOTIFICATION);
        
	}
	
	@Ignore
	@Test
	public void shouldScheduleReviewEvaluationRequest() {
		RegisteredUser reviewerUser1 = new RegisteredUserBuilder().build();
		RegisteredUser reviewerUser2 = new RegisteredUserBuilder().build();
		Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).build();
		Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).id(1).build();
		
		expect(reviewerDAOMock.getReviewersDueNotification()).andReturn(asList(reviewer1, reviewer2));
		
		replay(reviewerDAOMock);
		service.scheduleReviewEvaluationRequest();
		verify(reviewerDAOMock);
		
		assertNotNull(reviewer1.getLastNotified());
		assertNotNull(reviewer2.getLastNotified());
		
		assertEquals(reviewerUser1.getDigestNotificationType(),  DigestNotificationType.TASK_NOTIFICATION);
        assertEquals(reviewerUser2.getDigestNotificationType(),  DigestNotificationType.TASK_NOTIFICATION);
	}
 
	@Test
	public void shouldScheduleReviewEvaluationReminderIfAllReviewersHaveProvidedFeedback() {
	    RegisteredUser admin = new RegisteredUserBuilder().build();
		RegisteredUser reviewerUser1 = new RegisteredUserBuilder().build();
		RegisteredUser reviewerUser2 = new RegisteredUserBuilder().build();
		Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).review(new ReviewComment()).build();
		Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).id(2).review(new ReviewComment()).build();
		
		Program program = new ProgramBuilder().administrators(admin).build();

		ApplicationForm form = getSampleApplicationForm();
		form.setProgram(program);
        form.setLatestReviewRound(new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build());
		
        NotificationRecord record = new NotificationRecordBuilder().id(1).notificationType(NotificationType.REVIEW_EVALUATION_REMINDER).build();
        record.setApplication(form);
        
        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.REVIEW_EVALUATION_REMINDER, ApplicationFormStatus.REVIEW)).andReturn(asList(form));
        applicationFormDAOMock.save(form);
        
		EasyMock.replay(applicationFormDAOMock);
		
		service.scheduleReviewEvaluationReminder();
		
		verify(applicationFormDAOMock);
		
		assertEquals(DigestNotificationType.TASK_REMINDER, admin.getDigestNotificationType());
	}
	
    @Test
    public void shouldNotScheduleReviewEvaluationReminderIfAllReviewersHaveProvidedFeedback() {
        RegisteredUser admin = new RegisteredUserBuilder().build();
        RegisteredUser reviewerUser1 = new RegisteredUserBuilder().build();
        RegisteredUser reviewerUser2 = new RegisteredUserBuilder().build();
        Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).review(new ReviewComment()).build();
        Reviewer reviewer2 = new ReviewerBuilder().user(reviewerUser2).id(2).build();

        Program program = new ProgramBuilder().administrators(admin).build();

        ApplicationForm form = getSampleApplicationForm();
        form.setProgram(program);
        form.setLatestReviewRound(new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build());

        NotificationRecord record = new NotificationRecordBuilder().id(1).notificationType(NotificationType.REVIEW_EVALUATION_REMINDER).build();
        record.setApplication(form);

        EasyMock.expect(applicationFormDAOMock.getApplicationsDueUserReminder(NotificationType.REVIEW_EVALUATION_REMINDER, ApplicationFormStatus.REVIEW)).andReturn(asList(form));
        
        EasyMock.replay(applicationFormDAOMock);

        service.scheduleReviewEvaluationReminder();

        verify(applicationFormDAOMock);

        assertEquals(DigestNotificationType.NONE, admin.getDigestNotificationType());
    }
	
	@Test
	public void shouldScheduleConfirmSupervisionReminder() {
		RegisteredUser supervisorUser1 = new RegisteredUserBuilder().build();
		RegisteredUser supervisorUser2 = new RegisteredUserBuilder().build();
		Supervisor supervisor1 = new SupervisorBuilder().id(1).user(supervisorUser1).build();
		Supervisor supervisor2 = new SupervisorBuilder().user(supervisorUser2).id(1).build();
		
		expect(supervisorDAOMock.getPrimarySupervisorsDueReminder()).andReturn(asList(supervisor1, supervisor2));
				
		replay(supervisorDAOMock);
		service.scheduleConfirmSupervisionReminder();
		verify(supervisorDAOMock);
		
		assertNotNull(supervisor1.getLastNotified());
		assertNotNull(supervisor2.getLastNotified());
		
		assertEquals(supervisorUser1.getDigestNotificationType(),  DigestNotificationType.TASK_REMINDER);
        assertEquals(supervisorUser2.getDigestNotificationType(),  DigestNotificationType.TASK_REMINDER);
	}
	
	@Test
	public void shouldScheduleApplicationUnderInterviewNotification() {
		ApplicationForm form = getSampleApplicationForm();
		
		expect(
				applicationFormDAOMock.
				getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION, ApplicationFormStatus.INTERVIEW))
				.andReturn(asList(form));

		applicationFormDAOMock.save(form);
		
		replay(applicationFormDAOMock);
		service.scheduleApplicationUnderInterviewNotification();
		verify(applicationFormDAOMock);
		
		assertEquals(form.getApplicant().getDigestNotificationType(),  DigestNotificationType.UPDATE_NOTIFICATION);
	}
	
	@Test
	public void shouldScheduleApplicationUnderInterviewNotification2() {
		ApplicationForm form = getSampleApplicationForm();
		NotificationRecord record = new NotificationRecordBuilder().notificationType(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION).build();
		form.setNotificationRecords(asList(record));
		
		EasyMock.expect(
				applicationFormDAOMock.
				getApplicationsDueNotificationForStateChangeEvent(NotificationType.APPLICANT_MOVED_TO_INTERVIEW_NOTIFICATION, ApplicationFormStatus.INTERVIEW))
				.andReturn(asList(form));
		
		applicationFormDAOMock.save(form);
		
		
		replay(applicationFormDAOMock);
		service.scheduleApplicationUnderInterviewNotification();
		verify(applicationFormDAOMock);
		
		assertEquals(form.getApplicant().getDigestNotificationType(),  DigestNotificationType.UPDATE_NOTIFICATION);
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
