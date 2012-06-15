package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.NewUserDTO;

public class NewUserDTOValidatorTest {

	private NewUserDTOValidator validator;
	private NewUserDTO user;

	@Before
	public void setup() {
		validator = new  NewUserDTOValidator();
		user = new NewUserDTO();
		user.setFirstName("bob");
		user.setLastName("Smith");
		user.setEmail("email@test.com");
		user.setSelectedProgram(new ProgramBuilder().id(4).toProgram());
		user.setSelectedAuthorities(Authority.REVIEWER);
	}

	@Test
	public void shouldSupportNewUserDTO() {
		assertTrue(validator.supports(NewUserDTO.class));
	}

	@Test
	public void shouldNotRejectValidUser() {
		BindingResult mappingResult = new BeanPropertyBindingResult(user, "*");
		validator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	@Test
	public void shouldRejectIfFirstNameEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "firstName");
		user.setFirstName("");
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("firstName").getCode());
	}

	@Test
	public void shouldRejectIfLastNameNull() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "lastName");
		user.setLastName(null);
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastName").getCode());
	}

	@Test
	public void shouldRejectIfEmailNotValid() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "email");
		user.setEmail("");
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.email.notvalid", mappingResult.getFieldError("email").getCode());
	}
	
	
	@Test
	public void shouldRejectIfProgramIsNullAndAuthoritiesNotSuperadminOnly() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedProgram");
		user.setSelectedProgram(null);
		user.setSelectedAuthorities(Authority.REVIEWER, Authority.SUPERADMINISTRATOR);
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("selectedProgram").getCode());
	}
	@Test
	public void shouldNotRejectIfProgramIsNullAndAuthoritiesIsSuperadmin() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedProgram");
		user.setSelectedProgram(null);
		user.setSelectedAuthorities(Authority.SUPERADMINISTRATOR);
		validator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
		
	}
	
	
	@Test
	public void shouldRejectIfSelectedAuthoritiesAreNull() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedAuthorities");
		user.setSelectedAuthorities((Authority[])null);
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("selectedAuthorities").getCode());
	}
}
