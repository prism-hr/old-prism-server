package com.zuehlke.pgadmissions.rest.validation.validator.comment;

import static com.zuehlke.pgadmissions.utils.ValidationUtils.rejectIfNotNull;
import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

@Component
public class CommentConfirmSupervisionCustomValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Comment.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Comment comment = (Comment) target;
        PrismAction action = comment.getAction().getId();
        Preconditions.checkArgument(action == PrismAction.APPLICATION_CONFIRM_PRIMARY_SUPERVISION, "Unexpected action: " + action);

        if (comment.getRecruiterAcceptAppointment() == null) {
            errors.reject("recruiterAcceptAppointment", "notNull");
        } else if (comment.getRecruiterAcceptAppointment()) {
            rejectIfNotNull(comment, errors, "content", "forbidden");
        } else {
            rejectIfEmptyOrWhitespace(errors, "content", "notNull");
        }
    }

}
