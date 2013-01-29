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
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UpdateUserRolesDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class UpdateUserRolesDTOValidatorTest {

    @Autowired  
    private Validator validator;  
    
    private UpdateUserRolesDTOValidator updateUserRolesDTOValidator;
    
	private UpdateUserRolesDTO user;

	@Before
	public void setup() {
		user = new UpdateUserRolesDTO();
		user.setSelectedUser(new RegisteredUserBuilder().id(5).build());
		user.setSelectedProgram(new ProgramBuilder().id(4).build());
		user.setSelectedAuthorities(Authority.REVIEWER);
		
		updateUserRolesDTOValidator = new UpdateUserRolesDTOValidator();
		updateUserRolesDTOValidator.setValidator((javax.validation.Validator) validator);
	}

	@Test
	public void shouldSupportUpdateUserRolesDTO() {
		assertTrue(updateUserRolesDTOValidator.supports(UpdateUserRolesDTO.class));
	}

	@Test
	public void shouldNotRejectValidUser() {
		BindingResult mappingResult = new BeanPropertyBindingResult(user, "*");
		updateUserRolesDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	@Test
	public void shouldRejectIfUserEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedUser");
		user.setSelectedUser(null);
		updateUserRolesDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("selectedUser").getCode());
	}

		
	@Test
	public void shouldRejectIfProgramIsNullAndAuthoritiesNotSuperadminOnly() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedProgram");
		user.setSelectedProgram(null);
		user.setSelectedAuthorities(Authority.REVIEWER, Authority.SUPERADMINISTRATOR);
		updateUserRolesDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("selectedProgram").getCode());
	}
	@Test
	public void shouldNotRejectIfProgramIsNullAndAuthoritiesIsSuperadmin() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedProgram");
		user.setSelectedProgram(null);
		user.setSelectedAuthorities(Authority.SUPERADMINISTRATOR);
		updateUserRolesDTOValidator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
		
	}
	
	
}
