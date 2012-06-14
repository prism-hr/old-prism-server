package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.UserService;

public class RegisterFormValidatorTest {

	private RegisterFormValidator recordValidator;
	private UserService userServiceMock;
	private RegisteredUser user;
	
	@Test
	public void shouldSupportApplicantRecordValidator() {
		assertTrue(recordValidator.supports(RegisteredUser.class));
	}

	@Before
	public void setup(){
		user = new RegisteredUserBuilder().username("email").firstName("bob").lastName("bobson").email("email@test.com").confirmPassword("12345678").password("12345678").toUser();

		userServiceMock = EasyMock.createMock(UserService.class);
		recordValidator = new RegisterFormValidator(userServiceMock);
	}
	
	@Test
	public void shouldRejectIfFirstNameIsEmpty() {
		user.setFirstName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.firstName.notempty", mappingResult.getFieldError("firstName").getCode());
	}

	@Test
	public void shouldRejectIfLasttNameIsEmpty() {
		user.setLastName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "lastName");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.lastName.notempty", mappingResult.getFieldError("lastName").getCode());
	}

	@Test
	public void shouldRejectIfEmailNotValidEmail() {
		user.setEmail("nonvalidemail");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.email.invalid", mappingResult.getFieldError("email").getCode());
	}
	
	@Test
	public void shouldRejectIfNoConfirmPassword() {
		user.setConfirmPassword(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "confirmPassword");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.confirmPassword.notempty", mappingResult.getFieldError("confirmPassword").getCode());
	}
	
	@Test
	public void shouldRejectIfPasswordsDoNotMatch() {
		user.setConfirmPassword("12345");
		user.setPassword("12345678");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "confirmPassword");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(user, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("user.confirmPassword.notmatch", mappingResult.getFieldError("confirmPassword").getCode());
		Assert.assertEquals("user.password.notmatch", mappingResult.getFieldError("password").getCode());
	}
	
	@Test
	public void shouldRejectIfPasswordsNotValid() {
		user.setPassword("12");
		user.setConfirmPassword("12");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.password.notvalid", mappingResult.getFieldError("password").getCode());
	}
	
	@Test
	public void shouldNotRejectIfPasswordsMatch() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "confirmPassword");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfEmailAlreadyExist() {
		user.setEmail("meuston@gmail.com");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.shouldValidateSameEmail(true);
		recordValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.email.alreadyexists", mappingResult.getFieldError("email").getCode());
	}
}
