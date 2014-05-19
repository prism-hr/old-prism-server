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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class UserValidatorTest {

    @Autowired
    private Validator validator;

    private UserAccountValidator accountValidator;

    private EncryptionUtils encryptionUtilsMock;

    private UserService userServiceMock;

    private User user;

    private User currentUser;

    @Before
    public void setup() {
        userServiceMock = EasyMock.createMock(UserService.class);
        encryptionUtilsMock = EasyMock.createMock(EncryptionUtils.class);
        user = new User().withId(1).withAccount(new UserAccount().withNewPassword("12345678").withPassword("5f4dcc3b5aa"));
        currentUser = new User().withId(1)
                .withAccount(new UserAccount().withConfirmPassword("12345678").withNewPassword("12345678").withPassword("5f4dcc3b5aa"));
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);

        accountValidator = new UserAccountValidator(userServiceMock, encryptionUtilsMock);
        accountValidator.setValidator((javax.validation.Validator) validator);
    }

    @Test
    public void shouldSupportApplicantRecordValidator() {
        assertTrue(accountValidator.supports(User.class));
    }

    @Test
    public void shouldRejectIfNewPasswordIsNotSetAndCurrentAndConfirmAreSet() {
        user.getAccount().setNewPassword(null);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "newPassword");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.replay(userServiceMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("account.newPassword").getCode());
    }

    @Test
    public void shouldRejectIfCurrentPasswordIsNotSetAndConfirmAndNewPasswordAreSet() {
        user.getAccount().setPassword(null);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "password");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.replay(userServiceMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("account.password").getCode());
    }

    @Test
    public void shouldRejectIfConfirmPasswordIsNotSetAndCurrentAndNewPasswordAreSet() {
        user.getAccount().setConfirmPassword(null);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "confirmPassword");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.replay(userServiceMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("account.confirmPassword").getCode());
    }

    @Test
    public void shouldRejectIfCurrentPasswordNotSameWithExisting() {
        user.getAccount().setPassword("12345678");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "password");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("25d55ad283aa400af464c76d713c07ad");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("25d55ad283aa400af464c76d713c07ad");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("account.currentpassword.notmatch", mappingResult.getFieldError("account.password").getCode());
    }

    @Test
    @Ignore
    public void shouldRejectIfNewAndConfirmPasswordsNotSame() {
        user.getAccount().setConfirmPassword("password");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "newPassword");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("12345678")).andReturn("12345678");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("user.passwords.notmatch", mappingResult.getFieldError("account.newPassword").getCode());
        Assert.assertEquals("user.passwords.notmatch", mappingResult.getFieldError("account.confirmPassword").getCode());
    }

    @Test
    public void shouldRejectIfNewPasswordIsLessThan8Chars() {
        user.getAccount().setNewPassword("1234");
        user.getAccount().setConfirmPassword("1234");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "newPassword");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234")).andReturn("1234");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.password.small", mappingResult.getFieldError("account.newPassword").getCode());
    }

    @Test
    public void shouldRejectIfNewPasswordIsMoreThan15Chars() {
        user.getAccount().setNewPassword("1234567891234567");
        user.getAccount().setConfirmPassword("1234567891234567");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "newPassword");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("1234567891234567")).andReturn("1234567891234567");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.password.large", mappingResult.getFieldError("account.newPassword").getCode());
    }

    @Test
    public void shouldNotRejectIfContainsSpecialChars() {
        user.getAccount().setNewPassword(" 12o*-lala");
        user.getAccount().setConfirmPassword(" 12o*-lala");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "newPassword");
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
        User existingUser = new User().withId(2).withEmail("email2@test.com")
                .withAccount(new UserAccount().withConfirmPassword("12345678").withNewPassword("12345678").withPassword("5f4dcc3b5aa"));

        user.setEmail("email2@test.com");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "email");
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
        User existingUser = new User().withId(1).withEmail("email2@test.com").withAccount(new UserAccount().withConfirmPassword("12345678").withNewPassword("12345678")
                .withPassword("5f4dcc3b5aa"));
        user.setEmail("email2@test.com");

        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "email");

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
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "email");
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
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "email");
        EasyMock.expect(userServiceMock.getById(1)).andReturn(user);
        EasyMock.expect(encryptionUtilsMock.getMD5Hash("5f4dcc3b5aa")).andReturn("5f4dcc3b5aa");
        EasyMock.replay(userServiceMock, encryptionUtilsMock);
        accountValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("email").getCode());
    }

    @Test
    public void shouldRejectIfFirstNameEmpty() {
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "firstName");
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
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "lastName");
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
