package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.domain.enums.ValidationStage;

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
		ApplicationForm applicationForm = new ApplicationFormBuilder().referees(referee).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertTrue(refereeUser.canSee(applicationForm));

	}
	
	@Test
	public void shouldReturnTrueIfUserIsRefereeOfTheApplicationFormButHasDeclined() {
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).toRole()).toUser();
		Referee referee = new RefereeBuilder().id(1).user(refereeUser).declined(true).toReferee();
		ApplicationForm applicationForm = new ApplicationFormBuilder().referees(referee).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertFalse(refereeUser.canSee(applicationForm));

	}
	@Test
	public void shouldReturnFalseIfUserIsRefereeOfButNotOnApplicationForm() {
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REFEREE).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
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
	public void shouldReturnTrueIfUserIsAdministrator() {
		RegisteredUser administrator = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertTrue(administrator.canSee(applicationForm));

	}
	
	@Test
	public void shouldReturnTrueIfUserReviewerAndApplicationInNotValidateStage() {
		Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		reviewers.add(reviewer);
		ApplicationForm applicationForm = new ApplicationFormBuilder().reviewers(reviewers).submissionStatus(SubmissionStatus.SUBMITTED).validationStage(ValidationStage.FALSE).toApplicationForm();
		assertTrue(reviewer.canSee(applicationForm));
		
	}
	
	@Test
	public void shouldReturnFalseIfUserApproverAndApplicationInValidateStage() {
		RegisteredUser approver = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().approver(approver).submissionStatus(SubmissionStatus.SUBMITTED).validationStage(ValidationStage.TRUE).toApplicationForm();
		assertFalse(approver.canSee(applicationForm));
		
	}
	@Test
	public void shouldReturnTrueIfUserAdminAndApplicationInValidateStage() {
		RegisteredUser administrator = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).validationStage(ValidationStage.TRUE).toApplicationForm();
		assertTrue(administrator.canSee(applicationForm));
		
	}

	@Test
	public void shouldReturnTrueIfUserIsItsReviewer() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
		reviewers.add(reviewer);
		ApplicationForm applicationForm = new ApplicationFormBuilder().reviewers(reviewers).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertTrue(reviewer.canSee(applicationForm));

	}

	@Test
	public void shouldReturnFalseIfUserIsNotItsReviewer() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().toApplicationForm();
		assertFalse(reviewer.canSee(applicationForm));

	}

	@Test
	public void shouldReturnFalseForAnyoneNotAnApplicantIfUnsubmittedApplication() {
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
		reviewers.add(reviewer);
		ApplicationForm applicationForm = new ApplicationFormBuilder().reviewers(reviewers).toApplicationForm();
		assertFalse(reviewer.canSee(applicationForm));
	}

	@Test
	public void shouldReturnTrueForAnApplicantIfUnsubmittedApplication() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(applicant).toApplicationForm();
		assertTrue(applicant.canSee(applicationForm));
	}

	@Test
	public void shouldReturnTrueIfUserIsItsApproverOfProgramToWhichApplicationBelongs() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).approver(approver).toProgram();		
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertTrue(approver.canSee(applicationForm));

	}

	@Test
	public void shouldReturnFalseIfUserIsNotApproverOfProgramToWhichApplicationBelongs() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
	
		ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertFalse(approver.canSee(applicationForm));
	}

	@Test
	public void shouldReturnListOfAuthoritiesForProgram() {
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program)
				.programsOfWhichReviewer(program).toUser();
		List<Authority> authorities = user.getAuthoritiesForProgram(program);
		assertEquals(3, authorities.size());
		assertEquals(Authority.ADMINISTRATOR, authorities.get(0));
		assertEquals(Authority.REVIEWER, authorities.get(1));
		assertEquals(Authority.APPROVER, authorities.get(2));
	}

	@Test
	public void shouldReturnCommaSeparatedListOfAuthoritiesForProgram() {
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program)
				.programsOfWhichReviewer(program).toUser();
		assertEquals("Administrator, Reviewer, Approver", user.getAuthoritiesForProgramAsString(program));

	}

	@Test
	public void shouldAddSuperAdminToReturnCommaSeparatedListIfSuperadmin() {
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole())
				.programsOfWhichAdministrator(program).programsOfWhichApprover(program).programsOfWhichReviewer(program).toUser();
		assertEquals("Superadministrator, Administrator, Reviewer, Approver", user.getAuthoritiesForProgramAsString(program));

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
		assertFalse(user.canSeeReference(new ReferenceBuilder().id(1).toReference()));
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
		Reference reference = new ReferenceBuilder().id(1).referee(referee).toReference();
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
		Reference reference = new ReferenceBuilder().id(1).referee(referee).toReference();
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
		Reference reference = new ReferenceBuilder().id(1).referee(referee).toReference();
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
		Reference reference = new ReferenceBuilder().id(1).referee(referee).toReference();
		assertTrue(user.canSeeReference(reference));
	}
	
	@Test
	public void shouldReturnRefereeForApplicationForm(){
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();	
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(7).toApplicationForm();
		Referee refereeOne = new RefereeBuilder().id(7).user(user).application(applicationForm).toReferee();
		Referee refereeTwo = new RefereeBuilder().id(8).user(new RegisteredUserBuilder().id(9).toUser()).application(new ApplicationFormBuilder().id(78).toApplicationForm()).toReferee();
		user.setReferees(Arrays.asList(refereeOne, refereeTwo));
		Referee referee = user.getRefereeForApplicationForm(applicationForm);
		assertEquals(refereeOne, referee);
	}
	
	@Test
	public void shouldReturnNullIfNotRefereeForApplicationForm(){
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();	
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(7).toApplicationForm();
		assertNull(user.getRefereeForApplicationForm(applicationForm));
	}
	
	@Test
	public void shouldReturnNullIfDeclinedToRefereeForApplicationForm(){
		RegisteredUser user = new RegisteredUserBuilder().id(8).toUser();	
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(7).toApplicationForm();
		Referee refereeOne = new RefereeBuilder().id(7).user(user).declined(true).application(applicationForm).toReferee();
		Referee refereeTwo = new RefereeBuilder().id(8).user(new RegisteredUserBuilder().id(9).toUser()).application(new ApplicationFormBuilder().id(78).toApplicationForm()).toReferee();
		user.setReferees(Arrays.asList(refereeOne, refereeTwo));
		assertNull(user.getRefereeForApplicationForm(applicationForm));
	}
}
