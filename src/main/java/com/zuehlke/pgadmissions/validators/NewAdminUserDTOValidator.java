package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.NewAdminUserDTO;

public class NewAdminUserDTOValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return NewAdminUserDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newUserFirstName", "adminUser.firstName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newUserLastName", "adminUser.lastName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newUserEmail", "adminUser.email.notempty");
		
		if (!errors.hasFieldErrors("newUserEmail") && !EmailValidator.getInstance().isValid(((NewAdminUserDTO)target).getNewUserEmail())) {
			errors.rejectValue("newUserEmail", "adminUser.email.invalid");
		}
	}

}
