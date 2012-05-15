package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class InterviewerServiceTest {

	private InterviewerDAO interviewerDAOMock;
	private InterviewerService interviewerService;
	private UserService userServiceMock;
	private ProgramsService programsServiceMock;

	@Test
	public void shouldGetInterviewerById() {
		Interviewer interviewer = EasyMock.createMock(Interviewer.class);
		interviewer.setId(2);
		EasyMock.expect(interviewerDAOMock.getInterviewerById(2)).andReturn(interviewer);
		EasyMock.replay(interviewer, interviewerDAOMock);
		Assert.assertEquals(interviewer, interviewerService.getInterviewerById(2));
	}
	
	@Test
	public void shouldGetInterviewerByUser() {
		Interviewer interviewer = EasyMock.createMock(Interviewer.class);
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		interviewer.setUser(user);
		EasyMock.expect(interviewerDAOMock.getInterviewerByUser(user)).andReturn(interviewer);
		EasyMock.replay(interviewer, interviewerDAOMock);
		Assert.assertEquals(interviewer, interviewerService.getInterviewerByUser(user));
	}
	
	@Test
	public void shouldDelegateSaveToDAO() {
		Interviewer interviewer = EasyMock.createMock(Interviewer.class);
		interviewerDAOMock.save(interviewer);
		EasyMock.replay(interviewerDAOMock);
		interviewerService.save(interviewer);
		EasyMock.verify(interviewerDAOMock);
	}
	
	@Test
	public void shouldCreateNewUserWithInterviewerRoleInProgram() {
		RegisteredUser interviewer = new RegisteredUserBuilder().id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		EasyMock.expect(userServiceMock.createNewUserForProgramme(interviewer.getFirstName(), interviewer.getLastName(), interviewer.getEmail(), program, Authority.INTERVIEWER)).andReturn(interviewer);
		EasyMock.replay(userServiceMock);
		
		RegisteredUser newInterviewer = interviewerService.createNewUserWithInterviewerRoleInProgram(interviewer, program);
		
		EasyMock.verify(userServiceMock);	
		Assert.assertEquals(interviewer, newInterviewer);
		
	}
	
	
	@Test
	public void shouldAddExistingInterviewerToProgram() {
		RegisteredUser interviewer = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.INTERVIEWER).toRole()).id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		userServiceMock.save(interviewer);
		programsServiceMock.save(program);
		EasyMock.replay(userServiceMock, programsServiceMock);
		
		interviewerService.addInterviewerToProgram(interviewer, program);
		
		EasyMock.verify(userServiceMock, programsServiceMock);
		Assert.assertEquals(1, program.getInterviewers().size());
		Assert.assertTrue(program.getInterviewers().contains(interviewer));
		Assert.assertEquals(1, interviewer.getProgramsOfWhichInterviewer().size());
		Assert.assertTrue(interviewer.getProgramsOfWhichInterviewer().contains(program));
	}
	
	@Test
	public void shouldAddExistingUserToProgramAndAddInterviewerRole() {
		RegisteredUser interviewer = new RegisteredUserBuilder().roles(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).id(1).firstName("Bob").lastName("Bobson").email("bob@bobson.com").toUser();
		Program program = new ProgramBuilder().id(1).toProgram();
		userServiceMock.addRoleToUser(interviewer, Authority.INTERVIEWER);
		userServiceMock.save(interviewer);
		programsServiceMock.save(program);
		EasyMock.replay(userServiceMock, programsServiceMock);
		
		interviewerService.addInterviewerToProgram(interviewer, program);
		
		EasyMock.verify(userServiceMock, programsServiceMock);
		Assert.assertEquals(1, program.getInterviewers().size());
		Assert.assertTrue(program.getInterviewers().contains(interviewer));
		Assert.assertEquals(1, interviewer.getProgramsOfWhichInterviewer().size());
		Assert.assertTrue(interviewer.getProgramsOfWhichInterviewer().contains(program));
	}
	
	@Test
	public void shouldCreateInterviewerObjectFromUserAndAddToApplication() {
		final Interviewer interviewer = new InterviewerBuilder().id(1).toInterviewer();
		interviewerService = new InterviewerService(interviewerDAOMock, userServiceMock, programsServiceMock){
			@Override
			public Interviewer createNewInterviewer() {
				return interviewer;
			}
		};
		
		RegisteredUser interviewerUser =  new RegisteredUserBuilder().id(1).toUser();
		ApplicationForm application = new ApplicationFormBuilder().interviews(new Interview()).id(1).toApplicationForm();
		interviewerDAOMock.save(interviewer);
		EasyMock.replay(interviewerDAOMock);
		interviewerService.createInterviewerToApplication(interviewerUser, application);
		EasyMock.verify(interviewerDAOMock);
		Assert.assertNotNull(interviewer.getUser());
		Assert.assertNotNull(interviewer.getApplication());
		Assert.assertEquals(application, interviewer.getApplication());
		Assert.assertEquals(interviewerUser, interviewer.getUser());
		
		
	}
	
	@Before
	public void setUp() {
		interviewerDAOMock = EasyMock.createMock(InterviewerDAO.class);
		userServiceMock =  EasyMock.createMock(UserService.class);
		programsServiceMock =  EasyMock.createMock(ProgramsService.class);
		interviewerService = new InterviewerService(interviewerDAOMock, userServiceMock, programsServiceMock);
	}
	
}
