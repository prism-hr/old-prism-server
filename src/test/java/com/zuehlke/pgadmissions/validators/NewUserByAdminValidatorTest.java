package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.UserService;

public class NewUserByAdminValidatorTest {
	private NewUserByAdminValidator validator;
	private RegisteredUser user;
	private UserService userServiceMock;

	@Before
	public void setup() {
		userServiceMock = EasyMock.createMock(UserService.class);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@bla.com")).andReturn(null).anyTimes();
		EasyMock.replay(userServiceMock);
		validator = new NewUserByAdminValidator(userServiceMock);
		user = new RegisteredUserBuilder().firstName("first").lastName("last").email("email@bla.com").toUser();
	}

	@Test
	public void shouldSupportRegisteredUser() {
		assertTrue(validator.supports(RegisteredUser.class));
	}

	@Test
	public void shouldNotRejectValidUser() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");
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
}
