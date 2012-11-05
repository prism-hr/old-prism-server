package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.services.UserService;

@Service
public class RefereeValidator extends FormSectionObjectValidator implements Validator {

	private final UserService userService;

	RefereeValidator() {
		this(null);
	}

	@Autowired
	public RefereeValidator(UserService userService) {
		this.userService = userService;

	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Referee.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		super.addExtraValidation(target, errors);
		
		Referee referee = (Referee) target;
		if (userService.getCurrentUser().getEmail().equals(referee.getEmail())) {
			errors.rejectValue("email", "text.email.notyourself");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLocation", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobEmployer", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobTitle", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "text.field.empty");
		
		if (referee.getAddressLocation() != null && StringUtils.isBlank(referee.getAddressLocation().getAddress1())) {
			errors.rejectValue("addressLocation.address1", "text.field.empty");
		}
		if (referee.getAddressLocation() != null && StringUtils.isBlank(referee.getAddressLocation().getAddress3())) {
			errors.rejectValue("addressLocation.address3", "text.field.empty");
		}
		if (referee.getAddressLocation() != null && referee.getAddressLocation().getCountry()==null) {
			errors.rejectValue("addressLocation.country", "text.field.empty");
		}
	}
}
