package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

public class AccountValidatorTest {

	private AccountValidator accountValidator;
	private EncryptionUtils encryptionUtilsMock;
	private UserService userServiceMock;
	private RegisteredUser user;
	private List<RegisteredUser> userArray;
	private RegisteredUser currentUser;
	
	@Test
	public void shouldSupportApplicantRecordValidator() {
		assertTrue(accountValidator.supports(RegisteredUser.class));
	}

	@Before
	public void setup(){
		user = new RegisteredUserBuilder().id(1).username("email").firstName("bob").lastName("bobson").email("email@test.com").confirmPassword("12345678").newPassword("12345678").password("5f4dcc3b5aa").toUser();
		encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		userArray = new ArrayList<RegisteredUser>();
		userArray.add(user);
		currentUser = new RegisteredUserBuilder().id(1).username("email").firstName("bob").lastName("bobson").email("email@test.com").confirmPassword("12345678").newPassword("12345678").password("5f4dcc3b5aa").toUser();
		accountValidator = new AccountValidator(userServiceMock, encryptionUtilsMock){
			@Override
			public RegisteredUser getCurrentUser() {
				return currentUser;
			}
		};
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

	}
	
	@Test
	public void shouldRejectIfNewPasswordIsNotSetAndCurrentAndConfirmAreSet() {
		user.setNewPassword(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.replay(userServiceMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("newPassword").getCode());
	}

	
	@Test
	public void shouldRejectIfCurrentPasswordIsNotSetAndConfirmAndNewPasswordAreSet() {
		user.setPassword(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.replay(userServiceMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("password").getCode());
	}
	
	@Test
	public void shouldRejectIfConfirmPasswordIsNotSetAndCurrentAndNewPasswordAreSet() {
		user.setConfirmPassword(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "confirmPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.replay(userServiceMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("confirmPassword").getCode());
	}
	
	@Test
	public void shouldRejectIfCurrentPasswordNotSameWithExisting() {
		user.setPassword("12345678");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("25d55ad283aa400af464c76d713c07ad");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("25d55ad283aa400af464c76d713c07ad");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("account.currentpassword.notmatch", mappingResult.getFieldError("password").getCode());
	}
	
	@Test
	public void shouldRejectIfNewAndConfirmPasswordsNotSame() {
		user.setConfirmPassword("password");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("user.passwords.notmatch", mappingResult.getFieldError("newPassword").getCode());
		Assert.assertEquals("user.passwords.notmatch", mappingResult.getFieldError("confirmPassword").getCode());
	}
	
	@Test
	public void shouldRejectIfNewPasswordIsLessThan8Chars() {
		user.setNewPassword("1234");
		user.setConfirmPassword("1234");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234")).andReturn("1234");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.password.small", mappingResult.getFieldError("newPassword").getCode());
	}
	
	@Test
	public void shouldRejectIfNewPasswordIsMoreThan15Chars() {
		user.setNewPassword("1234567891234567");
		user.setConfirmPassword("1234567891234567");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234567891234567")).andReturn("1234567891234567");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.password.large", mappingResult.getFieldError("newPassword").getCode());
	}
	
	@Test
	public void shouldRejectIfContainsSpecialChars() {
		user.setNewPassword(" 12o*-lala");
		user.setConfirmPassword(" 12o*-lala");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash(" 12o*-lala")).andReturn(" 12o*-lala");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.password.nonalphanumeric", mappingResult.getFieldError("newPassword").getCode());
	}
	
	

	@Test
	public void shouldRejectIfNewEmailAlreadyExists() {
		userArray.add(new RegisteredUserBuilder().id(1).username("email2@test.com").firstName("bob").lastName("bobson").
				email("email2@test.com").confirmPassword("12345678").newPassword("12345678").password("5f4dcc3b5aa").toUser());
		user.setEmail("email2@test.com");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("account.email.alreadyexists", mappingResult.getFieldError("email").getCode());
	}
	
	@Test
	public void shouldRejectIfEmailNotValidEmail() {
		user.setEmail("notvalidemail");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.email.notvalid", mappingResult.getFieldError("email").getCode());

	}
	
	@Test
	public void shouldRejectIfNewPasswordSameWithExisting() {
		user.setNewPassword("5f4dcc3b5aa");
		user.setConfirmPassword("5f4dcc3b5aa");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("account.newPassword.same", mappingResult.getFieldError("newPassword").getCode());
	}
	
	@Test
	public void shouldRejectIfFirstNameEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");
		user.setFirstName("");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("firstName").getCode());
	}

	@Test
	public void shouldRejectIfLastNameNull() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "lastName");
		user.setLastName(null);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getAllUsers()).andReturn(userArray);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastName").getCode());
	}
	
}
