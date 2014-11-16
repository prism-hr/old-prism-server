package com.zuehlke.pgadmissions.rest.validation.validator.comment;

import static com.zuehlke.pgadmissions.utils.ValidationUtils.rejectIfNotNull;
import static org.springframework.validation.ValidationUtils.rejectIfEmptyOrWhitespace;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;

@Component
public class CommentConfirmSupervisionCustomValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CommentDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CommentDTO comment = (CommentDTO) target;
        PrismAction action = comment.getAction();
        Preconditions.checkArgument(action == PrismAction.APPLICATION_CONFIRM_PRIMARY_SUPERVISION, "Unexpected action: " + action);

        if (comment.getRecruiterAcceptAppointment() == null) {
            errors.reject("recruiterAcceptAppointment", "notNull");
        } else if (comment.getRecruiterAcceptAppointment()) {
            if (comment.getAssignedUsers() == null || comment.getAssignedUsers().isEmpty()) {
                errors.rejectValue("assignedUsers", "min", new Object[]{0}, null);
            } else if (comment.getAssignedUsers().size() > 1) {
                errors.rejectValue("assignedUsers", "max", new Object[]{1}, null);
            }
            rejectIfEmptyOrWhitespace(errors, "positionProvisionalStartDate", "notNull");
            rejectIfNotNull(comment, errors, "content", "forbidden");
        } else {
            rejectIfEmptyOrWhitespace(errors, "content", "notNull");
            rejectIfNotNull(comment, errors, "assignedUsers", "forbidden");
            rejectIfNotNull(comment, errors, "positionProvisionalStartDate", "forbidden");
            rejectIfNotNull(comment, errors, "appointmentConditions", "forbidden");
            rejectIfNotNull(comment, errors, "positionTitle", "forbidden");
            rejectIfNotNull(comment, errors, "positionDescription", "forbidden");
        }
    }
}
