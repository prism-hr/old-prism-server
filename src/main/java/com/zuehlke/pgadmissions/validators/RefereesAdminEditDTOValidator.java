package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;

@Component
public class RefereesAdminEditDTOValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return RefereesAdminEditDTO.class.equals(clazz);
    }
    
    @Override
    protected void addExtraValidation(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "text.field.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "telephone", "text.field.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "text.field.empty");
    }
}
