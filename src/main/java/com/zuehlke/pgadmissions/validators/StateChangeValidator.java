package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.InterviewEvaluationComment;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

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
        
        ApplicationFormStatus nextStatus = comment.getNextStatus();
        boolean stateChangeRequiresFastTrack = !(ApplicationFormStatus.APPROVED.equals(nextStatus)||ApplicationFormStatus.REJECTED.equals(nextStatus));
        boolean fastrackValueMissing = comment.getFastTrackApplication() == null && comment.getApplication().getBatchDeadline() != null;        
        
        if(stateChangeRequiresFastTrack&&fastrackValueMissing){
            errors.rejectValue("fastTrackApplication", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
        if (comment.getNextStatus() == null) {
            errors.rejectValue("nextStatus", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        if (BooleanUtils.isNotTrue(comment.getConfirmNextStage())) {
            errors.rejectValue("confirmNextStage", MANDATORY_CHECKBOX);
        }
    }

}
