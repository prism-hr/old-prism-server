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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class RegisterFormValidatorTest {

    @Autowired
    private Validator validator;

    private RegisterFormValidator recordValidator;

    private UserService userServiceMock;

    private User user;

    @Test
    public void shouldSupportApplicantRecordValidator() {
        assertTrue(recordValidator.supports(User.class));
    }

    @Before
    public void setup() {
        user = new UserBuilder().id(4).userAccount(new UserAccount().withConfirmPassword("12345678").withPassword("12345678")).build();
        userServiceMock = EasyMock.createMock(UserService.class);
        recordValidator = new RegisterFormValidator(userServiceMock);
        recordValidator.setValidator((javax.validation.Validator) validator);
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfFirstNameIsEmpty() {
        user.setFirstName(null);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "firstName");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("firstName").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfLasttNameIsEmpty() {
        user.setLastName(null);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "lastName");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastName").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfEmailNotValidEmail() {
        user.setEmail("nonvalidemail");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "email");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("nonvalidemail")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter a valid email address.", mappingResult.getFieldError("email").getDefaultMessage());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfNoConfirmPassword() {
        user.getAccount().setConfirmPassword(null);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "confirmPassword");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("account.confirmPassword").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfPasswordsDoNotMatch() {
        user.getAccount().setConfirmPassword("12345");
        user.getAccount().setPassword("12345678");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "confirmPassword");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.passwords.notmatch", mappingResult.getFieldError("account.confirmPassword").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfPasswordLessThan8Chars() {
        user.getAccount().setPassword("12");
        user.getAccount().setConfirmPassword("12");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "password");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.password.small", mappingResult.getFieldError("account.password").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfPasswordMoreThan15Chars() {
        user.getAccount().setPassword("1234567891234567");
        user.getAccount().setConfirmPassword("1234567891234567");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "password");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.password.large", mappingResult.getFieldError("account.password").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldNotRejectIfContainsSpecialChars() {
        user.getAccount().setPassword(" 12o*-lala");
        user.getAccount().setConfirmPassword(" 12o*-lala");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "password");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    @DirtiesContext
    public void shouldAcceptPasswordWithOnlyChars() {
        user.getAccount().setPassword("oooooooooo");
        user.getAccount().setConfirmPassword("oooooooooo");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "password");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    @DirtiesContext
    public void shouldAcceptPasswordWithOnlyNumbersAndLettes() {
        user.getAccount().setPassword("ooo12ooo3oo1");
        user.getAccount().setConfirmPassword("ooo12ooo3oo1");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "password");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    @DirtiesContext
    public void shouldNotRejectIfPasswordsMatch() {
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "confirmPassword");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfEmailEmpty() {
        user.setEmail(null);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "email");

        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("email").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfEmailAlreadyExistAndUserIsNewuser() {
        user.setId(null);
        user.setEmail("meuston@gmail.com");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "email");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(new UserBuilder().id(5).build());
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.email.alreadyexists", mappingResult.getFieldError("email").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfEmailAlreadyExistAndUserIsExistingUserButNotUserWithEmail() {

        user.setEmail("meuston@gmail.com");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "email");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(new UserBuilder().id(5).build());
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.email.alreadyexists", mappingResult.getFieldError("email").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldNotRejectIfEmailAlreadyExistAndUserIsExistingUserAndIsUserWithEmail() {

        user.setEmail("meuston@gmail.com");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(user, "email");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }
}
