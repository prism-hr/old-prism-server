package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.AdmitterComment;

@Component
public class AdmitterCommentValidator extends AbstractValidator {
    
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(AdmitterComment.class);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        if (target instanceof AdmitterComment) {
            AdmitterComment comment = (AdmitterComment) target;
            if (comment.getQualifiedForPhd() == null) {
                errors.rejectValue("qualifiedForPhd", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }
            if (comment.getEnglishCompetencyOk() == null) {
                errors.rejectValue("englishCompentencyOk", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }
            if (comment.getHomeOrOverseas() == null) {
                errors.rejectValue("homeOrOverseas", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
       
    }

}
