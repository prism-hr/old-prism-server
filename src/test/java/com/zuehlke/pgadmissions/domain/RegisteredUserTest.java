package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class RegisteredUserTest {

	@Test
	public void shouldReturnTrueIfUserIsInRole() {
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(),
				new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		assertTrue(user.isInRole(Authority.APPLICANT));

	}

	@Test
	public void shouldReturnFalseIfUserIsNotInRole() {
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(),
				new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		assertFalse(user.isInRole(Authority.REVIEWER));

	}

	@Test
	public void shouldReturnTrueIfUserIsInRolePassedAsString() {
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(),
				new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		assertTrue(user.isInRole("APPLICANT"));

	}

	@Test
	public void shouldReturnFalseIfUserIsNotInRolePassedAsString() {
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(),
				new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		assertFalse(user.isInRole("REVIEWER"));

	}

	@Test
	public void shouldReturnFalseIStringIsNotAuthorityValue() {
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(),
				new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		assertFalse(user.isInRole("bob"));

	}

	@Test
	public void shouldReturnTrueIfUserIsApplicantAndOwnerOfForm() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).toApplicationForm();
		assertTrue(applicant.canSee(applicationForm));

	}

	@Test
	public void shouldReturnTrueIfUserIsRefereeOfTheApplicationForm() {
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).toRole()).toUser();
		Referee referee = new RefereeBuilder().id(1).user(refereeUser).toReferee();
		ApplicationForm applicationForm = new ApplicationFormBuilder().referees(referee).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertTrue(refereeUser.canSee(applicationForm));

	}

	@Test
	public void shouldReturnTrueIfUserIsRefereeOfTheApplicationFormButHasDeclined() {
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).toRole()).toUser();
		Referee referee = new RefereeBuilder().id(1).user(refereeUser).declined(true).toReferee();
		ApplicationForm applicationForm = new ApplicationFormBuilder().referees(referee).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(refereeUser.canSee(applicationForm));

	}

	@Test
	public void shouldReturnFalseIfUserIsRefereeOfButNotOnApplicationForm() {
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(refereeUser.canSee(applicationForm));

	}

	@Test
	public void shouldReturnFalseIfUserIsApplicantAndNotOwnerOfForm() {
		RegisteredUser applicantOne = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicantTwo = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicantOne).toApplicationForm();
		assertFalse(applicantTwo.canSee(applicationForm));

	}

	@Test
	public void shouldReturnTrueIfUserIsSuperAdministrator() {
		RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertTrue(administrator.canSee(applicationForm));

	}

	@Test
	public void shouldReturnTrueIfUserInterviewerAndApplicationInInterviewStage() {

		RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).toUser();
		Interview interview = new InterviewBuilder().id(1).interviewers(new InterviewerBuilder().user(interviewerUser).toInterviewer()).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().interviews(interview).latestInterview(interview).status(ApplicationFormStatus.INTERVIEW)
				.toApplicationForm();
		assertTrue(interviewerUser.canSee(applicationForm));

	}

	@Test
	public void shouldNotFailIfLatestInterviewIsNull() {

		RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).toApplicationForm();
		assertFalse(interviewerUser.canSee(applicationForm));

	}

	@Test
	public void shouldReturnTrueIfUserReviewerAndApplicationInReviewStage() {

		RegisteredUser revieweruser = new RegisteredUserBuilder().id(1).toUser();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(revieweruser).toReviewer()).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(reviewRound).status(ApplicationFormStatus.REVIEW).toApplicationForm();
		assertTrue(revieweruser.canSee(applicationForm));

	}

	@Test
	public void shouldReturnFalseIfUserApproverAndApplicationInValidateStage() {
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser approver = new RegisteredUserBuilder().id(1).programsOfWhichApprover(program)
				.roles(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).approver(approver).status(ApplicationFormStatus.VALIDATION)
				.toApplicationForm();
		assertFalse(approver.canSee(applicationForm));

	}

	@Test
	public void shouldReturnTrueIfUserAdminInProgramOfApplication() {
		RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION)
				.program(new ProgramBuilder().administrators(administrator).toProgram()).toApplicationForm();
		assertTrue(administrator.canSee(applicationForm));

	}

	@Test
	public void shouldReturnFalseIfUserAdminInProgramOfApplicationNotSubmitted() {
		RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED)
				.program(new ProgramBuilder().administrators(administrator).toProgram()).toApplicationForm();
		assertFalse(administrator.canSee(applicationForm));

	}

	@Test
	public void shouldReturnTrueIfApplicationAdmin() {
		RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicationAdministrator(administrator)
				.toApplicationForm();
		assertTrue(administrator.canSee(applicationForm));

	}

	@Test
	public void shouldReturnFalseIfUserAdminOfApplicationNotSubmitted() {
		RegisteredUser administrator = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.UNSUBMITTED).applicationAdministrator(administrator)
				.toApplicationForm();
		assertFalse(administrator.canSee(applicationForm));

	}

	@Test
	public void shouldReturnTrueIfUserIsReviewerOfForm() {
		RegisteredUser revieweruser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(revieweruser).toReviewer()).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(reviewRound).status(ApplicationFormStatus.REVIEW).toApplicationForm();
		assertTrue(revieweruser.canSee(applicationForm));

	}

	@Test
	public void shouldReturnFalseIfUserIsNotItsReviewer() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().toApplicationForm();
		assertFalse(reviewer.canSee(applicationForm));

	}

	@Test
	public void shouldReturnFalseForAnyoneNotAnApplicantIfUnsubmittedApplication() {
		RegisteredUser revieweruser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(revieweruser).toReviewer()).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(reviewRound).toApplicationForm();
		assertFalse(revieweruser.canSee(applicationForm));
	}

	@Test
	public void shouldReturnFalseIfUserIsNotItsInterviewer() {
		RegisteredUser interviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().toApplicationForm();
		assertFalse(interviewer.canSee(applicationForm));

	}

	@Test
	public void shouldReturnTrueForAnApplicantIfUnsubmittedApplication() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicant(applicant).toApplicationForm();
		assertTrue(applicant.canSee(applicationForm));
	}

	@Test
	public void shouldReturnTrueIfUserIsItsApproverOfProgramToWhichApplicationBelongsAndApplicatioIsInApproval() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).approver(approver).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(approver.canSee(applicationForm));
		applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		assertTrue(approver.canSee(applicationForm));

	}

	@Test
	public void shouldReturnFalseIfUserIsNotApproverOfProgramToWhichApplicationBelongs() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(approver.canSee(applicationForm));
	}

	@Test
	public void shouldReturnTrueIfUserIsReviewerTApplication() {
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).toReviewer()).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(reviewRound).status(ApplicationFormStatus.VALIDATION)
				.toApplicationForm();
		assertTrue(reviewerUser.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnFalseIfLatestReviewRoundIsNull() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();

		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(reviewer.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnFalseIfUserIsReviewerButNotInApplication() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();

		ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(new ReviewRoundBuilder().toReviewRound()).program(program)
				.status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(reviewer.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnTrueInfUserIsReviewerOfReviewRound() {
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).toReviewer()).toReviewRound();
		assertTrue(reviewerUser.isReviewerInReviewRound(reviewRound));
	}

	@Test
	public void shouldReturnFalseInfUserIsNotReviewerOfReviewRound() {
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ReviewRound reviewRound = new ReviewRoundBuilder().toReviewRound();
		assertFalse(reviewerUser.isReviewerInReviewRound(reviewRound));
	}

	@Test
	public void shouldReturnFalseIfReviewRoundIsNull() {
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		assertFalse(reviewerUser.isReviewerInReviewRound(null));
	}

	@Test
	public void shouldReturnTrueInfUserIsInterviewerOfInterview() {
		RegisteredUser interviwerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		Interview interview = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviwerUser).toInterviewer()).toInterview();
		assertTrue(interviwerUser.isInterviewerInInterview(interview));
	}

	@Test
	public void shouldReturnFalseInfUserIsNotInterviewerOfInterview() {
		RegisteredUser interviwerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		Interview interview = new InterviewBuilder().toInterview();
		assertFalse(interviwerUser.isInterviewerInInterview(interview));
	}

	@Test
	public void shouldReturnFalseIfInterviewIsNull() {
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		assertFalse(reviewerUser.isInterviewerInInterview(null));
	}

	@Test
	public void shouldReturnTrueInfUserIsSupvisorOfApprovalRound() {
		RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.SUPERVISOR).toRole()).toUser();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(new SupervisorBuilder().user(supervisorUser).toSupervisor()).toApprovalRound();
		assertTrue(supervisorUser.isSupervisorInApprovalRound(approvalRound));
	}

	@Test
	public void shouldReturnFalseInfUserIsNotSupvisorOfApprovalRound() {
		RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.SUPERVISOR).toRole()).toUser();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().toApprovalRound();
		assertFalse(supervisorUser.isSupervisorInApprovalRound(approvalRound));
	}

	@Test
	public void shouldReturnFalseIfApprovalRoundIsNull() {
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.SUPERVISOR).toRole()).toUser();
		assertFalse(reviewerUser.isSupervisorInApprovalRound(null));
	}

	@Test
	public void shouldReturnFalseIfUserIsApproverInApplication() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().latestReviewRound(new ReviewRoundBuilder().toReviewRound()).approver(approver)
				.status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(approver.isReviewerInLatestReviewRoundOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnTrueIfUserIsPastOrPresentReviewerOfApplication() {
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).toReviewer()).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().reviewRounds(reviewRound).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertTrue(reviewerUser.isPastOrPresentReviewerOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnFalseIfUserIsNeitherPastOrPresentReviewerOfApplication() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(approver.isPastOrPresentReviewerOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnFalseForInterviewersIfUserIsApproverInApplication() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();

		ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(new Interview()).approver(approver).program(program)
				.status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(approver.isInterviewerOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnFalseForInterviewersIfUserIsReviewerButNotInApplication() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();

		ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(new Interview()).program(program)
				.status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(reviewer.isInterviewerOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnTrueForInterviewersIfUserIsInterviewerTApplication() {
		RegisteredUser interviewerUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		Interview interview = new InterviewBuilder().id(1).interviewers(new InterviewerBuilder().user(interviewerUser).toInterviewer()).toInterview();

		ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(interview).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertTrue(interviewerUser.isInterviewerOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnFalseForSupervisorsIfUserIsApproverInApplication() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();

		ApplicationForm applicationForm = new ApplicationFormBuilder().latestApprovalRound(new ApprovalRound()).approver(approver).program(program)
				.status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(approver.isSupervisorOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnFalseForSupervisorsIfUserIsReviewerButNotInApplication() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();

		ApplicationForm applicationForm = new ApplicationFormBuilder().latestApprovalRound(new ApprovalRound()).program(program)
				.status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(reviewer.isSupervisorOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnTrueForSupervisorsIfUserIsSupervisorTApplication() {
		RegisteredUser supervisorUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.SUPERVISOR).toRole()).toUser();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(1).supervisors(new SupervisorBuilder().user(supervisorUser).toSupervisor())
				.toApprovalRound();

		ApplicationForm applicationForm = new ApplicationFormBuilder().latestApprovalRound(approvalRound).status(ApplicationFormStatus.VALIDATION)
				.toApplicationForm();
		assertTrue(supervisorUser.isSupervisorOfApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnFalseForInterviewersIfUserIsInterviewerButNotInProgram() {
		RegisteredUser interviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		assertFalse(interviewer.isInterviewerOfProgram(program));
	}

	@Test
	public void shouldReturnTrueForInterviewersIfUserIsInterviewerOfProgram() {
		RegisteredUser interviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Program program = new ProgramBuilder().interviewers(interviewer).id(1).toProgram();
		assertTrue(interviewer.isInterviewerOfProgram(program));
	}

	@Test
	public void shouldReturnListOfAuthoritiesForProgram() {
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program)
				.programsOfWhichReviewer(program).programsOfWhichSupervisor(program).toUser();
		List<Authority> authorities = user.getAuthoritiesForProgram(program);
		assertEquals(4, authorities.size());
		assertEquals(Authority.ADMINISTRATOR, authorities.get(0));
		assertEquals(Authority.REVIEWER, authorities.get(1));
		assertEquals(Authority.APPROVER, authorities.get(2));
		assertEquals(Authority.SUPERVISOR, authorities.get(3));
	}

	@Test
	public void shouldReturnCommaSeparatedListOfAuthoritiesForProgram() {
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program)
				.programsOfWhichReviewer(program).programsOfWhichInterviewer(program).programsOfWhichSupervisor(program).toUser();
		assertEquals("Administrator, Reviewer, Interviewer, Approver, Supervisor", user.getAuthoritiesForProgramAsString(program));

	}

	@Test
	public void shouldAddSuperAdminToReturnCommaSeparatedListIfSuperadmin() {
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole())
				.programsOfWhichAdministrator(program).programsOfWhichApprover(program).programsOfWhichSupervisor(program).programsOfWhichReviewer(program)
				.programsOfWhichInterviewer(program).toUser();
		assertEquals("Superadministrator, Administrator, Reviewer, Interviewer, Approver, Supervisor", user.getAuthoritiesForProgramAsString(program));

	}

	@Test
	public void shouldReturnTrueIfUserHasRoleForProgram() {
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser user1 = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole())
				.programsOfWhichApprover(program).toUser();
		assertFalse(user1.isInRoleInProgram(Authority.ADMINISTRATOR, program));
		RegisteredUser user2 = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole())
				.programsOfWhichAdministrator(program).toUser();
		assertTrue(user2.isInRoleInProgram(Authority.ADMINISTRATOR, program));
	}

	@Test
	public void shouldReturnTrueForSuperadmins() {
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
		assertTrue(user.isInRoleInProgram(Authority.SUPERADMINISTRATOR, program));
	}

	@Test
	public void shouldReturnTrueIfUserIsAdminAndBelongsToAProgramme() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).administrators(user).toProgram();
		assertTrue(user.isAdminOrReviewerInProgramme(program));
	}

	@Test
	public void shouldReturnTrueIfUserIsReviewerAndBelongsToAProgramme() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).reviewers(user).toProgram();
		assertTrue(user.isAdminOrReviewerInProgramme(program));
	}

	@Test
	public void shouldReturnFalseIfUserIsReviewerButDoesNotBelongToTheProgramme() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		assertFalse(user.isAdminOrReviewerInProgramme(program));
	}

	@Test
	public void shouldReturnTrueIfUserIsAdminAndOfAProgramme() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).administrators(user).toProgram();
		assertTrue(user.isAdminInProgramme(program));
	}

	@Test
	public void shouldReturnFalseIfUserDoesNotBelongToTheProgramme() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		assertFalse(user.isAdminInProgramme(program));
	}

	@Test
	public void shouldReturnTrueIfHasRefereesInApplicationForm() {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee1 = new RefereeBuilder().id(1).firstname("ref").lastname("erre").email("emailemail1@test.com").application(form).toReferee();
		Referee referee2 = new RefereeBuilder().id(2).firstname("ref").lastname("erre").email("emailemail2@test.com").toReferee();
		Referee referee3 = new RefereeBuilder().id(3).firstname("ref").lastname("erre").email("emailemail3@test.com").toReferee();

		RegisteredUser user = new RegisteredUserBuilder().id(1).referees(referee1, referee2, referee3)
				.role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();

		assertTrue(user.hasRefereesInApplicationForm(form));
	}

	@Test
	public void shouldReturnFalseIfDoesntHaveRefereesInApplicationForm() {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee1 = new RefereeBuilder().id(1).firstname("ref").lastname("erre").email("emailemail1@test.com").toReferee();
		Referee referee2 = new RefereeBuilder().id(2).firstname("ref").lastname("erre").email("emailemail2@test.com").toReferee();
		Referee referee3 = new RefereeBuilder().id(3).firstname("ref").lastname("erre").email("emailemail3@test.com").toReferee();

		RegisteredUser user = new RegisteredUserBuilder().id(1).referees(referee1, referee2, referee3)
				.role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();

		assertFalse(user.hasRefereesInApplicationForm(form));
	}

	@Test
	public void shouldReturnFalseIfUserIsApplicant() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		assertFalse(user.canSeeReference(new ReferenceCommentBuilder().id(1).toReferenceComment()));
	}

	@Test
	public void shouldReturnFalseIfUserCannotSeeApplicationForReference() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		@SuppressWarnings("serial")
		RegisteredUser user = new RegisteredUser() {
			@Override
			public boolean canSee(ApplicationForm application) {
				return false;
			}
		};

		Referee referee = new RefereeBuilder().id(1).application(applicationForm).toReferee();
		ReferenceComment reference = new ReferenceCommentBuilder().id(1).referee(referee).toReferenceComment();
		assertFalse(user.canSeeReference(reference));
	}

	@Test
	public void shouldReturnTrueIfUserCanSeeFormAndIsNotReferee() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		@SuppressWarnings("serial")
		RegisteredUser user = new RegisteredUser() {
			@Override
			public boolean canSee(ApplicationForm application) {
				return true;
			}

			@Override
			public boolean isRefereeOfApplicationForm(ApplicationForm form) {
				return false;
			}
		};

		Referee referee = new RefereeBuilder().id(1).application(applicationForm).toReferee();
		ReferenceComment reference = new ReferenceCommentBuilder().id(1).referee(referee).toReferenceComment();
		assertTrue(user.canSeeReference(reference));
	}

	@Test
	public void shouldReturnFalseIfUserIsRefereeAndNotUploadingReferee() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		@SuppressWarnings("serial")
		RegisteredUser user = new RegisteredUser() {
			@Override
			public boolean isRefereeOfApplicationForm(ApplicationForm form) {
				return true;
			}

		};

		Referee referee = new RefereeBuilder().id(1).application(applicationForm).user(new RegisteredUserBuilder().id(8).toUser()).toReferee();
		ReferenceComment reference = new ReferenceCommentBuilder().id(1).referee(referee).toReferenceComment();
		assertFalse(user.canSeeReference(reference));
	}

	@Test
	public void shouldReturnTrueIfUserIsRefereeAndUploadingReferee() {
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		@SuppressWarnings("serial")
		RegisteredUser user = new RegisteredUser() {
			@Override
			public boolean isRefereeOfApplicationForm(ApplicationForm form) {
				return true;
			}

			@Override
			public boolean canSee(ApplicationForm application) {
				return true;
			}

		};
		user.setId(1);
		Referee referee = new RefereeBuilder().id(1).application(applicationForm).user(user).toReferee();
		ReferenceComment reference = new ReferenceCommentBuilder().id(1).referee(referee).toReferenceComment();
		assertTrue(user.canSeeReference(reference));
	}

	@Test
	public void shouldReturnNullIfDeclinedToRefereeForApplicationForm() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(7).toApplicationForm();
		Referee refereeOne = new RefereeBuilder().id(7).user(user).declined(true).application(applicationForm).toReferee();
		Referee refereeTwo = new RefereeBuilder().id(8).user(new RegisteredUserBuilder().id(9).toUser())
				.application(new ApplicationFormBuilder().id(78).toApplicationForm()).toReferee();
		user.setReferees(Arrays.asList(refereeOne, refereeTwo));
		assertNull(user.getRefereeForApplicationForm(applicationForm));
	}

	@Test
	public void shouldReturnTrueIfUserIsReviewerOfApplicationAndHasDeclinedToProvideReview() {

		RegisteredUser applicant = new RegisteredUserBuilder().id(3).username("applicantemail").firstName("bob").lastName("bobson").email("email@test.com")
				.toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(applicant).id(1).toApplicationForm();

		Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").toComment();
		ReviewComment reviewComment = new ReviewCommentBuilder().application(application).id(2).decline(true).comment("This is a review comment")
				.commentType(CommentType.REVIEW).toReviewComment();
		Comment comment1 = new CommentBuilder().id(1).application(application).comment("This is another generic Comment").toComment();

		RegisteredUser reviewer = new RegisteredUserBuilder().programsOfWhichReviewer(program).comments(comment1, comment, reviewComment)
				.roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).username("email").firstName("bob").lastName("bobson")
				.email("email@test.com").toUser();
		assertTrue(reviewer.hasDeclinedToProvideReviewForApplication(application));
	}

	@Test
	public void shouldReturnFalseIfUserIsReviewerOfApplicationButHasNotDeclinedToProvideReview() {

		RegisteredUser applicant = new RegisteredUserBuilder().id(3).username("applicantemail").firstName("bob").lastName("bobson").email("email@test.com")
				.toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(applicant).id(1).toApplicationForm();

		Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").toComment();
		ReviewComment reviewComment = new ReviewCommentBuilder().application(application).id(2).decline(false).comment("This is a review comment")
				.commentType(CommentType.REVIEW).toReviewComment();
		Comment comment1 = new CommentBuilder().id(1).application(application).comment("This is another generic Comment").toComment();

		RegisteredUser reviewer = new RegisteredUserBuilder().programsOfWhichReviewer(program).comments(comment1, comment, reviewComment)
				.roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).username("email").firstName("bob").lastName("bobson")
				.email("email@test.com").toUser();
		assertFalse(reviewer.hasDeclinedToProvideReviewForApplication(application));
	}

	@Test
	public void shouldReturnTrueIfUserIsReviewerOfApplicationAndHasProvidedReview() {

		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();

		Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").toComment();
		ReviewComment reviewComment = new ReviewCommentBuilder().application(application).id(2).decline(false).comment("This is a review comment")
				.commentType(CommentType.REVIEW).toReviewComment();
		Comment comment1 = new CommentBuilder().id(3).application(application).comment("This is another generic Comment").toComment();

		RegisteredUser reviewer = new RegisteredUserBuilder().programsOfWhichReviewer(program).comments(comment1, comment, reviewComment)
				.roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		assertTrue(reviewer.hasRespondedToProvideReviewForApplication(application));
	}

	@Test
	public void shouldReturnFalseIfUserIsReviewerOfApplicationButHasNotProvidedReview() {

		RegisteredUser applicant = new RegisteredUserBuilder().id(3).username("applicantemail").firstName("bob").lastName("bobson").email("email@test.com")
				.toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(applicant).id(1).toApplicationForm();

		Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").toComment();
		Comment comment1 = new CommentBuilder().id(3).application(application).comment("This is another generic Comment").toComment();

		RegisteredUser reviewer = new RegisteredUserBuilder().programsOfWhichReviewer(program).comments(comment1, comment)
				.roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).username("email").firstName("bob").lastName("bobson")
				.email("email@test.com").toUser();
		assertFalse(reviewer.hasRespondedToProvideReviewForApplication(application));
	}

	@Test
	public void shouldReturnFalseIfUserIsReviewerButNotForThisInApplication() {

		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application1 = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		ApplicationForm application2 = new ApplicationFormBuilder().program(program).id(2).toApplicationForm();

		Comment comment = new CommentBuilder().id(1).application(application1).comment("This is a generic Comment").toComment();
		ReviewComment reviewComment = new ReviewCommentBuilder().application(application2).id(2).decline(false).comment("This is a review comment")
				.commentType(CommentType.REVIEW).toReviewComment();
		Comment comment1 = new CommentBuilder().id(3).application(application1).comment("This is another generic Comment").toComment();

		RegisteredUser reviewer = new RegisteredUserBuilder().programsOfWhichReviewer(program).comments(comment1, comment, reviewComment)
				.roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		assertFalse(reviewer.hasRespondedToProvideReviewForApplication(application1));
	}

	@Test
	public void shouldReturnTrueIfUserIsInterviewerOfApplicationAndHasProvidedInterview() {

		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();

		Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").toComment();
		InterviewComment interviewComment = new InterviewCommentBuilder().application(application).id(2).decline(false).comment("This is an interview comment")
				.commentType(CommentType.INTERVIEW).toInterviewComment();
		Comment comment1 = new CommentBuilder().id(3).application(application).comment("This is another generic Comment").toComment();

		RegisteredUser interviewer = new RegisteredUserBuilder().programsOfWhichReviewer(program).comments(comment1, comment, interviewComment)
				.roles(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		assertTrue(interviewer.hasRespondedToProvideInterviewFeedbackForApplication(application));
	}

	@Test
	public void shouldReturnFalseIfInterviewerUserRespondedInPreviousRoundsButNotInLatest() {
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(1).toUser();
		Interviewer interviewer1 = new InterviewerBuilder().interview(new InterviewBuilder().id(1).toInterview()).id(1).user(interviewerUser1).toInterviewer();
		Interviewer interviewer2 = new InterviewerBuilder().id(1).user(interviewerUser1).toInterviewer();
		Interview interview1 = new InterviewBuilder().id(1).interviewers(interviewer1).toInterview();
		Interview interview2 = new InterviewBuilder().id(1).interviewers(interviewer2).toInterview();
		interviewer1.setInterview(interview1);
		interviewer2.setInterview(interview2);

		application.setInterviews(Arrays.asList(interview1, interview2));
		application.setLatestInterview(interview2);
		assertFalse(interviewerUser1.hasRespondedToProvideInterviewFeedbackForApplication(application));

	}

	@Test
	public void shouldReturnTrueIfInterviewerUserRespondedInLatestRound() {
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		RegisteredUser interviewerUser1 = new RegisteredUserBuilder().id(1).toUser();
		Interviewer interviewer1 = new InterviewerBuilder().id(1).user(interviewerUser1).toInterviewer();
		Interviewer interviewer2 = new InterviewerBuilder().interview(new InterviewBuilder().id(2).toInterview()).id(1).user(interviewerUser1).toInterviewer();
		Interview reviewRound1 = new InterviewBuilder().id(1).interviewers(interviewer1).toInterview();
		Interview reviewRound2 = new InterviewBuilder().id(1).interviewers(interviewer2).toInterview();
		interviewer1.setInterview(reviewRound1);

		interviewer2.setInterview(reviewRound2);
		interviewer2.setInterviewComment(new InterviewCommentBuilder().id(2).toInterviewComment());

		application.setInterviews(Arrays.asList(reviewRound1, reviewRound2));
		application.setLatestInterview(reviewRound2);
		assertTrue(interviewerUser1.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(application));
	}

	@Test
	public void shouldReturnFalseIfReviewerUserRespondedInPreviousRoundsButNotInLatest() {
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(1).toUser();
		Reviewer reviewer1 = new ReviewerBuilder().review(new ReviewCommentBuilder().id(1).toReviewComment()).id(1).user(reviewerUser1).toReviewer();
		Reviewer reviewer2 = new ReviewerBuilder().id(1).user(reviewerUser1).toReviewer();
		ReviewRound reviewRound1 = new ReviewRoundBuilder().id(1).reviewers(reviewer1).toReviewRound();
		ReviewRound reviewRound2 = new ReviewRoundBuilder().id(1).reviewers(reviewer2).toReviewRound();
		reviewer1.setReviewRound(reviewRound1);
		reviewer2.setReviewRound(reviewRound2);

		application.setReviewRounds(Arrays.asList(reviewRound1, reviewRound2));
		application.setLatestReviewRound(reviewRound2);
		assertFalse(reviewerUser1.hasRespondedToProvideReviewForApplicationLatestRound(application));

	}

	@Test
	public void shouldReturnFalseIfReviewerUserDidNotRespondInAnyRound() {
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(1).toUser();
		Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).toReviewer();
		Reviewer reviewer2 = new ReviewerBuilder().id(1).user(reviewerUser1).toReviewer();
		ReviewRound reviewRound1 = new ReviewRoundBuilder().id(1).reviewers(reviewer1).toReviewRound();
		ReviewRound reviewRound2 = new ReviewRoundBuilder().id(1).reviewers(reviewer2).toReviewRound();
		reviewer1.setReviewRound(reviewRound1);
		reviewer2.setReviewRound(reviewRound2);

		application.setReviewRounds(Arrays.asList(reviewRound1, reviewRound2));
		application.setLatestReviewRound(reviewRound2);
		assertFalse(reviewerUser1.hasRespondedToProvideReviewForApplicationLatestRound(application));

	}

	@Test
	public void shouldReturnTrueIfReviewerUserRespondedInLatestRoundAndInPrevious() {
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(1).toUser();
		Reviewer reviewer1 = new ReviewerBuilder().review(new ReviewCommentBuilder().id(1).toReviewComment()).id(1).user(reviewerUser1).toReviewer();
		Reviewer reviewer2 = new ReviewerBuilder().review(new ReviewCommentBuilder().id(2).toReviewComment()).id(1).user(reviewerUser1).toReviewer();
		ReviewRound reviewRound1 = new ReviewRoundBuilder().id(1).reviewers(reviewer1).toReviewRound();
		ReviewRound reviewRound2 = new ReviewRoundBuilder().id(1).reviewers(reviewer2).toReviewRound();
		reviewer1.setReviewRound(reviewRound1);
		reviewer2.setReviewRound(reviewRound2);

		application.setReviewRounds(Arrays.asList(reviewRound1, reviewRound2));
		application.setLatestReviewRound(reviewRound2);
		assertTrue(reviewerUser1.hasRespondedToProvideReviewForApplicationLatestRound(application));

	}

	@Test
	public void shouldReturnTrueIfReviewerUserRespondedInLatestRound() {
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		RegisteredUser reviewerUser1 = new RegisteredUserBuilder().id(1).toUser();
		Reviewer reviewer1 = new ReviewerBuilder().id(1).user(reviewerUser1).toReviewer();
		Reviewer reviewer2 = new ReviewerBuilder().review(new ReviewCommentBuilder().id(2).toReviewComment()).id(1).user(reviewerUser1).toReviewer();
		ReviewRound reviewRound1 = new ReviewRoundBuilder().id(1).reviewers(reviewer1).toReviewRound();
		ReviewRound reviewRound2 = new ReviewRoundBuilder().id(1).reviewers(reviewer2).toReviewRound();
		reviewer1.setReviewRound(reviewRound1);
		reviewer2.setReviewRound(reviewRound2);

		application.setReviewRounds(Arrays.asList(reviewRound1, reviewRound2));
		application.setLatestReviewRound(reviewRound2);
		assertTrue(reviewerUser1.hasRespondedToProvideReviewForApplicationLatestRound(application));

	}

	@Test
	public void shouldReturnFalseIfUserIsInterviewerOfApplicationButHasNotProvidedInterviewFeedback() {

		RegisteredUser applicant = new RegisteredUserBuilder().id(3).username("applicantemail").firstName("bob").lastName("bobson").email("email@test.com")
				.toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(applicant).id(1).toApplicationForm();

		Comment comment = new CommentBuilder().id(1).application(application).comment("This is a generic Comment").toComment();
		Comment comment1 = new CommentBuilder().id(3).application(application).comment("This is another generic Comment").toComment();

		RegisteredUser interviewer = new RegisteredUserBuilder().programsOfWhichInterviewer(program).comments(comment1, comment)
				.roles(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).username("email").firstName("bob").lastName("bobson")
				.email("email@test.com").toUser();
		assertFalse(interviewer.hasRespondedToProvideInterviewFeedbackForApplication(application));
	}

	@Test
	public void shouldReturnFalseIfUserIsInterviewerButNotForThisInApplication() {

		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm application1 = new ApplicationFormBuilder().program(program).id(1).toApplicationForm();
		ApplicationForm application2 = new ApplicationFormBuilder().program(program).id(2).toApplicationForm();

		Comment comment = new CommentBuilder().id(1).application(application1).comment("This is a generic Comment").toComment();
		InterviewComment interviewComment = new InterviewCommentBuilder().application(application2).id(2).decline(false)
				.comment("This is an interview comment").commentType(CommentType.INTERVIEW).toInterviewComment();
		Comment comment1 = new CommentBuilder().id(3).application(application1).comment("This is another generic Comment").toComment();

		RegisteredUser interviewer = new RegisteredUserBuilder().programsOfWhichReviewer(program).comments(comment1, comment, interviewComment)
				.roles(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		assertFalse(interviewer.hasRespondedToProvideInterviewFeedbackForApplication(application1));
	}

	@Test
	public void shouldReturnReviewersForApplicationForm() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(7).toApplicationForm();
		Reviewer reviewerOne = new ReviewerBuilder().id(7).user(user).toReviewer();
		Reviewer reviewerTwo = new ReviewerBuilder().id(8).user(new RegisteredUserBuilder().id(9).toUser()).toReviewer();
		Reviewer reviewerThree = new ReviewerBuilder().id(9).user(user).toReviewer();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewerOne, reviewerTwo, reviewerThree).toReviewRound();

		applicationForm.setLatestReviewRound(reviewRound);
		List<Reviewer> reviewers = user.getReviewersForApplicationForm(applicationForm);
		assertEquals(2, reviewers.size());
		assertTrue(reviewers.containsAll(Arrays.asList(reviewerOne, reviewerThree)));
	}

	@Test
	public void shouldReturnEmptyListfNotReviewerForApplicationForm() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(7).toApplicationForm();
		assertTrue(user.getReviewersForApplicationForm(applicationForm).isEmpty());
	}

	@Test
	public void shouldReturnInterviewersForApplicationForm() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(new Interview()).id(7).toApplicationForm();
		Interviewer interviewerOne = new InterviewerBuilder().id(7).user(user).toInterviewer();
		Interviewer interviewerTwo = new InterviewerBuilder().id(8).user(new RegisteredUserBuilder().id(9).toUser()).toInterviewer();
		Interviewer interviewerThree = new InterviewerBuilder().id(9).user(user).toInterviewer();

		applicationForm.getLatestInterview().getInterviewers().addAll(Arrays.asList(interviewerOne, interviewerTwo, interviewerThree));
		List<Interviewer> interviewers = user.getInterviewersForApplicationForm(applicationForm);
		assertEquals(2, interviewers.size());
		assertTrue(interviewers.containsAll(Arrays.asList(interviewerOne, interviewerThree)));
	}

	@Test
	public void shouldReturnEmptyListfNotInterviewerForApplicationForm() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().latestInterview(new Interview()).id(7).toApplicationForm();
		assertTrue(user.getInterviewersForApplicationForm(applicationForm).isEmpty());
	}

	@Test
	public void shouldHaveAdminRightsOnAppIfAdministratorInApplicationProgram() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().administrators(user).toProgram())
				.status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertTrue(user.hasAdminRightsOnApplication(applicationForm));

	}

	@Test
	public void shouldHaveAdminRightsOnAppIfAdministratorOfApplication() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicationAdministrator(user).status(ApplicationFormStatus.VALIDATION)
				.toApplicationForm();
		assertTrue(user.hasAdminRightsOnApplication(applicationForm));
	}

	@Test
	public void shouldHaveAdminRightsOnAppIfSuperadmin() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertTrue(user.hasAdminRightsOnApplication(applicationForm));
	}

	@Test
	public void shouldNotHaveAdminRightsOnAppNeihterAdministratorOfProgramOrApplication() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(new Program()).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertFalse(user.hasAdminRightsOnApplication(applicationForm));
	}

	@Test
	public void shouldNotHaveAdminRightsOnUnsubmitteApplication() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicationAdministrator(user).status(ApplicationFormStatus.UNSUBMITTED)
				.toApplicationForm();
		assertFalse(user.hasAdminRightsOnApplication(applicationForm));
	}

	@Test
	public void shouldNotHaveStaffRigstOnlyIfInRefereeRoleOnly() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().toProgram()).status(ApplicationFormStatus.VALIDATION)
				.referees(new RefereeBuilder().user(user).toReferee()).toApplicationForm();
		assertFalse(user.hasStaffRightsOnApplicationForm(applicationForm));

	}

	@Test
	public void shouldNotHaveStaffRigstOnlyIfApplicant() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().toProgram()).status(ApplicationFormStatus.VALIDATION)
				.applicant(user).toApplicationForm();
		assertFalse(user.hasStaffRightsOnApplicationForm(applicationForm));

	}

	@Test
	public void shouldHaveStaffRigstIfAdmin() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().administrators(user).toProgram())
				.status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

	}

	@Test
	public void shouldHaveStaffRigstIfReviewer() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Reviewer reviewer = new ReviewerBuilder().id(1).user(user).toReviewer();
		ReviewRound reviewRound = new ReviewRoundBuilder().id(4).reviewers(reviewer).toReviewRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().toProgram()).status(ApplicationFormStatus.VALIDATION)
				.latestReviewRound(reviewRound).toApplicationForm();
		assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

	}

	@Test
	public void shouldHaveStaffRigstIfInterviweer() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		Interviewer interviewer = new InterviewerBuilder().id(1).user(user).toInterviewer();
		Interview interview = new InterviewBuilder().id(4).interviewers(interviewer).toInterview();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().toProgram()).status(ApplicationFormStatus.VALIDATION)
				.latestInterview(interview).toApplicationForm();
		assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

	}
	

	@Test
	public void shouldHaveStaffRigstIfSuperviros() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		Supervisor supervisor = new SupervisorBuilder().id(1).user(user).toSupervisor();
		ApprovalRound approvalRound = new ApprovalRoundBuilder().id(4).supervisors(supervisor).toApprovalRound();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().toProgram()).status(ApplicationFormStatus.VALIDATION)
				.latestApprovalRound(approvalRound).toApplicationForm();
		assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

	}
	@Test
	public void shouldHaveStaffRigstIfApprover() {
		Program program = new ProgramBuilder().id(4).toProgram();
		RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).programsOfWhichApprover(program).toUser();
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program)
				.status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

	}
	@Test
	public void shouldHaveStaffRigstIfSuperadming() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().toProgram())
				.status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

	}
	@Test
	public void shouldHaveStaffRigstIfReviewerAndReferee() {
		RegisteredUser user = new RegisteredUserBuilder().id(8).roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole(), new RoleBuilder().authorityEnum(Authority.REFEREE).toRole()).toUser();
		Reviewer reviewer = new ReviewerBuilder().id(1).user(user).toReviewer();
		ReviewRound reviewRound = new ReviewRoundBuilder().id(4).reviewers(reviewer).toReviewRound();
		Referee referee = new RefereeBuilder().id(7).user(user).toReferee();
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(new ProgramBuilder().toProgram()).status(ApplicationFormStatus.VALIDATION)
				.latestReviewRound(reviewRound).referees(referee).toApplicationForm();
		assertTrue(user.hasStaffRightsOnApplicationForm(applicationForm));

	}
}
