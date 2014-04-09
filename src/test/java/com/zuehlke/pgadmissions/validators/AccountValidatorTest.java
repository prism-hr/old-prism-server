package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class AccountValidatorTest {

    @Autowired
    private Validator validator;

    private AccountValidator accountValidator;

    private EncryptionUtils encryptionUtilsMock;

    private UserService userServiceMock;

    private User user;

    private User currentUser;

    @Before
    public void setup() {
        userServiceMock = EasyMock.createMock(UserService.class);
        encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
        user = new UserBuilder().id(1).username("email").firstName("bob").lastName("bobson").email("email@test.com").confirmPassword("12345678")
                .newPassword("12345678").password("5f4dcc3b5aa").build();
        currentUser = new UserBuilder().id(1).username("email").firstName("bob").lastName("bobson").email("email@test.com")
                .confirmPassword("12345678").newPassword("12345678").password("5f4dcc3b5aa").build();
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);

        accountValidator = new AccountValidator(userServiceMock, encryptionUtilsMock);
        accountValidator.setValidator((javax.validation.Validator) validator);
    }

    @Test
    public void shouldSupportApplicantRecordValidator() {
        assertTrue(accountValidator.supports(User.class));
    }

    @Test
    public void shouldRejectIfNewPasswordIsNotSetAndCurrentAndConfirmAreSet() {
        user.setNewPassword(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.replay(userServiceMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("newPassword").getCode());
    }

    @Test
    public void shouldRejectIfCurrentPasswordIsNotSetAndConfirmAndNewPasswordAreSet() {
        user.setPassword(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.replay(userServiceMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("password").getCode());
    }

    @Test
    public void shouldRejectIfConfirmPasswordIsNotSetAndCurrentAndNewPasswordAreSet() {
        user.setConfirmPassword(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "confirmPassword");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.replay(userServiceMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("confirmPassword").getCode());
    }

    @Test
    public void shouldRejectIfCurrentPasswordNotSameWithExisting() {
        user.setPassword("12345678");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("25d55ad283aa400af464c76d713c07ad");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("25d55ad283aa400af464c76d713c07ad");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("account.currentpassword.notmatch", mappingResult.getFieldError("password").getCode());
    }

    @Test
    @Ignore
    public void shouldRejectIfNewAndConfirmPasswordsNotSame() {
        user.setConfirmPassword("password");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
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
    public void shouldRejectIfNewPasswordIsLessThan8Chars() {
        user.setNewPassword("1234");
        user.setConfirmPassword("1234");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
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
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234567891234567")).andReturn("1234567891234567");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.password.large", mappingResult.getFieldError("newPassword").getCode());
    }

    @Test
    public void shouldNotRejectIfContainsSpecialChars() {
        user.setNewPassword(" 12o*-lala");
        user.setConfirmPassword(" 12o*-lala");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "newPassword");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash(" 12o*-lala")).andReturn(" 12o*-lala");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldRejectIfNewEmailAlreadyExists() {
        User existingUser = new UserBuilder().id(2).username("email2@test.com").firstName("bob").lastName("bobson")
                .email("email2@test.com").confirmPassword("12345678").newPassword("12345678").password("5f4dcc3b5aa").build();

        user.setEmail("email2@test.com");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email2@test.com")).andReturn(existingUser);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.email.alreadyexists", mappingResult.getFieldError("email").getCode());
    }

    @Test
    public void shouldNotRejectIfuserWithEmailExistsButIsCUrrentUser() {
        User existingUser = new UserBuilder().id(1).username("email2@test.com").firstName("bob").lastName("bobson")
                .email("email2@test.com").confirmPassword("12345678").newPassword("12345678").password("5f4dcc3b5aa").build();
        user.setEmail("email2@test.com");

        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");

        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email2@test.com")).andReturn(existingUser);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);

        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(mappingResult.getAllErrors().toString(), 0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldRejectIfEmailNotValidEmail() {
        user.setEmail("notvalidemail");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("notvalidemail")).andReturn(null);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter a valid email address.", mappingResult.getFieldError("email").getDefaultMessage());
    }

    @Test
    public void shouldRejectIfEmailIsEmpty() {
        user.setEmail(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("email").getCode());
    }

    @Test
    public void shouldRejectIfFirstNameEmpty() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");
        user.setFirstName("");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
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
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastName").getCode());
    }
}
