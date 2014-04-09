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
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.User;
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
        user = new UserBuilder().id(4).username("email").firstName("Hans-Peter").lastName("Mueller").email("meuston@gmail.com")
                .confirmPassword("12345678").password("12345678").build();
        userServiceMock = EasyMock.createMock(UserService.class);
        recordValidator = new RegisterFormValidator(userServiceMock);
        recordValidator.setValidator((javax.validation.Validator) validator);
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfFirstNameIsEmpty() {
        user.setFirstName(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");

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
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "lastName");

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
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("nonvalidemail")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter a valid email address.", mappingResult.getFieldError("email").getDefaultMessage());
    }
    
    @Test
    @DirtiesContext
    public void shouldRejectIfNoConfirmPassword() {
        user.setConfirmPassword(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "confirmPassword");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("confirmPassword").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfPasswordsDoNotMatch() {
        user.setConfirmPassword("12345");
        user.setPassword("12345678");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "confirmPassword");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.passwords.notmatch", mappingResult.getFieldError("confirmPassword").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfPasswordLessThan8Chars() {
        user.setPassword("12");
        user.setConfirmPassword("12");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.password.small", mappingResult.getFieldError("password").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfPasswordMoreThan15Chars() {
        user.setPassword("1234567891234567");
        user.setConfirmPassword("1234567891234567");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("user.password.large", mappingResult.getFieldError("password").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldNotRejectIfContainsSpecialChars() {
        user.setPassword(" 12o*-lala");
        user.setConfirmPassword(" 12o*-lala");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    @DirtiesContext
    public void shouldAcceptPasswordWithOnlyChars() {
        user.setPassword("oooooooooo");
        user.setConfirmPassword("oooooooooo");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    @DirtiesContext
    public void shouldAcceptPasswordWithOnlyNumbersAndLettes() {
        user.setPassword("ooo12ooo3oo1");
        user.setConfirmPassword("ooo12ooo3oo1");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "password");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    @DirtiesContext
    public void shouldNotRejectIfPasswordsMatch() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "confirmPassword");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfEmailEmpty() {
        user.setEmail(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");

        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("email").getCode());
    }

    @Test
    @DirtiesContext
    public void shouldRejectIfEmailAlreadyExistAndUserIsNewuser() {
        user.setId(null);
        user.setEmail("meuston@gmail.com");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");

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
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");

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
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("meuston@gmail.com")).andReturn(user);
        EasyMock.replay(userServiceMock);
        recordValidator.validate(user, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }
}
