package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.dto.UpdateUserForProgramWithRolesDTO;

@Component
public class UpdateUserForProgramWithRolesDTOValidator extends AbstractValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return UpdateUserForProgramWithRolesDTO.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedProgram", EMPTY_DROPDOWN_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedUser", EMPTY_DROPDOWN_ERROR_MESSAGE);
	}
}
