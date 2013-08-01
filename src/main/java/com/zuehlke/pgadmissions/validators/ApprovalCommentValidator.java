package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.ApprovalComment;

@Component
public class ApprovalCommentValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ApprovalComment.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ApprovalComment approvalComment = (ApprovalComment) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectDescriptionAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectTitle", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectAbstract", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedStartDate", EMPTY_FIELD_ERROR_MESSAGE);
        
        Date startDate = approvalComment.getRecommendedStartDate();
        Date today = new Date();
        if (startDate != null && !startDate.after(today)) {
            errors.rejectValue("recommendedStartDate", "date.field.notfuture");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditionsAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (BooleanUtils.isTrue(approvalComment.getRecommendedConditionsAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditions", EMPTY_FIELD_ERROR_MESSAGE);
        }
    }

}
