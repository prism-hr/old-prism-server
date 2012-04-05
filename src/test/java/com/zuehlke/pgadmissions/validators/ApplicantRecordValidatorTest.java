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
import com.zuehlke.pgadmissions.dto.RegistrationDTO;
import com.zuehlke.pgadmissions.services.UserService;

public class ApplicantRecordValidatorTest {

	private RegistrationDTO record;
	private ApplicantRecordValidator recordValidator;
	private UserService userServiceMock;
	private RegisteredUser user;
	
	@Test
	public void shouldSupportApplicantRecordValidator() {
		assertTrue(recordValidator.supports(RegistrationDTO.class));
	}

	@Before
	public void setup(){
		record = new RegistrationDTO();
		record.setFirstname("Mark");
		record.setLastname("Euston");
		record.setEmail("meuston@gmail.com");
		record.setPassword("12345678");
		record.setConfirmPassword("12345678");
		user = new RegisteredUserBuilder().id(1).email("differentEmail").toUser();
		userServiceMock = EasyMock.createMock(UserService.class);
		recordValidator = new ApplicantRecordValidator(userServiceMock);
	}
	
	@Test
	public void shouldRejectIfFirstNameIsEmpty() {
		record.setFirstname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "firstname");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.firstname.notempty", mappingResult.getFieldError("firstname").getCode());
	}

	@Test
	public void shouldRejectIfLasttNameIsEmpty() {
		record.setLastname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "lastname");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.lastname.notempty", mappingResult.getFieldError("lastname").getCode());
	}

	@Test
	public void shouldRejectIfEmailNotValidEmail() {
		record.setEmail("nonvalidemail");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "email");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.email.invalid", mappingResult.getFieldError("email").getCode());
	}
	
	@Test
	public void shouldRejectIfNoConfirmPassword() {
		record.setConfirmPassword(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "confirmPassword");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.confirmPassword.notempty", mappingResult.getFieldError("confirmPassword").getCode());
	}
	
	@Test
	public void shouldRejectIfPasswordsDoNotMatch() {
		record.setConfirmPassword("12345");
		record.setPassword("12345678");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "confirmPassword");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("record.confirmPassword.notmatch", mappingResult.getFieldError("confirmPassword").getCode());
		Assert.assertEquals("record.password.notmatch", mappingResult.getFieldError("password").getCode());
	}
	
	@Test
	public void shouldRejectIfPasswordsNotValid() {
		record.setPassword("12");
		record.setConfirmPassword("12");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "password");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.password.notvalid", mappingResult.getFieldError("password").getCode());
	}
	
	@Test
	public void shouldNotRejectIfPasswordsMatch() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "confirmPassword");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfEmailAlreadyExist() {
		user.setEmail("meuston@gmail.com");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "email");
		userServiceMock.save(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(Arrays.asList(user));
		EasyMock.replay(userServiceMock);
		recordValidator.shouldValidateSameEmail(true);
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.email.alreadyexists", mappingResult.getFieldError("email").getCode());
	}
}
