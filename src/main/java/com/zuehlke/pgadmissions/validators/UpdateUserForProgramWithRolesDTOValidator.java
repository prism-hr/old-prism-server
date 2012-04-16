package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.UpdateUserForProgramWithRolesDTO;

@Component
public class UpdateUserForProgramWithRolesDTOValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return UpdateUserForProgramWithRolesDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "setProgram", "selectedProgram.existingAdminUser.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "setUser", "selectedUser.existingAdminUser.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newSetRolesDTO", "selectedRoles.existingAdminUser.notempty");
	}
}
