package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class UserDTOValidatorTest {

    @Autowired  
    private Validator validator; 
    
	private UserDTOValidator userDTOValidator;
	
	private UserDTO user;
	
	@Before
	public void setup() {
		user = new UserDTO();
		user.setFirstName("bob");
		user.setLastName("Smith");
		user.setEmail("email@test.com");
		user.setSelectedProgram(new ProgramBuilder().id(4).build());
		user.setSelectedAuthorities(Authority.APPLICATION_REVIEWER);
		
		userDTOValidator = new UserDTOValidator();
		userDTOValidator.setValidator((javax.validation.Validator) validator);
	}

	@Test
	public void shouldSupportNewUserDTO() {
		assertTrue(userDTOValidator.supports(UserDTO.class));
	}

	@Test
	public void shouldNotRejectValidUser() {
		BindingResult mappingResult = new BeanPropertyBindingResult(user, "*");
		userDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	@Test
	public void shouldRejectIfFirstNameEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");
		user.setFirstName("");
		userDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("firstName").getCode());
	}

	@Test
	public void shouldRejectIfLastNameNull() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "lastName");
		user.setLastName(null);
		userDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastName").getCode());
	}

	@Test
	public void shouldRejectIfEmailNotValid() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		user.setEmail("");
		userDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.email.notvalid", mappingResult.getFieldError("email").getCode());
	}

	@Test
	public void shouldRejectIfProgramIsNullAndAuthoritiesNotSuperadminOnly() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedProgram");
		user.setSelectedProgram(null);
		user.setSelectedAuthorities(Authority.APPLICATION_REVIEWER, Authority.SYSTEM_ADMINISTRATOR);
		userDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("selectedProgram").getCode());
	}

	@Test
	public void shouldNotRejectIfProgramIsNullAndAuthoritiesIsSuperadmin() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedProgram");
		user.setSelectedProgram(null);
		user.setSelectedAuthorities(Authority.SYSTEM_ADMINISTRATOR);
		userDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());

	}

	@Test
	public void shouldRejectIfSelectedAuthoritiesAreNull() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedAuthorities");
		user.setSelectedAuthorities((Authority[]) null);
		userDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("selectedAuthorities").getCode());
	}

	@Test
	public void shouldRejectIfSelectedAuthoritiesAreEmpyt() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedAuthorities");
		user.setSelectedAuthorities(new Authority[] {});
		userDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("selectedAuthorities").getCode());
	}
}
