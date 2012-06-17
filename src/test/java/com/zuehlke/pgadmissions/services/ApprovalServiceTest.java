package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

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
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
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

	

	@Before
	public void setUp() {
		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		approvalRoundDAOMock = EasyMock.createMock(ApprovalRoundDAO.class);
		stageDurationDAOMock = EasyMock.createMock(StageDurationDAO.class);
		mailServiceMock = EasyMock.createMock(MailService.class);
		eventFactoryMock = EasyMock.createMock(EventFactory.class);
		commentFactoryMock = EasyMock.createMock(CommentFactory.class);
		commentDAOMock = EasyMock.createMock(CommentDAO.class);
		approvalService = new ApprovalService(applicationFormDAOMock, approvalRoundDAOMock, stageDurationDAOMock, mailServiceMock,eventFactoryMock, commentDAOMock, commentFactoryMock );
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
			if (status != ApplicationFormStatus.VALIDATION && status != ApplicationFormStatus.APPROVAL
					&& status != ApplicationFormStatus.REVIEW
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
	public void shouldSaveReviewRound(){
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
		EasyMock.expect(commentFactoryMock.createComment(applicationForm, approver, "Requested re-start of approval phase", CommentType.GENERIC, null)).andReturn(comment);
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
}
