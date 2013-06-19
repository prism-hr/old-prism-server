package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;

@Component
public class StateChangeValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ValidationComment.class) || clazz.isAssignableFrom(InterviewEvaluationComment.class)
                || clazz.isAssignableFrom(StateChangeComment.class);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        if (target instanceof ValidationComment) {
            ValidationComment comment = (ValidationComment) target;
            if (comment.getQualifiedForPhd() == null) {
                errors.rejectValue("qualifiedForPhd", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }
            if (comment.getEnglishCompentencyOk() == null) {
                errors.rejectValue("englishCompentencyOk", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }
            if (comment.getHomeOrOverseas() == null) {
                errors.rejectValue("homeOrOverseas", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }
        }

        StateChangeComment comment = (StateChangeComment) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
        if (comment.getNextStatus() == null) {
            errors.rejectValue("nextStatus", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        if (BooleanUtils.isNotTrue(comment.getConfirmNextStage())) {
            errors.rejectValue("confirmNextStage", MANDATORY_CHECKBOX);
        }
    }

}
