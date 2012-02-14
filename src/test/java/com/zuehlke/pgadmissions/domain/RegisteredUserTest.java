package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RegisteredUserTest {

	@Test
	public void shouldReturnTrueIfUserIsInRole(){
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(), new RoleBuilder().authorityEnum(Authority.RECRUITER).toRole()).toUser();
		assertTrue(user.isInRole(Authority.APPLICANT));
		
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotInRole(){
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(), new RoleBuilder().authorityEnum(Authority.RECRUITER).toRole()).toUser();
		assertFalse(user.isInRole(Authority.REVIEWER));
		
	}
	
	@Test
	public void shouldReturnTrueIfUserIsInRolePassedAsString(){
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(), new RoleBuilder().authorityEnum(Authority.RECRUITER).toRole()).toUser();
		assertTrue(user.isInRole("APPLICANT"));
		
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotInRolePassedAsString(){
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(), new RoleBuilder().authorityEnum(Authority.RECRUITER).toRole()).toUser();
		assertFalse(user.isInRole("REVIEWER"));
		
	}
	
	@Test
	public void shouldReturnFalseIStringIsNotAuthorityValue(){
		RegisteredUser user = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole(), new RoleBuilder().authorityEnum(Authority.RECRUITER).toRole()).toUser();
		assertFalse(user.isInRole("bob"));
		
	}
	
	@Test
	public void shouldReturnTrueIfUserIsRecruiterOrReviewer(){
		RegisteredUser recruiter = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.RECRUITER).toRole()).toUser();
		RegisteredUser reviewer = new RegisteredUserBuilder().roles( new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		ApplicationForm applicationForm = new ApplicationForm();
		assertTrue(recruiter.canSee(applicationForm));
		assertTrue(reviewer.canSee(applicationForm));
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
	
}
