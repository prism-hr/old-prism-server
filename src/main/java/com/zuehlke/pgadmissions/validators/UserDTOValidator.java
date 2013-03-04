package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;

@Component
public class UserDTOValidator extends AbstractValidator {

	public UserDTOValidator() {
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(UserDTO.class);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		UserDTO user = (UserDTO) target;
		if (!(user.getSelectedAuthorities().length == 1 && user.getSelectedAuthorities()[0] == Authority.SUPERADMINISTRATOR)) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedProgram", EMPTY_DROPDOWN_ERROR_MESSAGE);
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", EMPTY_FIELD_ERROR_MESSAGE);

		if (user.getSelectedAuthorities() == null || user.getSelectedAuthorities().length == 0) {
			errors.rejectValue("selectedAuthorities", EMPTY_DROPDOWN_ERROR_MESSAGE);
		}
		
		if (!EmailValidator.getInstance().isValid(user.getEmail())) {
			errors.rejectValue("email", "text.email.notvalid");
		}
	}
}
