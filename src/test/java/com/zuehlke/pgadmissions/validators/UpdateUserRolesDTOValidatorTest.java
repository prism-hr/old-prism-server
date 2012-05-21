package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UpdateUserRolesDTO;

public class UpdateUserRolesDTOValidatorTest {

	private UpdateUserRolesDTOValidator validator;
	private UpdateUserRolesDTO user;

	@Before
	public void setup() {
		validator = new  UpdateUserRolesDTOValidator();
		user = new UpdateUserRolesDTO();
		user.setSelectedUser(new RegisteredUserBuilder().id(5).toUser());
		user.setSelectedProgram(new ProgramBuilder().id(4).toProgram());
		user.setSelectedAuthorities(Authority.REVIEWER);
	}

	@Test
	public void shouldSupportUpdateUserRolesDTO() {
		assertTrue(validator.supports(UpdateUserRolesDTO.class));
	}

	@Test
	public void shouldNotRejectValidUser() {
		BindingResult mappingResult = new BeanPropertyBindingResult(user, "*");
		validator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	@Test
	public void shouldRejectIfUserEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedUser");
		user.setSelectedUser(null);
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("updateuser.selectedUser.notempty", mappingResult.getFieldError("selectedUser").getCode());
	}

		
	@Test
	public void shouldRejectIfProgramIsNullAndAuthoritiesNotSuperadminOnly() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedProgram");
		user.setSelectedProgram(null);
		user.setSelectedAuthorities(Authority.REVIEWER, Authority.SUPERADMINISTRATOR);
		validator.validate(user, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("updateuser.program.notempty", mappingResult.getFieldError("selectedProgram").getCode());
	}
	@Test
	public void shouldNotRejectIfProgramIsNullAndAuthoritiesIsSuperadmin() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(user, "selectedProgram");
		user.setSelectedProgram(null);
		user.setSelectedAuthorities(Authority.SUPERADMINISTRATOR);
		validator.validate(user, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
		
	}
	
	
}
