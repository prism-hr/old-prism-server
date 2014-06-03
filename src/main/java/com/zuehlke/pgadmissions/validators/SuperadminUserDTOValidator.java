package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.dto.UserDTO;

@Component
public class SuperadminUserDTOValidator extends AbstractValidator {

	public SuperadminUserDTOValidator() {
	}

	@Override
	public boolean supports(Class<?> clazz) {		
		return clazz.isAssignableFrom(UserDTO.class);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		UserDTO user = (UserDTO) target;	
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", EMPTY_FIELD_ERROR_MESSAGE);		
		
		if (!EmailValidator.getInstance().isValid(user.getEmail())) {
			errors.rejectValue("email", "text.email.notvalid");
		}
	}
}
