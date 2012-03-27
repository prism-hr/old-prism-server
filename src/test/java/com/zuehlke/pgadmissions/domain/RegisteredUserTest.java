package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class RegisteredUserTest {

	@Test
	public void shouldReturnTrueIfUserIsInRole(){
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(), new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		assertTrue(user.isInRole(Authority.APPLICANT));
		
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotInRole(){
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(), new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		assertFalse(user.isInRole(Authority.REVIEWER));
		
	}
	
	@Test
	public void shouldReturnTrueIfUserIsInRolePassedAsString(){
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(), new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		assertTrue(user.isInRole("APPLICANT"));
		
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotInRolePassedAsString(){
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(), new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		assertFalse(user.isInRole("REVIEWER"));
		
	}
	
	@Test
	public void shouldReturnFalseIStringIsNotAuthorityValue(){
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(), new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		assertFalse(user.isInRole("bob"));
		
	}
	
	@Test
	public void shouldReturnTrueIfUserIsApplicantAndOwnerOfForm(){
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();		
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).toApplicationForm();
		assertTrue(applicant.canSee(applicationForm));
		
	}
	
	@Test
	public void shouldReturnFalseIfUserIsApplicantAndNotOwnerOfForm(){
		RegisteredUser applicantOne = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();		
		RegisteredUser applicantTwo = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicantOne).toApplicationForm();
		assertFalse(applicantTwo.canSee(applicationForm));
		
	}

	@Test
	public void shouldReturnTrueIfUserIsAdministrator(){
		RegisteredUser administrator = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertTrue(administrator.canSee(applicationForm));
		
	}
	
	@Test
	public void shouldReturnTrueIfUserIsItsReviewer(){
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
		reviewers.add(reviewer);
		ApplicationForm applicationForm = new ApplicationFormBuilder().reviewers(reviewers).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertTrue(reviewer.canSee(applicationForm));
		
	}

	
	@Test
	public void shouldReturnFalseIfUserIsNotItsReviewer(){
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
	public void shouldReturnTrueIfUserIsItsApproverOfProgramToWhichApplicationProjectBelongs(){
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).approver(approver).toProgram();
		Project project = new ProjectBuilder().id(1).program(program).toProject();
		ApplicationForm applicationForm = new ApplicationFormBuilder().project(project).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertTrue(approver.canSee(applicationForm));
		
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotApproverOfProgramToWhichApplicationProjectBelongs(){
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		Project project = new ProjectBuilder().id(1).program(program).toProject();
		ApplicationForm applicationForm = new ApplicationFormBuilder().project(project).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		assertFalse(approver.canSee(applicationForm));
	}
	
	
	@Test
	public void shouldReturnListOfAuthoritiesForProgram(){
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program).programsOfWhichReviewer(program).toUser();
		List<Authority> authorities = user.getAuthoritiesForProgram(program);
		assertEquals(3, authorities.size());
		assertEquals(Authority.ADMINISTRATOR, authorities.get(0));
		assertEquals(Authority.REVIEWER, authorities.get(1));
		assertEquals(Authority.APPROVER, authorities.get(2));
	}
	
	@Test
	public void shouldReturnCommaSeparatedListOfAuthoritiesForProgram(){
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser user = new RegisteredUserBuilder().programsOfWhichAdministrator(program).programsOfWhichApprover(program).programsOfWhichReviewer(program).toUser();
		assertEquals("Administrator, Reviewer, Approver", user.getAuthoritiesForProgramAsString(program));
	
	}
	
	@Test
	public void shouldAddSuperAdminToReturnCommaSeparatedListIfSuperadmin(){
		Program program = new ProgramBuilder().id(1).toProgram();
		RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).programsOfWhichAdministrator(program).programsOfWhichApprover(program).programsOfWhichReviewer(program).toUser();
		assertEquals("Superadministrator, Administrator, Reviewer, Approver", user.getAuthoritiesForProgramAsString(program));
	
	}
	
}
