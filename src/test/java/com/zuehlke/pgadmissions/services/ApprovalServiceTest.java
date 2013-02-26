package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApprovalRoundDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.services.exporters.UclExportService;
import com.zuehlke.pgadmissions.utils.EventFactory;

public class ApprovalServiceTest {

	private ApprovalService approvalService;

	private ApplicationFormDAO applicationFormDAOMock;

	private ApprovalRoundDAO approvalRoundDAOMock;
	
	private StageDurationDAO stageDurationDAOMock;
	
	private ProgrammeDetailDAO programmeDetailDAOMock;

	private EventFactory eventFactoryMock;

	private CommentDAO commentDAOMock;

	private UserService userServiceMock;
	
	private SupervisorDAO supervisorDAOMock;
	
	private ApprovalRound approvalRound;
	
	private Supervisor supervisor;

	private UclExportService uclExportServiceMock;

	@Before
	public void setUp() {
		supervisor = new SupervisorBuilder().id(1).build();
		approvalRound = new ApprovalRoundBuilder().id(1).build();
		supervisorDAOMock = EasyMock.createMock(SupervisorDAO.class);
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		approvalRoundDAOMock = EasyMock.createMock(ApprovalRoundDAO.class);
		stageDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
		programmeDetailDAOMock = EasyMock.createMock(ProgrammeDetailDAO.class);
		eventFactoryMock = EasyMock.createMock(EventFactory.class);
		uclExportServiceMock = EasyMock.createMock(UclExportService.class);
		commentDAOMock = EasyMock.createMock(CommentDAO.class);
		userServiceMock = EasyMock.createMock(UserService.class);
        
		approvalService = new ApprovalService(userServiceMock, applicationFormDAOMock, approvalRoundDAOMock,
                stageDurationDAOMock, eventFactoryMock, commentDAOMock, supervisorDAOMock, programmeDetailDAOMock,
                uclExportServiceMock) {
            @Override
			public ApprovalRound newApprovalRound() {
				return approvalRound;
			}

			@Override
			public Supervisor newSupervisor() {
				return supervisor;
			}
		};
	}

	@Test
	public void shouldCreateNewSupervisorInNeApprovalRoundIfLatestRoundIsNull() {
		RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		ApplicationForm application = new ApplicationFormBuilder().id(1).program(new ProgramBuilder().id(1).build())
				.applicant(new RegisteredUserBuilder().id(1).build()).status(ApplicationFormStatus.VALIDATION).build();

		supervisorDAOMock.save(supervisor);
		EasyMock.replay(supervisorDAOMock);

		approvalService.addSupervisorInPreviousApprovalRound(application, supervisorUser);

		Assert.assertEquals(supervisorUser, supervisor.getUser());
		Assert.assertTrue(approvalRound.getSupervisors().contains(supervisor));

	}

	@Test
	public void shouldCreateNewSueprvisorInLatestAppprovalRoundIfLatestRoundIsNotNull() {
		RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().build();

		ApplicationForm application = new ApplicationFormBuilder().latestApprovalRound(latestApprovalRound).id(1)
				.program(new ProgramBuilder().id(1).build()).applicant(new RegisteredUserBuilder().id(1).build()).status(ApplicationFormStatus.VALIDATION)
				.build();

		supervisorDAOMock.save(supervisor);
		EasyMock.replay(supervisorDAOMock);
		approvalService.addSupervisorInPreviousApprovalRound(application, supervisorUser);
		Assert.assertEquals(supervisorUser, supervisor.getUser());
		Assert.assertTrue(latestApprovalRound.getSupervisors().contains(supervisor));

	}

	@Test
	public void shouldSetDueDateOnApplicationUpdateFormAndSaveBoth() {

		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).pendingApprovalRestart(true)
				.build();
		applicationForm.addNotificationRecord(
				new NotificationRecordBuilder().id(2).notificationType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION).build());
		applicationForm.addNotificationRecord(
				new NotificationRecordBuilder().id(5).notificationType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER).build());
		applicationForm.addNotificationRecord(
				new NotificationRecordBuilder().id(4).notificationType(NotificationType.APPROVAL_NOTIFICATION).build());
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
				new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
		approvalRoundDAOMock.save(approvalRound);
		applicationFormDAOMock.save(applicationForm);

		StateChangeEvent event = new ApprovalStateChangeEventBuilder().id(1).build();
		EasyMock.expect(eventFactoryMock.createEvent(approvalRound)).andReturn(event);

		EasyMock.replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, eventFactoryMock);

		approvalService.moveApplicationToApproval(applicationForm, approvalRound);
		
		assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 2), Calendar.DATE), DateUtils.truncate(applicationForm.getDueDate(), Calendar.DATE));
		assertEquals(applicationForm, approvalRound.getApplication());
		assertEquals(approvalRound, applicationForm.getLatestApprovalRound());
		assertEquals(ApplicationFormStatus.APPROVAL, applicationForm.getStatus());
		assertEquals(1, applicationForm.getEvents().size());
		assertEquals(event, applicationForm.getEvents().get(0));
		assertFalse(applicationForm.isPendingApprovalRestart());
		EasyMock.verify(approvalRoundDAOMock, applicationFormDAOMock);
		assertNull(applicationForm.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_NOTIFICATION));
		assertNull(applicationForm.getNotificationForType(NotificationType.APPROVAL_RESTART_REQUEST_REMINDER));
		assertNull(applicationForm.getNotificationForType(NotificationType.APPROVAL_NOTIFICATION));

	}



	@Test
	public void shouldCopyLastNotifiedForSupervisorsWhoWereAlsoInPreviousRound() throws ParseException {
		Date lastNotified = new SimpleDateFormat("dd MM yyyy").parse("05 06 2012");
		RegisteredUser repeatUser = new RegisteredUserBuilder().id(1).build();
		Supervisor repeatSupervisorOld = new SupervisorBuilder().id(1).user(repeatUser).lastNotified(lastNotified).build();
		Supervisor repeatSupervisorNew = new SupervisorBuilder().id(2).user(repeatUser).build();
		
		RegisteredUser nonRepeatUser = new RegisteredUserBuilder().id(2).build();
		Supervisor nonRepeatUserSupervisor = new SupervisorBuilder().id(3).user(nonRepeatUser).build();
		ApprovalRound previousApprovalRound = new ApprovalRoundBuilder().id(1).supervisors(repeatSupervisorOld).build();
		
		ApprovalRound newApprovalRound = new ApprovalRoundBuilder().id(2).supervisors(repeatSupervisorNew, nonRepeatUserSupervisor).build();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().latestApprovalRound(previousApprovalRound).status(ApplicationFormStatus.APPROVAL).id(1).build();
		
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
				new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
		approvalRoundDAOMock.save(newApprovalRound);
		applicationFormDAOMock.save(applicationForm);
		StateChangeEvent event = new ApprovalStateChangeEventBuilder().id(1).build();
		EasyMock.expect(eventFactoryMock.createEvent(newApprovalRound)).andReturn(event);
		EasyMock.replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, eventFactoryMock);

		approvalService.moveApplicationToApproval(applicationForm, newApprovalRound);
		EasyMock.verify(approvalRoundDAOMock, applicationFormDAOMock);
		assertNull(nonRepeatUserSupervisor.getLastNotified());
		assertEquals(lastNotified, repeatSupervisorNew.getLastNotified());
	
	}

	@Test
	public void shouldMoveToApprovalIfInApproval() {

		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).id(1).build();
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
				new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).build());
		approvalRoundDAOMock.save(approvalRound);
		applicationFormDAOMock.save(applicationForm);
		EasyMock.replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock);
		approvalService.moveApplicationToApproval(applicationForm, approvalRound);
		EasyMock.verify(approvalRoundDAOMock, applicationFormDAOMock);

	}

	@Test
	public void shouldFailIfApplicationInInvalidState() {
		ApplicationFormStatus[] values = ApplicationFormStatus.values();
		for (ApplicationFormStatus status : values) {
			if (status != ApplicationFormStatus.VALIDATION && status != ApplicationFormStatus.APPROVAL && status != ApplicationFormStatus.REVIEW
					&& status != ApplicationFormStatus.INTERVIEW) {
				ApplicationForm application = new ApplicationFormBuilder().id(3).status(status).build();
				boolean threwException = false;
				try {
					approvalService.moveApplicationToApproval(application, new ApprovalRoundBuilder().id(1).build());
				} catch (IllegalStateException ise) {
					if (ise.getMessage().equals("Application in invalid status: '" + status + "'!")) {
						threwException = true;
					}
				}
				Assert.assertTrue(threwException);
			}
		}
	}

	@Test
	public void shouldSaveReviewRound() {
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).build();
		approvalRoundDAOMock.save(approvalRound);
		EasyMock.replay(approvalRoundDAOMock);
		approvalService.save(approvalRound);
		EasyMock.verify(approvalRoundDAOMock);
	}

	@Test
	public void shouldSaveRequestRestardComment() {
		Program program = new ProgramBuilder().id(321).title("lala").build();
		RegisteredUser approver = new RegisteredUserBuilder().id(2234).firstName("dada").lastName("dudu").username("dd@test.com")//
				.role(new RoleBuilder().id(2).authorityEnum(Authority.APPROVER).build())//
				.programsOfWhichApprover(program).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.APPROVAL).id(1).build();

		Comment comment = new CommentBuilder().id(1).build();
		commentDAOMock.save(comment);
		applicationFormDAOMock.save(applicationForm);
		EasyMock.replay(commentDAOMock, applicationFormDAOMock);
		approvalService.requestApprovalRestart(applicationForm, approver, comment);
		EasyMock.verify(commentDAOMock, applicationFormDAOMock);
		assertTrue(applicationForm.isPendingApprovalRestart());
		assertEquals(approver, applicationForm.getApproverRequestedRestart());
	}

	@Test
	public void throwExceptionWhenApplicationNotInApprovalWhenRequestRestartOfApprovalMail() {
		Program program = new ProgramBuilder().id(321).title("lala").build();
		RegisteredUser approver = new RegisteredUserBuilder().id(2234).firstName("dada").lastName("dudu").username("dd@test.com")//
				.role(new RoleBuilder().id(2).authorityEnum(Authority.APPROVER).build())//
				.programsOfWhichApprover(program).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW)//
				.program(program).id(1).applicationNumber("DUDU").build();
		Comment comment = new CommentBuilder().id(1).build();

		try {
			approvalService.requestApprovalRestart(applicationForm, approver, comment);
			Assert.fail("expected exception not thrown!");
		} catch (IllegalArgumentException iae) {
			Assert.assertEquals("Application DUDU is not in state APPROVAL!", iae.getMessage());
		}

	}

	@Test
	public void throwExceptionWhenUserIsNotApprover() {
		Program program = new ProgramBuilder().id(321).title("lala").build();
		RegisteredUser approver = new RegisteredUserBuilder().id(2234).firstName("dada").lastName("dudu").username("dd@test.com")//
				.role(new RoleBuilder().id(2).authorityEnum(Authority.REVIEWER).build())//
				.programsOfWhichApprover(program).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW)//
				.program(program).id(1).applicationNumber("DUDU").build();
		Comment comment = new CommentBuilder().id(1).build();

		try {
			approvalService.requestApprovalRestart(applicationForm, approver, comment);
			Assert.fail("expected exception not thrown!");
		} catch (IllegalArgumentException iae) {
			Assert.assertEquals("User dd@test.com is not an approver!", iae.getMessage());
		}

	}

	@Test
	public void throwExceptionWhenApproverIsNotInProgram() {
		Program program = new ProgramBuilder().id(321).title("lala").build();
		RegisteredUser approver = new RegisteredUserBuilder().id(2234).firstName("dada").lastName("dudu").username("dd@test.com")//
				.role(new RoleBuilder().id(2).authorityEnum(Authority.APPROVER).build())//
				.build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW)//
				.program(program).id(1).applicationNumber("DUDU").build();
		Comment comment = new CommentBuilder().id(1).build();

		try {
			approvalService.requestApprovalRestart(applicationForm, approver, comment);
			Assert.fail("expected exception not thrown!");
		} catch (IllegalArgumentException iae) {
			Assert.assertEquals("User dd@test.com is not an approver in program lala!", iae.getMessage());
		}

	}

	@Test
	public void shouldMoveApplicationToApprovedWithComment() {
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);

		Date startDate = new Date();
		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
		ProgramInstance instance = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate,1)).enabled(true).studyOption("1", "full").build();
		Program program = new ProgramBuilder().id(1).instances(instance).enabled(true).build();
		ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).program(program).id(2).programmeDetails(programmeDetails).build();

		applicationFormDAOMock.save(application);

		StateChangeEvent event = new StateChangeEventBuilder().id(1).build();
		EasyMock.expect(eventFactoryMock.createEvent(ApplicationFormStatus.APPROVED)).andReturn(event);

		EasyMock.replay(applicationFormDAOMock, eventFactoryMock, commentDAOMock);

		approvalService.moveToApproved(application);

		EasyMock.verify(applicationFormDAOMock, commentDAOMock);
		assertEquals(ApplicationFormStatus.APPROVED, application.getStatus());
		assertEquals(currentUser, application.getApprover());

		assertEquals(1, application.getEvents().size());
		assertEquals(event, application.getEvents().get(0));
	}
	
	@Test
	public void shouldChangeStartDate() {
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).build();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);

		Date startDate = DateUtils.addDays(new Date(), 1);
		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
		ProgramInstance instanceDisabled = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate, 4)).enabled(false).studyOption("1", "full").build();
		ProgramInstance instanceEnabled = new ProgramInstanceBuilder().applicationStartDate(DateUtils.addDays(startDate, 3)).applicationDeadline(DateUtils.addDays(startDate, 4)).enabled(true).studyOption("1", "full").build();
		Program program = new ProgramBuilder().id(1).enabled(true).instances(instanceDisabled, instanceEnabled).build();
		ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).program(program).id(2).programmeDetails(programmeDetails).build();

		programmeDetailDAOMock.save(EasyMock.same(programmeDetails));
		applicationFormDAOMock.save(application);

		StateChangeEvent event = new StateChangeEventBuilder().id(1).build();
		EasyMock.expect(eventFactoryMock.createEvent(ApplicationFormStatus.APPROVED)).andReturn(event);

		EasyMock.replay(applicationFormDAOMock, eventFactoryMock, commentDAOMock, programmeDetailDAOMock);

		approvalService.moveToApproved(application);

		EasyMock.verify(applicationFormDAOMock, commentDAOMock, programmeDetailDAOMock);
		assertEquals(ApplicationFormStatus.APPROVED, application.getStatus());
		assertEquals(currentUser, application.getApprover());
		assertEquals(programmeDetails.getStartDate(), instanceEnabled.getApplicationStartDate());

		assertEquals(1, application.getEvents().size());
		assertEquals(event, application.getEvents().get(0));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailOmMoveToApprovedIfApplicationNotInApproval() {

		ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).id(2).build();

		EasyMock.replay(applicationFormDAOMock, eventFactoryMock, commentDAOMock);

		approvalService.moveToApproved(application);

		EasyMock.verify(applicationFormDAOMock, commentDAOMock);

	}
}
