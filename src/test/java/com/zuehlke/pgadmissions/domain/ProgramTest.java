package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ProgramTest {

	@Test
	public void shouldCreateProgram() {
		Program program = new ProgramBuilder().id(1).administrators(new RegisteredUser()).approver(new RegisteredUser()).code("code")
				.reviewers(new RegisteredUser()).title("title").build();
		Assert.assertNotNull(program.getCode());
		Assert.assertNotNull(program.getTitle());
		Assert.assertNotNull(program.getAdministrators());
		Assert.assertNotNull(program.getApprovers());
		Assert.assertNotNull(program.getProgramReviewers());
		Assert.assertNotNull(program.getId());
	}

	@Test
	public void shouldReturnTrueIfUserIsApproverOfProgram() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).build()).build();
		Program program = new ProgramBuilder().id(1).approver(approver).build();
		assertTrue(program.isApprover(approver));
	}

	@Test
	public void shouldReturnFalseIfUserIsNotApproverOfProgram() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).build()).build();
		Program program = new ProgramBuilder().id(1).build();
		assertFalse(program.isApprover(approver));
	}

	@Test
	public void shouldReturnFalseIfUserIsNotApprover() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
		Program program = new ProgramBuilder().id(1).approver(approver).build();
		assertFalse(program.isApprover(approver));
	}

	@Test
	public void shouldReturnTrueIfUserIsAdminOfProgram() {
		RegisteredUser admin = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).build();
		Program program = new ProgramBuilder().id(1).administrators(admin).build();
		assertTrue(program.isAdministrator(admin));
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotAdminOfProgram() {
		RegisteredUser admin = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).build();
		Program program = new ProgramBuilder().id(1).build();
		assertFalse(program.isAdministrator(admin));
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotAdmin() {
		RegisteredUser admin = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
		Program program = new ProgramBuilder().id(1).administrators(admin).build();
		assertFalse(program.isAdministrator(admin));
	}
	
	@Test
	public void shouldReturnTrueIfUserInterviewerOfProgram(){
		RegisteredUser interviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).build()).build();
		Program program = new ProgramBuilder().id(1).interviewers(interviewer).build();
		assertTrue(program.isInterviewerOfProgram(interviewer));
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotInterviewerOfProgram(){
		RegisteredUser interviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).build()).build();
		Program program = new ProgramBuilder().id(1).build();
		assertFalse(program.isInterviewerOfProgram(interviewer));
	}
	
}
