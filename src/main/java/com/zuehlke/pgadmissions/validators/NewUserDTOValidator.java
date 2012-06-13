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
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedProgram", "newuser.program.notempty");		
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "user.firstName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "user.lastName.notempty");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedAuthorities", "newuser.selectedAuthorities.notempty");

		if (!EmailValidator.getInstance().isValid(user.getEmail())) {
			errors.rejectValue("email", "user.email.invalid");
		}
	}

}
