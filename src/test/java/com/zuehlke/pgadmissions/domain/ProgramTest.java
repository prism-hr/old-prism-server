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
				.reviewers(new RegisteredUser()).title("title").toProgram();
		Assert.assertNotNull(program.getCode());
		Assert.assertNotNull(program.getTitle());
		Assert.assertNotNull(program.getAdministrators());
		Assert.assertNotNull(program.getApprovers());
		Assert.assertNotNull(program.getProgramReviewers());
		Assert.assertNotNull(program.getId());
	}

	@Test
	public void shouldReturnTrueIfUserIsApproverOfProgram() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).approver(approver).toProgram();
		assertTrue(program.isApprover(approver));
	}

	@Test
	public void shouldReturnFalseIfUserIsNotApproverOfProgram() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		assertFalse(program.isApprover(approver));
	}

	@Test
	public void shouldReturnFalseIfUserIsNotApprover() {
		RegisteredUser approver = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).approver(approver).toProgram();
		assertFalse(program.isApprover(approver));
	}

	@Test
	public void shouldReturnTrueIfUserIsAdminOfProgram() {
		RegisteredUser admin = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).administrators(admin).toProgram();
		assertTrue(program.isAdministrator(admin));
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotAdminOfProgram() {
		RegisteredUser admin = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		assertFalse(program.isAdministrator(admin));
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotAdmin() {
		RegisteredUser admin = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).administrators(admin).toProgram();
		assertFalse(program.isAdministrator(admin));
	}
	
	@Test
	public void shouldReturnTrueIfUserInterviewerOfProgram(){
		RegisteredUser interviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).interviewers(interviewer).toProgram();
		assertTrue(program.isInterviewerOfProgram(interviewer));
	}
	
	@Test
	public void shouldReturnFalseIfUserIsNotInterviewerOfProgram(){
		RegisteredUser interviewer = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		assertFalse(program.isInterviewerOfProgram(interviewer));
	}
}
