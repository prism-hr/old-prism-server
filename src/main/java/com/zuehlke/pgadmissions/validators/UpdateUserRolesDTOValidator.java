package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.UpdateUserRolesDTO;

@Component
public class UpdateUserRolesDTOValidator extends AbstractValidator {

	@Override
	public boolean supports(Class<?> clazz) {		
		return clazz.isAssignableFrom(UpdateUserRolesDTO.class);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		UpdateUserRolesDTO userDTO = (UpdateUserRolesDTO) target;
		if( !(userDTO.getSelectedAuthorities().length == 1 && userDTO.getSelectedAuthorities()[0] == Authority.SUPERADMINISTRATOR)){
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedProgram", EMPTY_DROPDOWN_ERROR_MESSAGE);		
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedUser", EMPTY_DROPDOWN_ERROR_MESSAGE);
	}

}
