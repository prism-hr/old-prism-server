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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedProgram", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedUser", "dropdown.radio.select.none");
	}
}
