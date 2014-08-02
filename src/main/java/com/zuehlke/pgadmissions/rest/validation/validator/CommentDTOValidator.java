package com.zuehlke.pgadmissions.rest.validation.validator;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.validators.AbstractValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;

@Component
public class CommentDTOValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CommentDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        CommentDTO comment = (CommentDTO) target;

        PrismAction action = comment.getAction();

        if (Arrays.asList(PrismAction.APPLICATION_ASSESS_ELIGIBILITY, PrismAction.APPLICATION_CONFIRM_ELIGIBILITY).contains(action)) {
            ValidationUtils.rejectIfEmpty(errors, "qualified", EMPTY_DROPDOWN_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmpty(errors, "competentInWorkLanguage", EMPTY_DROPDOWN_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmpty(errors, "residenceStatus", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
    }
}
