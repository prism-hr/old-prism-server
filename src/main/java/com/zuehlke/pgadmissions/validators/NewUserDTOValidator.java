package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.NewUserDTO;
import com.zuehlke.pgadmissions.dto.UpdateUserRolesDTO;

@Component
public class NewUserDTOValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {		
		return clazz.isAssignableFrom(NewUserDTO.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		NewUserDTO user = (NewUserDTO) target;	
		if( !(user.getSelectedAuthorities().length == 1 && user.getSelectedAuthorities()[0] == Authority.SUPERADMINISTRATOR)){
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedProgram", "dropdown.radio.select.none");		
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "text.field.empty");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedAuthorities", "dropdown.radio.select.none");

		if (!EmailValidator.getInstance().isValid(user.getEmail())) {
			errors.rejectValue("email", "text.email.notvalid");
		}
	}

}
