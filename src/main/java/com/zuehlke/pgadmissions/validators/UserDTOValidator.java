package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserDTOValidator implements Validator {

	private final UserService userService;

	UserDTOValidator() {
		this(null);
	}

	@Autowired
	public UserDTOValidator(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(UserDTO.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserDTO user = (UserDTO) target;
		if (!(user.getSelectedAuthorities().length == 1 && user.getSelectedAuthorities()[0] == Authority.SUPERADMINISTRATOR)) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedProgram", "dropdown.radio.select.none");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "text.field.empty");

		if (user.getSelectedAuthorities() == null || user.getSelectedAuthorities().length == 0) {
			errors.rejectValue("selectedAuthorities", "dropdown.radio.select.none");
		}
		if (StringUtils.isNotBlank(user.getEmail())) {
			RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(user.getEmail());
			if (existingUser != null && existingUser.isInRole(Authority.APPLICANT)) {
				errors.rejectValue("email", "text.email.applicant", new Object[]{user.getEmail()}, "Cannot add applicant to role");
			}
		}
		if (!EmailValidator.getInstance().isValid(user.getEmail())) {
			errors.rejectValue("email", "text.email.notvalid");
		}
	}
}
