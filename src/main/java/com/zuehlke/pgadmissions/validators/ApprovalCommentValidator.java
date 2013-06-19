package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.ApprovalComment;

@Component
public class ApprovalCommentValidator extends AbstractValidator {

    private static final int MAX_ABSTRACT_WORD_COUNT = 200;

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
        
        String projectAbstract = approvalComment.getProjectAbstract();
        if (projectAbstract != null) {
            int wordCount = countWords(projectAbstract);
            if (wordCount > MAX_ABSTRACT_WORD_COUNT) {
                errors.rejectValue("projectAbstract", "text.field.maxwords", new Object[] { MAX_ABSTRACT_WORD_COUNT }, null);
            }
        }

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

    private int countWords(String text) {
        return StringUtils.split(text, "\t\n\r ").length;
    }
}
