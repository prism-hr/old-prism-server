package com.zuehlke.pgadmissions.validators;

import static com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY;
import static com.zuehlke.pgadmissions.validators.AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE;
import static com.zuehlke.pgadmissions.validators.AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE;
import static com.zuehlke.pgadmissions.validators.AbstractValidator.MUST_SELECT_DATE_AND_TIMES_IN_THE_FUTURE;
import static com.zuehlke.pgadmissions.validators.ProjectDTOValidator.PROSPECTUS_NO_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.validators.ProjectDTOValidator.PROSPECTUS_NO_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.validators.ProjectDTOValidator.*;

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
	private static final String VALID_PRIMARY_SUPERVISOR_EMAIL = "primary@test.com";
	private static final String VALID_SECONDARY_SUPERVISOR_EMAIL = "secondary@test.com";
	private static final String _256_CHARACTERS_TEXT = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor..";
	private static final String _2001_CHARACTERS_TEXT = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor.in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor.in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor.in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor.in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea....";
	private ProjectDTO projectDTO;
	private PersonValidator personValidator;
	private Person primarySupervisor;
	private UserService userService;
	private Person secondarySupervisor;
	
	@Override
	public void setUp(){
		userService = EasyMock.createMock(UserService.class);
		personValidator = EasyMock.createMock(PersonValidator.class);
		primarySupervisor = createValidPrimarySupervisor();
		secondarySupervisor = createValidSecondarySupervisor();
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
		setupPersonValidatorMock_WithFirstNameEmptyError(primarySupervisor);
		assertThatObjectFieldHasErrorCode(projectDTO, "primarySupervisor.firstname", EMPTY_FIELD_ERROR_MESSAGE);
		EasyMock.verify(personValidator);
	}

	@Test
	public void shouldRejectIf_PrimarySupervisor_IsNotA_RegisteredUser(){
		primarySupervisor.setEmail(INVALID_SUPERVISOR_EMAIL);
		setupUserServiceMock_WithInvalidRegisteredUserEmail();
		assertThatObjectFieldHasErrorCode(projectDTO, "primarySupervisor", PROSPECTUS_SUPERVISOR_NOT_EXISTS);
	}
	
	@Test
	public void shouldRejectIf_SecondarySupervisorSpecified_IsMissing() {
		projectDTO.setSecondarySupervisorSpecified(null);
		assertThatObjectFieldHasErrorCode(projectDTO, "secondarySupervisorSpecified", EMPTY_DROPDOWN_ERROR_MESSAGE);
	}

	@Test
	public void shouldRejectIf_SecondarySupervisorSpecified_IsTrue_And_SecondarySupervisor_IsMissing() {
		projectDTO.setSecondarySupervisorSpecified(true);
		projectDTO.setSecondarySupervisor(null);
		assertThatObjectFieldHasErrorCode(projectDTO, "secondarySupervisor", PROSPECTUS_NO_SECONDARY_SUPERVISOR);
	}
	
	@Test
	public void shouldRejectIf_SecondarySupervisor_HasValidationErrors(){
		projectDTO.setSecondarySupervisorSpecified(true);
		projectDTO.setSecondarySupervisor(secondarySupervisor);
		secondarySupervisor.setFirstname(null); //SYMBOLIC. Supervisor validation is mocked, not performed.
		setupPersonValidatorMock_WithFirstNameEmptyError(secondarySupervisor);
		assertThatObjectFieldHasErrorCode(projectDTO, "secondarySupervisor.firstname", EMPTY_FIELD_ERROR_MESSAGE);
		EasyMock.verify(personValidator);
	}

	@Test
	public void shouldRejectIf_Same_SecondaryAndPrimarySupervisor(){
		secondarySupervisor.setEmail(VALID_PRIMARY_SUPERVISOR_EMAIL);
		projectDTO.setSecondarySupervisorSpecified(true);
		projectDTO.setSecondarySupervisor(secondarySupervisor);
		String[][] expectedErrors = new String[][]{	{"secondarySupervisor", PROSPECTUS_SAME_SUPERVISORS_SECONDARY},	{"primarySupervisor", PROSPECTUS_SAME_SUPERVISORS_PRIMARY}};
		assertThatObjectFieldsHaveErrorCodes(projectDTO, expectedErrors);
		EasyMock.verify(personValidator);
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
				.funding("funding").closingDateSpecified(true)
				.closingDate(futureClosingDate)
				.primarySupervisor(primarySupervisor).program(program)
				.secondarySupervisorSpecified(false)
				.active(true);
		return builder.build();
	}

	private Program createValidProgram() {
		ProgramBuilder builder = new ProgramBuilder();
		builder.id(1).title("Program 1").enabled(true);
		return builder.build();
	}

	private Person createValidPrimarySupervisor() {
		PersonBuilder builder = new PersonBuilder();
		builder.id(1).firstname("Name").lastname("LastName")
				.email(VALID_PRIMARY_SUPERVISOR_EMAIL);
		primarySupervisor = builder.build();
		return primarySupervisor;
	}

	private Person createValidSecondarySupervisor() {
		PersonBuilder builder = new PersonBuilder();
		builder.id(2).firstname("Name2").lastname("LastName2")
		.email(VALID_SECONDARY_SUPERVISOR_EMAIL);
		secondarySupervisor = builder.build();
		return secondarySupervisor;
	}

	private void setupPersonValidatorMock_WithSuccessfullValidation() {
		EasyMock.reset(personValidator);
		EasyMock.expect(personValidator.supports(Person.class)).andReturn(true).anyTimes();
		personValidator.validate(EasyMock.eq(primarySupervisor), EasyMock.anyObject(Errors.class));
		EasyMock.expectLastCall().times(1);
		personValidator.validate(EasyMock.eq(secondarySupervisor), EasyMock.anyObject(Errors.class));
		EasyMock.expectLastCall().times(0,1);
		EasyMock.replay(personValidator);
	}
	
	private void setupPersonValidatorMock_WithFirstNameEmptyError(Person person) {
		EasyMock.reset(personValidator);
		EasyMock.expect(personValidator.supports(Person.class)).andReturn(true).anyTimes();

		personValidator.validate(EasyMock.not(EasyMock.eq(person)), EasyMock.anyObject(Errors.class));
		EasyMock.expectLastCall().times(0,1);
		
		personValidator.validate(EasyMock.eq(person), EasyMock.anyObject(Errors.class));
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
		EasyMock.expect(userService.getUserByEmailIncludingDisabledAccounts(VALID_PRIMARY_SUPERVISOR_EMAIL)).andReturn(createRegisteredUser()).times(1,2);
		EasyMock.expect(userService.getUserByEmailIncludingDisabledAccounts(VALID_SECONDARY_SUPERVISOR_EMAIL)).andReturn(createRegisteredUser()).times(0,1);
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
