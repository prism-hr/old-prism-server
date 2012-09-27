package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class SuperadminUserDTOValidatorTest {

	private UserDTO user;
	
	@Autowired
	private SuperadminUserDTOValidator validator;
	
	@Autowired
	private UserService userServiceMock;

	@Before
	public void setup() {
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("email@test.com")).andReturn(null).anyTimes();
		EasyMock.replay(userServiceMock);

		user = new UserDTO();
		user.setFirstName("bob");
		user.setLastName("Smith");
		user.setEmail("email@test.com");
		user.setSelectedProgram(new ProgramBuilder().id(4).toProgram());
		user.setSelectedAuthorities(Authority.REVIEWER);
	}

	@Test
	@DirtiesContext
	public void shouldSupportNewUserDTO() {
		assertTrue(validator.supports(UserDTO.class));
	}

	@Test
	@DirtiesContext
	public void shouldNotRejectValidUser() {
		BindingResult mappingResult = new BeanPropertyBindingResult(user, "*");
		validator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfFirstNameEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");
		user.setFirstName("");
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("firstName").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfLastNameNull() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "lastName");
		user.setLastName(null);
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastName").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfEmailNotValid() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		user.setEmail("");
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.email.notvalid", mappingResult.getFieldError("email").getCode());
	}	
	
	@Test
	@DirtiesContext
	@Ignore
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
