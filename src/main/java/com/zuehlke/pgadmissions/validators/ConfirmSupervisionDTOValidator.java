package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
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

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmedSupervision", "dropdown.radio.select.none");

        if (BooleanUtils.isFalse(dto.getConfirmedSupervision())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "declinedSupervisionReason", "text.field.empty");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectTitle", "text.field.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectAbstract", "text.field.empty");

        // recommended offer validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedStartDate", "text.field.empty");
        Date startDate = dto.getRecommendedStartDate();
        Date today = new Date();
        if (startDate != null && !startDate.after(today)) {
            errors.rejectValue("recommendedStartDate", "date.field.notfuture");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditionsAvailable", "dropdown.radio.select.none");

        if (BooleanUtils.isTrue(dto.getRecommendedConditionsAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditions", "text.field.empty");
        }
    }

}
