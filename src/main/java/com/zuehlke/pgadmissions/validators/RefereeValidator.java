package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.services.UserService;

@Service
public class RefereeValidator extends FormSectionObjectValidator implements Validator {

	private static final int MAXIMUM_ADDRESS_CHARS = 500;
	private final UserService userService;

	RefereeValidator(){
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
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);
		Referee referee = (Referee) target;
		if (!EmailValidator.getInstance().isValid(referee.getEmail())) {
			errors.rejectValue("email", "text.email.notvalid");
		}
		if(userService.getCurrentUser().getEmail().equals(referee.getEmail())){
			errors.rejectValue("email", "text.email.notyourself");
		}
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressCountry", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLocation", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobEmployer", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobTitle", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "text.field.empty");
		
		if (referee.getAddressLocation() != null) {
			if (referee.getAddressLocation().length() > MAXIMUM_ADDRESS_CHARS) {
				errors.rejectValue("addressLocation", "user.refereeAddressLength.exceeded");
			}
		}
	}

}
