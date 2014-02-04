package com.zuehlke.pgadmissions.converters;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reset;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectDTOBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.DateUtils;

public class ProjectConverterTest {

	private static final int PROJECT_ID = 1;
	private static final String ADMINISTRATOR_EMAIL = "administrator@email.com";
	private static final String SECONDARY_SUPERVISOR_EMAIL = "secondary@email.com";
	private static final String PRIMARY_SUPERVISOR_EMAIL = "primary@email.com";
	private ProjectConverter converter;
	private UserService userService;
	private ProgramsService programService;
	private Person administratorPerson;
	private Person primarySupervisorPerson;
	private Person secondarySupervisorPerson;
	private RegisteredUser administratorUser;
	private RegisteredUser primarySupervisorUser;
	private RegisteredUser secondarySupervisorUser;
	private Program program;
	private Project project;
	private ProjectDTO projectDTO;
	
	@Before
	public void setup(){
		setupProgram();
		setupSupervisorsPerson();
		setupSupervisorsUser();
		setupProject();
		setupFullProjectDTO(administratorPerson, primarySupervisorPerson, secondarySupervisorPerson, program);
		setupUserService();
		setupProgramService();
		converter = new ProjectConverter(userService, programService);
	}
	
	@After
	public void tearDown(){
		EasyMock.verify(userService, programService);
	}
	
	@Test
	public void shouldReturn_NullProject_ForNullDto(){
		resetAndReplayMocks();
		assertThat(converter.toDomainObject(null), nullValue());
	}

	@Test
	public void shouldReturn_NullProject_WhenProjectDoesNotExists(){
		reset(programService, userService);
		EasyMock.expect(programService.getProject(PROJECT_ID)).andReturn(null);
		EasyMock.replay(programService, userService);
		assertThat(converter.toDomainObject(projectDTO), nullValue());
	}
	
	@Test
	public void shouldReturn_NewProject(){
		projectDTO.setId(null);
		EasyMock.reset(programService);
		EasyMock.replay(programService);

		Project convertedProject = converter.toDomainObject(projectDTO);
		assertThatConvertedProjectHasSameFieldsAsDto(convertedProject, projectDTO);
	}


	@Test
	public void shouldReturn_ExistingProject_ForDtoWithProjectId(){
	    Project convertedProject = converter.toDomainObject(projectDTO);
		assertThat(convertedProject, equalTo(project));
		assertThatConvertedProjectHasSameFieldsAsDto(convertedProject, projectDTO);
	}
	
	private void assertThatConvertedProjectHasSameFieldsAsDto(Project project, ProjectDTO dto) {
		assertThat(project.getId(), equalTo(dto.getId()));
		assertThat(project.getAuthor(), nullValue());
		assertThat(project.getAdvert(), notNullValue());
		assertThat(project.getAdvert().getActive(), equalTo(dto.getActive()));
		assertThat(project.getAdvert().getDescription(), equalTo(dto.getDescription()));
		assertThat(project.getAdvert().getFunding(), equalTo(dto.getFunding()));
		assertThat(project.getAdvert().getStudyDuration(), nullValue());
		assertThat(project.getAdvert().getTitle(), equalTo(dto.getTitle()));
		assertThat(project.getProgram(), equalTo(dto.getProgram()));
		assertThat(project.getClosingDate(), equalTo(dto.getClosingDate()));
		assertThat(project.getAdministrator(), equalTo(administratorUser));
		assertThat(project.getPrimarySupervisor(), equalTo(primarySupervisorUser));
		assertThat(project.getSecondarySupervisor(), equalTo(secondarySupervisorUser));
	}
	
	private void resetAndReplayMocks() {
		EasyMock.reset(userService, programService);
		EasyMock.replay(userService, programService);
	}
	
	
	private void setupSupervisorsPerson() {
	    administratorPerson = new PersonBuilder().id(0).email(ADMINISTRATOR_EMAIL).build();
		primarySupervisorPerson = new PersonBuilder().id(1).email(PRIMARY_SUPERVISOR_EMAIL).build();
		secondarySupervisorPerson =  new PersonBuilder().id(2).email(SECONDARY_SUPERVISOR_EMAIL).build();
	}

	private void setupSupervisorsUser() {
	    administratorUser = new RegisteredUserBuilder().id(0).email(ADMINISTRATOR_EMAIL).build();
		primarySupervisorUser = new RegisteredUserBuilder().id(1).email(PRIMARY_SUPERVISOR_EMAIL).build();
		secondarySupervisorUser = new RegisteredUserBuilder().id(2).email(SECONDARY_SUPERVISOR_EMAIL).build();
	}

	private void setupProgram() {
		program = new ProgramBuilder().id(1).build();
	}

	private void setupFullProjectDTO(Person administrator, Person primarySupervisor, Person secondarySupervisor, Program program) {
		ProjectDTOBuilder builder = new ProjectDTOBuilder();
		builder.id(PROJECT_ID)
		.program(program)
		.title("title").description("description").funding("funding")
		.closingDateSpecified(true).closingDate(DateUtils.truncateToDay(new Date()))
		.administrator(administrator)
		.primarySupervisor(primarySupervisor)
		.secondarySupervisorSpecified(true).secondarySupervisor(secondarySupervisor)
		.active(true);
		projectDTO = builder.build();
	}

	private void setupProject() {
		ProjectBuilder builder = new ProjectBuilder();
		builder.id(PROJECT_ID);
		project= builder.build();
	}

	private void setupProgramService() {
		programService = EasyMock.createMock(ProgramsService.class);
		expect(programService.getProject(PROJECT_ID)).andReturn(project);
		EasyMock.replay(programService);
	}

	private void setupUserService() {
		userService = EasyMock.createMock(UserService.class);
		expect(userService.getUserByEmailIncludingDisabledAccounts(ADMINISTRATOR_EMAIL)).andReturn(administratorUser);
		expect(userService.getUserByEmailIncludingDisabledAccounts(PRIMARY_SUPERVISOR_EMAIL)).andReturn(primarySupervisorUser);
		expect(userService.getUserByEmailIncludingDisabledAccounts(SECONDARY_SUPERVISOR_EMAIL)).andReturn(secondarySupervisorUser);
		EasyMock.replay(userService);
	}
}
