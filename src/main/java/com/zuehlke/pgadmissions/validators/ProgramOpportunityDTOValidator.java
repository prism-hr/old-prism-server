package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.dto.ProgramOpportunityDTO;

@Component
public class ProgramOpportunityDTOValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ProgramOpportunityDTO.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ProgramOpportunityDTO programAdvert = (ProgramOpportunityDTO) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", EMPTY_FIELD_ERROR_MESSAGE);
        validateStudyDuration(errors, programAdvert.getStudyDuration());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "active", EMPTY_DROPDOWN_ERROR_MESSAGE);
    }
    
}