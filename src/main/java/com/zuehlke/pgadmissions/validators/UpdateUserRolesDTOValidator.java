package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UpdateUserRolesDTO;

@Component
public class UpdateUserRolesDTOValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {		
		return clazz.isAssignableFrom(UpdateUserRolesDTO.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UpdateUserRolesDTO userDTO = (UpdateUserRolesDTO) target;
		if( !(userDTO.getSelectedAuthorities().length == 1 && userDTO.getSelectedAuthorities()[0] == Authority.SUPERADMINISTRATOR)){
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedProgram", "dropdown.radio.select.none");		
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedUser", "dropdown.radio.select.none");
	}

}
