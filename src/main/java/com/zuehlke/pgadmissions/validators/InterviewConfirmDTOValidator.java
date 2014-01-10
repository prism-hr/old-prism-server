package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.dto.InterviewConfirmDTO;

@Component
public class InterviewConfirmDTOValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return InterviewConfirmDTO.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "timeslotId", EMPTY_DROPDOWN_ERROR_MESSAGE);
    }

}
