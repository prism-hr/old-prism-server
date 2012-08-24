package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class AccountValidatorTest {

    @Autowired
	private AccountValidator accountValidator;
    
    @Autowired
	private EncryptionUtils encryptionUtilsMock;
	
    @Autowired
    private UserService userServiceMock;
	
    private RegisteredUser user;
	
    private RegisteredUser currentUser;
	

	@Before
	public void setup(){
		user = new RegisteredUserBuilder().id(1).username("email").firstName("bob").lastName("bobson").email("email@test.com").confirmPassword("12345678").newPassword("12345678").password("5f4dcc3b5aa").toUser();
		currentUser = new RegisteredUserBuilder().id(1).username("email").firstName("bob").lastName("bobson").email("email@test.com").confirmPassword("12345678").newPassword("12345678").password("5f4dcc3b5aa").toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
//		accountValidator = new AccountValidator(userServiceMock, encryptionUtilsMock){
//			@Override
//			public RegisteredUser getCurrentUser() {
//				return currentUser;
//			}
//		};
	}
	
	@Test
	@DirtiesContext
	public void shouldSupportApplicantRecordValidator() {
	    assertTrue(accountValidator.supports(RegisteredUser.class));
	}
	
	@Test
	@DirtiesContext
	public void shouldRejectIfNewPasswordIsNotSetAndCurrentAndConfirmAreSet() {
		user.setNewPassword(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
		EasyMock.replay(userServiceMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("newPassword").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfCurrentPasswordIsNotSetAndConfirmAndNewPasswordAreSet() {
		user.setPassword(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
		EasyMock.replay(userServiceMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("password").getCode());
	}
	
	@Test
	@DirtiesContext
	public void shouldRejectIfConfirmPasswordIsNotSetAndCurrentAndNewPasswordAreSet() {
		user.setConfirmPassword(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "confirmPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
		EasyMock.replay(userServiceMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("confirmPassword").getCode());
	}
	
	@Test
	@DirtiesContext
	public void shouldRejectIfCurrentPasswordNotSameWithExisting() {
		user.setPassword("12345678");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("25d55ad283aa400af464c76d713c07ad");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("25d55ad283aa400af464c76d713c07ad");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("account.currentpassword.notmatch", mappingResult.getFieldError("password").getCode());
	}
	
	public void shouldRejectIfNewAndConfirmPasswordsNotSame() {
		user.setConfirmPassword("password");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("user.passwords.notmatch", mappingResult.getFieldError("newPassword").getCode());
		Assert.assertEquals("user.passwords.notmatch", mappingResult.getFieldError("confirmPassword").getCode());
	}
	
	@Test
	@DirtiesContext
	public void shouldRejectIfNewPasswordIsLessThan8Chars() {
		user.setNewPassword("1234");
		user.setConfirmPassword("1234");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234")).andReturn("1234");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.password.small", mappingResult.getFieldError("newPassword").getCode());
	}
	
	@Test
	@DirtiesContext
	public void shouldRejectIfNewPasswordIsMoreThan15Chars() {
		user.setNewPassword("1234567891234567");
		user.setConfirmPassword("1234567891234567");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234567891234567")).andReturn("1234567891234567");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.password.large", mappingResult.getFieldError("newPassword").getCode());
	}
	
	@Test
	@DirtiesContext
	public void shouldRejectIfContainsSpecialChars() {
		user.setNewPassword(" 12o*-lala");
		user.setConfirmPassword(" 12o*-lala");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash(" 12o*-lala")).andReturn(" 12o*-lala");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.password.nonalphanumeric", mappingResult.getFieldError("newPassword").getCode());
	}
	
	@Test
	@DirtiesContext
	public void shouldRejectIfNewEmailAlreadyExists() {
		RegisteredUser existingUser = new RegisteredUserBuilder().id(2).username("email2@test.com").firstName("bob").lastName("bobson").
				email("email2@test.com").confirmPassword("12345678").newPassword("12345678").password("5f4dcc3b5aa").toUser();
		
		user.setEmail("email2@test.com");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email2@test.com")).andReturn(existingUser);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.email.alreadyexists", mappingResult.getFieldError("email").getCode());
	}
	
	@Test
	@DirtiesContext
	public void shouldNotRejectIfuserWithEmailExistsButIsCUrrentUser() {
		RegisteredUser existingUser = new RegisteredUserBuilder().id(1).username("email2@test.com").firstName("bob").lastName("bobson").
				email("email2@test.com").confirmPassword("12345678").newPassword("12345678").password("5f4dcc3b5aa").toUser();
		
		user.setEmail("email2@test.com");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email2@test.com")).andReturn(existingUser);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
		
	}
	
	@Test
	@DirtiesContext
	public void shouldRejectIfEmailNotValidEmail() {
		user.setEmail("notvalidemail");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("notvalidemail")).andReturn(null);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("You must enter a valid email address.", mappingResult.getFieldError("email").getDefaultMessage());
	}
	
	@Test
	@DirtiesContext
	public void shouldRejectIfFirstNameEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");
		user.setFirstName("");
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("firstName").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfLastNameNull() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "lastName");
		user.setLastName(null);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
		EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
		EasyMock.replay(userServiceMock, encryptionUtilsMock);
		accountValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastName").getCode());
	}
}
