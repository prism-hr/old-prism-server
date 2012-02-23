package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
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
		ApplicationForm applicationForm = new ApplicationFormBuilder().registeredUser(applicant).toApplicationForm();
		assertTrue(applicant.canSee(applicationForm));
		
	}
	
	@Test
	public void shouldReturnFalseIfUserIsApplicantAndNotOwnerOfForm(){
		RegisteredUser applicantOne = new RegisteredUserBuilder().id(1).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();		
		RegisteredUser applicantTwo = new RegisteredUserBuilder().id(2).roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().registeredUser(applicantOne).toApplicationForm();
		assertFalse(applicantTwo.canSee(applicationForm));
		
	}

	@Test
	public void shouldReturnTrueIfUserIsAdministrator(){
		RegisteredUser administrator = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
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
	public void shouldReturnTrueIfUserIsItsApprover(){
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).approver(approver).toApplicationForm();
		assertTrue(approver.canSee(applicationForm));
		
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotItsReviewer(){
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().toApplicationForm();
		assertFalse(reviewer.canSee(applicationForm));
		
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotItsApprover(){
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().toApplicationForm();
		assertFalse(approver.canSee(applicationForm));
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
		ApplicationForm applicationForm = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.UNSUBMITTED).registeredUser(applicant).toApplicationForm();
		assertTrue(applicant.canSee(applicationForm));
	}
}
