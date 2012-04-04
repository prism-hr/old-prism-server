package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.dto.NewAdminUserDTO;


public class NewAdminUserDTOValidatorTest {

	private NewAdminUserDTOValidator validator;
	private NewAdminUserDTO newAdminUserDTO;
	
	@Test
	public void shouldSupportAdminUser() {
		assertTrue(validator.supports(NewAdminUserDTO.class));
	}
	
	@Test
	public void shouldAcceptAdminUser() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(newAdminUserDTO, "adminUser");
		validator.validate(newAdminUserDTO, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectAdminUserIfNullFirstName() {
		newAdminUserDTO.setNewUserFirstName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(newAdminUserDTO, "adminUser");
		validator.validate(newAdminUserDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectAdminUserIfNullLastName() {
		newAdminUserDTO.setNewUserLastName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(newAdminUserDTO, "adminUser");
		validator.validate(newAdminUserDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectAdminUserIfNullEmail() {
		newAdminUserDTO.setNewUserEmail(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(newAdminUserDTO, "adminUser");
		validator.validate(newAdminUserDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectAdminUserIfInvalidEmail() {
		newAdminUserDTO.setNewUserEmail("test");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(newAdminUserDTO, "adminUser");
		validator.validate(newAdminUserDTO, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}

	@Before
	public void setup(){
		validator = new NewAdminUserDTOValidator();
		
		newAdminUserDTO = new NewAdminUserDTO();
		newAdminUserDTO.setNewUserEmail("test@gmail.com");
		newAdminUserDTO.setNewUserFirstName("test");
		newAdminUserDTO.setNewUserLastName("test");
	}
}
