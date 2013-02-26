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

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class NewUserByAdminValidatorTest {
    
    @Autowired  
    private Validator validator;  
    
    private NewUserByAdminValidator newUserByAdminValidator;
	
    private RegisteredUser user;
	
	private UserService userServiceMock;

	@Before
	public void setup() {
	    userServiceMock = EasyMock.createMock(UserService.class);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@bla.com")).andReturn(null).anyTimes();
		EasyMock.replay(userServiceMock);
		user = new RegisteredUserBuilder().firstName("first").lastName("last").email("email@bla.com").build();
		
		newUserByAdminValidator = new NewUserByAdminValidator();
		newUserByAdminValidator.setValidator((javax.validation.Validator) validator);
	}

	@Test
	public void shouldSupportRegisteredUser() {
		assertTrue(newUserByAdminValidator.supports(RegisteredUser.class));
	}

	@Test
	public void shouldNotRejectValidUser() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");
		newUserByAdminValidator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	@Test
	public void shouldRejectIfFirstNameEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");
		user.setFirstName("");
		newUserByAdminValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("firstName").getCode());
	}

	@Test
	public void shouldRejectIfLastNameNull() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "lastName");
		user.setLastName(null);
		newUserByAdminValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastName").getCode());
	}

	@Test
	public void shouldRejectIfEmailEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		user.setEmail("");
		newUserByAdminValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("email").getCode());
	}
	
	@Test
	@Ignore
	public void shouldRejectIfEmailThatOfExistingApplicant() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		user.setEmail("applicant@test.com");
		EasyMock.reset(userServiceMock);
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("applicant@test.com"))
				.andReturn(new RegisteredUserBuilder().id(1).roles(new RoleBuilder().id(1).authorityEnum(Authority.APPLICANT).build()).build()).anyTimes();
		EasyMock.replay(userServiceMock);
		newUserByAdminValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.email.applicant", mappingResult.getFieldError("email").getCode());
	}
}
