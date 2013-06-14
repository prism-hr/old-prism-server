package com.zuehlke.pgadmissions.validators;

import static com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor.ERROR_UNIT_FOR_DURATION_OF_STUDY;
import static com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY;
import static com.zuehlke.pgadmissions.validators.AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE;
import static com.zuehlke.pgadmissions.validators.AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE;
import static com.zuehlke.pgadmissions.validators.AbstractValidator.MUST_SELECT_DATE_AND_TIMES_IN_THE_FUTURE;
import static com.zuehlke.pgadmissions.validators.ProjectDTOValidator.PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER;
import static com.zuehlke.pgadmissions.validators.ProjectDTOValidator.PROSPECTUS_NO_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.validators.ProjectDTOValidator.PROSPECTUS_SUPERVISOR_NOT_EXISTS;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Test;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectDTOBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.services.UserService;


public class ProjectDTOValidatorTest extends ValidatorTest<ProjectDTO> {

	private static final String INVALID_SUPERVISOR_EMAIL = "invalid@email.test";
	private static final String VALID_SUPERVISOR_EMAIL = "email@test.com";
	private static final String _256_CHARACTERS_TEXT = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor..";
	private static final String _2001_CHARACTERS_TEXT = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor.in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor.in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor.in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor.in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea....";
	private ProjectDTO projectDTO;
	private PersonValidator personValidator;
	private Person primarySupervisor;
	private UserService userService;
	
	@Override
	public void setUp(){
		userService = EasyMock.createMock(UserService.class);
		personValidator = EasyMock.createMock(PersonValidator.class);
		primarySupervisor = createValidSupervisor();
		setupPersonValidatorMock_WithSuccessfullValidation();
		setupUserServiceMock_WithValidRegisteredUserEmail();
		super.setUp();
	}
	
	@After
	public void tearDown(){
		EasyMock.verify(personValidator,userService);
	}
	
	@Override
	protected void resetMocks() {
		EasyMock.reset(personValidator, userService);
		EasyMock.replay(personValidator, userService);
	}

	@Test
	public void shouldRejectIf_Program_IsMissing() {
		projectDTO.setProgram(null);
		assertThatObjectFieldHasErrorCode(projectDTO, "program", EMPTY_DROPDOWN_ERROR_MESSAGE);
	}

	@Test
	public void shouldRejectIf_Title_IsMissing() {
		projectDTO.setTitle(null);
		assertThatObjectFieldHasErrorCode(projectDTO, "title", EMPTY_FIELD_ERROR_MESSAGE);
	}

	@Test
	public void shouldRejectIf_Description_IsMissing() {
		projectDTO.setDescription(null);
		assertThatObjectFieldHasErrorCode(projectDTO, "description", EMPTY_FIELD_ERROR_MESSAGE);
	}

	@Test
	public void shouldRejectIf_StudyDuration_IsMissing() {
		projectDTO.setStudyDuration(null);
		assertThatObjectFieldHasErrorCode(projectDTO, "studyDuration", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
	}

	@Test
	public void shouldRejectIf_StudyDuration_IsErrorFor_Value() {
		projectDTO.setStudyDuration(ERROR_VALUE_FOR_DURATION_OF_STUDY);
		assertThatObjectFieldHasErrorCode(projectDTO, "studyDuration", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
	}

	@Test
	public void shouldRejectIf_StudyDuration_IsErrorFor_Unit() {
		projectDTO.setStudyDuration(ERROR_UNIT_FOR_DURATION_OF_STUDY);
		assertThatObjectFieldHasErrorCode(projectDTO, "studyDuration", EMPTY_DROPDOWN_ERROR_MESSAGE);
	}

	@Test
	public void shouldRejectIf_ClosingDateSpecified_IsMissing() {
		projectDTO.setClosingDateSpecified(null);
		assertThatObjectFieldHasErrorCode(projectDTO, "closingDateSpecified", EMPTY_DROPDOWN_ERROR_MESSAGE);
	}

	@Test
	public void shouldRejectIf_ClosingDateSpecified_IsTrue_And_ClosingDate_IsMissing() {
		projectDTO.setClosingDate(null);
		assertThatObjectFieldHasErrorCode(projectDTO, "closingDate", EMPTY_FIELD_ERROR_MESSAGE);
	}

	@Test
	public void shouldRejectIf_ClosingDateSpecified_IsTrue_And_ClosingDate_IsNotInTheFuture() {
		projectDTO.setClosingDate(new Date());
		assertThatObjectFieldHasErrorCode(projectDTO, "closingDate", MUST_SELECT_DATE_AND_TIMES_IN_THE_FUTURE);
	}

	@Test
	public void shouldRejectIf_Active_IsMissing() {
		projectDTO.setActive(null);
		assertThatObjectFieldHasErrorCode(projectDTO, "active", EMPTY_DROPDOWN_ERROR_MESSAGE);
	}
	
	@Test
	public void shouldRejectIf_Funding_HasMoreThan_255_Characters(){
		projectDTO.setFunding(_256_CHARACTERS_TEXT);
		assertThatObjectFieldHasErrorMessage(projectDTO, "funding", "A maximum of 255 characters are allowed.");
	}

	@Test
	public void shouldRejectIf_Title_HasMoreThan_255_Characters(){
		projectDTO.setTitle(_256_CHARACTERS_TEXT);
		assertThatObjectFieldHasErrorMessage(projectDTO, "title", "A maximum of 255 characters are allowed.");
	}

	@Test
	public void shouldRejectIf_Description_HasMoreThan_2000_Characters(){
		projectDTO.setDescription(_2001_CHARACTERS_TEXT);
		assertThatObjectFieldHasErrorMessage(projectDTO, "description", "A maximum of 2000 characters are allowed.");
	}

	@Test
	public void shouldRejectIf_PrimarySupervisor_IsMissing(){
		resetMocks();
		projectDTO.setPrimarySupervisor(null);
		assertThatObjectFieldHasErrorCode(projectDTO, "primarySupervisor", PROSPECTUS_NO_PRIMARY_SUPERVISOR);
	}
	
	@Test
	public void shouldRejectIf_PrimarySupervisor_HasValidationErrors(){
		primarySupervisor.setFirstname(null); //SYMBOLIC. Supervisor validation is mocked, not performed.
		setupPersonValidatorMock_WithFirstNameEmptyError();
		assertThatObjectFieldHasErrorCode(projectDTO, "primarySupervisor.firstname", EMPTY_FIELD_ERROR_MESSAGE);
		EasyMock.verify(personValidator);
	}

	@Test
	public void shouldRejectIf_PrimarySupervisor_IsNotA_RegisteredUser(){
		primarySupervisor.setEmail(INVALID_SUPERVISOR_EMAIL);
		setupUserServiceMock_WithInvalidRegisteredUserEmail();
		assertThatObjectFieldHasErrorCode(projectDTO, "primarySupervisor", PROSPECTUS_SUPERVISOR_NOT_EXISTS);
	}

	//SETUP

	@Override
	protected ProjectDTOValidator createValidator() {
		return new ProjectDTOValidator(personValidator, userService);
	}
	
	@Override
	protected void setObject(ProjectDTO dto) {
		this.projectDTO=dto;
	}
	
	@Override
	protected String getObjectName() {
		return "projectDTO";
	}
	
	@Override
	protected ProjectDTO createValidObject() {
		ProjectDTOBuilder builder = new ProjectDTOBuilder();
		Program program = createValidProgram();
		Date futureClosingDate = DateUtils.addMonths(new Date(), 1);
		builder.id(1).title("title").description("description")
				.funding("funding").studyDuration(1).closingDateSpecified(true)
				.closingDate(futureClosingDate)
				.primarySupervisor(primarySupervisor).program(program)
				.active(true);
		return builder.build();
	}

	private Program createValidProgram() {
		ProgramBuilder builder = new ProgramBuilder();
		builder.id(1).title("Program 1").enabled(true);
		return builder.build();
	}

	private Person createValidSupervisor() {
		PersonBuilder builder = new PersonBuilder();
		builder.id(1).firstname("Name").lastname("Last Name")
				.email(VALID_SUPERVISOR_EMAIL);
		primarySupervisor = builder.build();
		return primarySupervisor;
	}

	private void setupPersonValidatorMock_WithSuccessfullValidation() {
		EasyMock.reset(personValidator);
		EasyMock.expect(personValidator.supports(Person.class)).andReturn(true);
		personValidator.validate(EasyMock.anyObject(Person.class), EasyMock.anyObject(Errors.class));
		EasyMock.expectLastCall();
		EasyMock.replay(personValidator);
	}
	
	private void setupPersonValidatorMock_WithFirstNameEmptyError() {
		EasyMock.reset(personValidator);
		EasyMock.expect(personValidator.supports(Person.class)).andReturn(true).anyTimes();
		personValidator.validate(EasyMock.anyObject(Person.class), EasyMock.anyObject(Errors.class));
		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				Errors errors = (Errors) EasyMock.getCurrentArguments()[1];
				errors.rejectValue("firstname", EMPTY_FIELD_ERROR_MESSAGE);
				return null;
			}
		});
		EasyMock.replay(personValidator);
	}
	
	private void setupUserServiceMock_WithValidRegisteredUserEmail() {
		EasyMock.reset(userService);
		EasyMock.expect(userService.getUserByEmailIncludingDisabledAccounts(VALID_SUPERVISOR_EMAIL)).andReturn(createRegisteredUser()).times(1);
		EasyMock.replay(userService);
	}

	private RegisteredUser createRegisteredUser() {
		RegisteredUserBuilder builder = new RegisteredUserBuilder();
		builder.id(1).firstName(primarySupervisor.getFirstname()).lastName(primarySupervisor.getLastname()).email(primarySupervisor.getEmail());
		return builder.build();
	}
	
	private void setupUserServiceMock_WithInvalidRegisteredUserEmail() {
		EasyMock.reset(userService);
		EasyMock.expect(userService.getUserByEmailIncludingDisabledAccounts(INVALID_SUPERVISOR_EMAIL)).andReturn(null).times(1);
		EasyMock.replay(userService);
	}

}
