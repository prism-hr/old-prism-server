package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;

@Component
public class ConfirmSupervisionDTOValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ConfirmSupervisionDTO.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {

        ConfirmSupervisionDTO dto = (ConfirmSupervisionDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmedSupervision", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (BooleanUtils.isFalse(dto.getConfirmedSupervision())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "declinedSupervisionReason", EMPTY_FIELD_ERROR_MESSAGE);
        } 

        if (BooleanUtils.isTrue(dto.getConfirmedSupervision())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectTitle", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectAbstract", EMPTY_FIELD_ERROR_MESSAGE);
        
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedStartDate", EMPTY_FIELD_ERROR_MESSAGE);
            LocalDate startDate = dto.getRecommendedStartDate();
            LocalDate today = new LocalDate();
            if (startDate != null && !startDate.isAfter(today)) {
                errors.rejectValue("recommendedStartDate", "date.field.notfuture");
            }
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditionsAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);

            if (BooleanUtils.isTrue(dto.getRecommendedConditionsAvailable())) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditions", EMPTY_FIELD_ERROR_MESSAGE);
            }
        }
    }
}
