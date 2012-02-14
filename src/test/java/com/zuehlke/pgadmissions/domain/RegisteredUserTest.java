package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.*;

import org.junit.Test;

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
}
