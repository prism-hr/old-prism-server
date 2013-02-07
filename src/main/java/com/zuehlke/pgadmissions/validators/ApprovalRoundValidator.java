package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.ApprovalRound;

@Component
public class ApprovalRoundValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ApprovalRound.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {

        ApprovalRound approvalRound = (ApprovalRound) target;

        if (approvalRound.getSupervisors().isEmpty()) {
            errors.rejectValue("supervisors", "dropdown.radio.select.none");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectDescriptionAvailable", "dropdown.radio.select.none");

        if (BooleanUtils.isTrue(approvalRound.getProjectDescriptionAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectTitle", "text.field.empty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectAbstract", "text.field.empty");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedStartDate", "text.field.empty");
        Date startDate = approvalRound.getRecommendedStartDate();
        Date today = new Date();
        if (startDate != null && !startDate.after(today)) {
            errors.rejectValue("recommendedStartDate", "date.field.notfuture");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditionsAvailable", "dropdown.radio.select.none");

        if (BooleanUtils.isTrue(approvalRound.getRecommendedConditionsAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditions", "text.field.empty");
        }

    }

}
