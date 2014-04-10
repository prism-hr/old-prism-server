package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.dto.UpdateUserForProgramWithRolesDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class UpdateUserForProgramWithRolesDTOValidatorTest {

    @Autowired  
    private Validator validator;  
    
	private UpdateUserForProgramWithRolesDTOValidator updateUserForProgramWithRolesDTOValidator;
    
	private UpdateUserForProgramWithRolesDTO dto;
	
	@Test
	public void shouldSupportAdminUser() {
		assertTrue(updateUserForProgramWithRolesDTOValidator.supports(UpdateUserForProgramWithRolesDTO.class));
	}
	
	@Test
	public void shouldAcceptValidDTO() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(dto, "existingAdminUser");
		updateUserForProgramWithRolesDTOValidator.validate(dto, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfNoSelectedProgram() {
		dto.setSelectedProgram(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(dto, "existingAdminUser");
		updateUserForProgramWithRolesDTOValidator.validate(dto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfNoSelectedUser() {
		dto.setSelectedUser(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(dto, "existingAdminUser");
		updateUserForProgramWithRolesDTOValidator.validate(dto, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Before
	public void setup() {
		dto = new UpdateUserForProgramWithRolesDTO();
		dto.setSelectedProgram(new Program());
		dto.setSelectedUser(new User());
		
		updateUserForProgramWithRolesDTOValidator = new UpdateUserForProgramWithRolesDTOValidator();
		updateUserForProgramWithRolesDTOValidator.setValidator((javax.validation.Validator) validator);
	}
}
