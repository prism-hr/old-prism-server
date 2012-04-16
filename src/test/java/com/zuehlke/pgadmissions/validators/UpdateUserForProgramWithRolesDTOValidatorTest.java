package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.UpdateUserForProgramWithRolesDTO;


public class UpdateUserForProgramWithRolesDTOValidatorTest {

	private UpdateUserForProgramWithRolesDTOValidator validator;
	private UpdateUserForProgramWithRolesDTO dto;
	
	@Test
	public void shouldSupportAdminUser() {
		assertTrue(validator.supports(UpdateUserForProgramWithRolesDTO.class));
	}
	
	@Test
	public void shouldAcceptValidDTO() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(dto, "existingAdminUser");
		validator.validate(dto, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfNoSelectedProgram() {
		dto.setSelectedProgram(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(dto, "existingAdminUser");
		validator.validate(dto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfNoSelectedUser() {
		dto.setSelectedUser(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(dto, "existingAdminUser");
		validator.validate(dto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Before
	public void setup() {
		validator = new UpdateUserForProgramWithRolesDTOValidator();
		dto = new UpdateUserForProgramWithRolesDTO();
		dto.setSelectedProgram(new Program());
		dto.setSelectedUser(new RegisteredUser());
	}
}
