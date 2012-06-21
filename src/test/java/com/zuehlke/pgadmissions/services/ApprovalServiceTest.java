package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApprovalRoundDAO;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.EventFactory;

public class ApprovalServiceTest {

	private ApprovalService approvalService;

	private ApplicationFormDAO applicationFormDAOMock;
	private ApprovalRoundDAO approvalRoundDAOMock;
	private StageDurationDAO stageDurationDAOMock;
	private MailService mailServiceMock;
	private EventFactory eventFactoryMock;
	private CommentFactory commentFactoryMock;
	private CommentDAO commentDAOMock;
	private EncryptionHelper encryptionHelperMock;
	private DocumentDAO documentDAOMock;
	private UserService userServiceMock;
	private SupervisorDAO supervisorDAOMock;
	private ApprovalRound approvalRound;
	private Supervisor supervisor;
	
	@Before
	public void setUp() {
		supervisor = new SupervisorBuilder().id(1).toSupervisor();
		approvalRound = new ApprovalRoundBuilder().id(1).toApprovalRound();
		supervisorDAOMock = EasyMock.createMock(SupervisorDAO.class);
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		approvalRoundDAOMock = EasyMock.createMock(ApprovalRoundDAO.class);
		stageDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
		mailServiceMock = EasyMock.createMock(MailService.class);
		eventFactoryMock = EasyMock.createMock(EventFactory.class);
		commentFactoryMock = EasyMock.createMock(CommentFactory.class);
		commentDAOMock = EasyMock.createMock(CommentDAO.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		documentDAOMock = EasyMock.createMock(DocumentDAO.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		approvalService = new ApprovalService(userServiceMock, applicationFormDAOMock, approvalRoundDAOMock, stageDurationDAOMock, mailServiceMock,
				eventFactoryMock, commentDAOMock, documentDAOMock, commentFactoryMock, encryptionHelperMock, supervisorDAOMock){
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
	public void shouldCreateNewInterviewerInNewInterviewRoundIfLatestRoundIsNull(){
		RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(new ProgramBuilder().id(1).toProgram()).applicant(new RegisteredUserBuilder().id(1).toUser()).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		supervisorDAOMock.save(supervisor);
		EasyMock.replay(supervisorDAOMock);
		approvalService.addSupervisorInPreviousReviewRound(application, supervisorUser);
		Assert.assertEquals(supervisorUser, supervisor.getUser());
		Assert.assertTrue(approvalRound.getSupervisors().contains(supervisor));
		
	}
	
	@Test
	public void shouldCreateNewInterviewerInLatestInterviewRoundIfLatestRoundIsNotNull(){
		RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).firstName("Maria").lastName("Doe").email("mari@test.com").username("mari").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		ApprovalRound latestApprovalRound = new ApprovalRoundBuilder().toApprovalRound();
		ApplicationForm application = new ApplicationFormBuilder().latestApprovalRound(latestApprovalRound).id(1).program(new ProgramBuilder().id(1).toProgram()).applicant(new RegisteredUserBuilder().id(1).toUser()).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		supervisorDAOMock.save(supervisor);
		EasyMock.replay(supervisorDAOMock);
		approvalService.addSupervisorInPreviousReviewRound(application, supervisorUser);
		Assert.assertEquals(supervisorUser, supervisor.getUser());
		Assert.assertTrue(latestApprovalRound.getSupervisors().contains(supervisor));
		
	}

	@Test
	public void shouldSetDueDateOnApplicationUpdateFormAndSaveBoth() {

		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).toApprovalRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).toApplicationForm();
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
				new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).toStageDuration());
		approvalRoundDAOMock.save(approvalRound);
		applicationFormDAOMock.save(applicationForm);

		StateChangeEvent event = new ApprovalStateChangeEventBuilder().id(1).toApprovalStateChangeEvent();
		EasyMock.expect(eventFactoryMock.createEvent(approvalRound)).andReturn(event);

		EasyMock.replay(approvalRoundDAOMock, applicationFormDAOMock, stageDurationDAOMock, eventFactoryMock);

		approvalService.moveApplicationToApproval(applicationForm, approvalRound);
		assertEquals(DateUtils.truncate(DateUtils.addDays(new Date(), 2), Calendar.DATE), DateUtils.truncate(applicationForm.getDueDate(), Calendar.DATE));
		assertEquals(applicationForm, approvalRound.getApplication());
		assertEquals(approvalRound, applicationForm.getLatestApprovalRound());
		assertEquals(ApplicationFormStatus.APPROVAL, applicationForm.getStatus());
		assertEquals(1, applicationForm.getEvents().size());
		assertEquals(event, applicationForm.getEvents().get(0));

		EasyMock.verify(approvalRoundDAOMock, applicationFormDAOMock);

	}

	@Test
	public void shouldMoveToApprovalIfInApproval() {

		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).toApprovalRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).id(1).toApplicationForm();
		EasyMock.expect(stageDurationDAOMock.getByStatus(ApplicationFormStatus.APPROVAL)).andReturn(
				new StageDurationBuilder().duration(2).unit(DurationUnitEnum.DAYS).toStageDuration());
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
				ApplicationForm application = new ApplicationFormBuilder().id(3).status(status).toApplicationForm();
				boolean threwException = false;
				try {
					approvalService.moveApplicationToApproval(application, new ApprovalRoundBuilder().id(1).toApprovalRound());
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
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(5).toApprovalRound();
		approvalRoundDAOMock.save(approvalRound);
		EasyMock.replay(approvalRoundDAOMock);
		approvalService.save(approvalRound);
		EasyMock.verify(approvalRoundDAOMock);
	}

	@Test
	public void shouldSendRequestRestartOfApprovalMailAndSaveAComment() {
		Program program = new ProgramBuilder().id(321).title("lala").toProgram();
		RegisteredUser approver = new RegisteredUserBuilder().id(2234).firstName("dada").lastName("dudu").username("dd@test.com")//
				.role(new RoleBuilder().id(2).authorityEnum(Authority.APPROVER).toRole())//
				.programsOfWhichApprover(program).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.APPROVAL).id(1).toApplicationForm();

		mailServiceMock.sendRequestRestartApproval(applicationForm, approver);
		EasyMock.expectLastCall();
		Comment comment = new CommentBuilder().id(1).toComment();
		EasyMock.expect(commentFactoryMock.createComment(applicationForm, approver, "Requested re-start of approval phase", CommentType.GENERIC, null))
				.andReturn(comment);
		commentDAOMock.save(comment);
		EasyMock.replay(mailServiceMock, commentFactoryMock, commentDAOMock);
		approvalService.requestApprovalRestart(applicationForm, approver);
		EasyMock.verify(mailServiceMock, commentDAOMock);
	}

	@Test
	public void throwExceptionWhenApplicationNotInApprovalWhenRequestRestartOfApprovalMail() {
		Program program = new ProgramBuilder().id(321).title("lala").toProgram();
		RegisteredUser approver = new RegisteredUserBuilder().id(2234).firstName("dada").lastName("dudu").username("dd@test.com")//
				.role(new RoleBuilder().id(2).authorityEnum(Authority.APPROVER).toRole())//
				.programsOfWhichApprover(program).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW)//
				.program(program).id(1).applicationNumber("DUDU").toApplicationForm();

		EasyMock.replay(mailServiceMock);
		try {
			approvalService.requestApprovalRestart(applicationForm, approver);
			Assert.fail("expected exception not thrown!");
		} catch (IllegalArgumentException iae) {
			Assert.assertEquals("Application DUDU is not in state APPROVAL!", iae.getMessage());
		}
		EasyMock.verify(mailServiceMock);
	}

	@Test
	public void throwExceptionWhenUserIsNotApprover() {
		Program program = new ProgramBuilder().id(321).title("lala").toProgram();
		RegisteredUser approver = new RegisteredUserBuilder().id(2234).firstName("dada").lastName("dudu").username("dd@test.com")//
				.role(new RoleBuilder().id(2).authorityEnum(Authority.REVIEWER).toRole())//
				.programsOfWhichApprover(program).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW)//
				.program(program).id(1).applicationNumber("DUDU").toApplicationForm();

		EasyMock.replay(mailServiceMock);
		try {
			approvalService.requestApprovalRestart(applicationForm, approver);
			Assert.fail("expected exception not thrown!");
		} catch (IllegalArgumentException iae) {
			Assert.assertEquals("User dd@test.com is not an approver!", iae.getMessage());
		}
		EasyMock.verify(mailServiceMock);
	}

	@Test
	public void throwExceptionWhenApproverIsNotInProgram() {
		Program program = new ProgramBuilder().id(321).title("lala").toProgram();
		RegisteredUser approver = new RegisteredUserBuilder().id(2234).firstName("dada").lastName("dudu").username("dd@test.com")//
				.role(new RoleBuilder().id(2).authorityEnum(Authority.APPROVER).toRole())//
				.toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW)//
				.program(program).id(1).applicationNumber("DUDU").toApplicationForm();

		EasyMock.replay(mailServiceMock);
		try {
			approvalService.requestApprovalRestart(applicationForm, approver);
			Assert.fail("expected exception not thrown!");
		} catch (IllegalArgumentException iae) {
			Assert.assertEquals("User dd@test.com is not an approver in program lala!", iae.getMessage());
		}
		EasyMock.verify(mailServiceMock);
	}

	@Test
	public void shouldMoveApplicationToApprovedWithComment() {
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser).anyTimes();
		EasyMock.replay(userServiceMock);
		String strComment = "bob";
		List<String> documentIds = Arrays.asList("abc", "def");
		EasyMock.expect(encryptionHelperMock.decryptToInteger("abc")).andReturn(1);
		EasyMock.expect(encryptionHelperMock.decryptToInteger("def")).andReturn(2);
		Document documentOne = new DocumentBuilder().id(1).toDocument();
		Document documentTwo = new DocumentBuilder().id(2).toDocument();
		EasyMock.expect(documentDAOMock.getDocumentbyId(1)).andReturn(documentOne);
		EasyMock.expect(documentDAOMock.getDocumentbyId(2)).andReturn(documentTwo);

		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).program(program).id(2).toApplicationForm();

		applicationFormDAOMock.save(application);

		StateChangeEvent event = new StateChangeEventBuilder().id(1).toEvent();
		EasyMock.expect(eventFactoryMock.createEvent(ApplicationFormStatus.APPROVED)).andReturn(event);

		StateChangeComment stateChangeComment = new StateChangeComment();
		stateChangeComment.setId(3);
		EasyMock.expect(commentFactoryMock.createComment(application, currentUser, strComment, CommentType.APPROVAL, ApplicationFormStatus.APPROVED))
				.andReturn(stateChangeComment);
		commentDAOMock.save(stateChangeComment);

		EasyMock.replay(applicationFormDAOMock, eventFactoryMock, encryptionHelperMock, commentFactoryMock, documentDAOMock, commentDAOMock);

		approvalService.moveToApproved(application, strComment, documentIds);

		EasyMock.verify(applicationFormDAOMock, commentDAOMock);
		assertEquals(ApplicationFormStatus.APPROVED, application.getStatus());
		assertEquals(currentUser, application.getApprover());

		assertEquals(1, application.getEvents().size());
		assertEquals(event, application.getEvents().get(0));

		assertEquals(2, stateChangeComment.getDocuments().size());
		assertTrue(stateChangeComment.getDocuments().containsAll(Arrays.asList(documentOne, documentTwo)));
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldFailOmMoveToApprovedIfApplicationNotInApproval() {


		ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.REJECTED).id(2).toApplicationForm();
		

		EasyMock.replay(applicationFormDAOMock, eventFactoryMock, encryptionHelperMock, commentFactoryMock, documentDAOMock, commentDAOMock);

		approvalService.moveToApproved(application, "non", null);

		EasyMock.verify(applicationFormDAOMock, commentDAOMock);
		
	}
}
