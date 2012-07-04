package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.services.UserService;

public class UserDTOValidatorTest {

	private UserDTOValidator validator;
	private UserDTO user;
	private UserService userServiceMock;

	@Before
	public void setup() {
		userServiceMock = EasyMock.createMock(UserService.class);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null).anyTimes();
		EasyMock.replay(userServiceMock);
		validator = new UserDTOValidator(userServiceMock);

		user = new UserDTO();
		user.setFirstName("bob");
		user.setLastName("Smith");
		user.setEmail("email@test.com");
		user.setSelectedProgram(new ProgramBuilder().id(4).toProgram());
		user.setSelectedAuthorities(Authority.REVIEWER);
	}

	@Test
	public void shouldSupportNewUserDTO() {
		assertTrue(validator.supports(UserDTO.class));
	}

	@Test
	public void shouldNotRejectValidUser() {
		BindingResult mappingResult = new BeanPropertyBindingResult(user, "*");
		validator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	@Test
	public void shouldRejectIfFirstNameEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");
		user.setFirstName("");
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("firstName").getCode());
	}

	@Test
	public void shouldRejectIfLastNameNull() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "lastName");
		user.setLastName(null);
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastName").getCode());
	}

	@Test
	public void shouldRejectIfEmailNotValid() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		user.setEmail("");
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.email.notvalid", mappingResult.getFieldError("email").getCode());
	}

	@Test
	public void shouldRejectIfEmailThatOfExistingApplicant() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		user.setEmail("applicant@test.com");
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("applicant@test.com"))
				.andReturn(new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(1).authorityEnum(Authority.APPLICANT).toRole()).toUser()).anyTimes();
		EasyMock.replay(userServiceMock);
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.email.applicant", mappingResult.getFieldError("email").getCode());
	}

	@Test
	public void shouldRejectIfProgramIsNullAndAuthoritiesNotSuperadminOnly() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedProgram");
		user.setSelectedProgram(null);
		user.setSelectedAuthorities(Authority.REVIEWER, Authority.SUPERADMINISTRATOR);
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("selectedProgram").getCode());
	}

	@Test
	public void shouldNotRejectIfProgramIsNullAndAuthoritiesIsSuperadmin() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedProgram");
		user.setSelectedProgram(null);
		user.setSelectedAuthorities(Authority.SUPERADMINISTRATOR);
		validator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());

	}

	@Test
	public void shouldRejectIfSelectedAuthoritiesAreNull() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedAuthorities");
		user.setSelectedAuthorities((Authority[]) null);
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("selectedAuthorities").getCode());
	}

	@Test
	public void shouldRejectIfSelectedAuthoritiesAreEmpyt() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedAuthorities");
		user.setSelectedAuthorities(new Authority[] {});
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("selectedAuthorities").getCode());
	}
}
