package com.zuehlke.pgadmissions.rest.validation.validator.comment;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;

import static com.zuehlke.pgadmissions.utils.ValidationUtils.rejectIfNotNull;

@Component
public class AssignInterviewersCommentCustomValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CommentDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CommentDTO comment = (CommentDTO) target;
        PrismAction action = comment.getAction();
        Preconditions.checkArgument(action == PrismAction.APPLICATION_ASSIGN_INTERVIEWERS, "Unexpected action: " + action);

        boolean takenPlace = false;
        if (comment.getInterviewDateTime() == null) {
            // to be scheduled
            if(comment.getAppointmentTimeslots() == null || comment.getAppointmentTimeslots().isEmpty()){
                errors.rejectValue("appointmentTimeslots", "min", new Object[]{0}, null);
            }
        } else {
            DateTime interviewDateTime = comment.getInterviewDateTime().toDateTime(DateTimeZone.forTimeZone(comment.getInterviewTimeZone()));
            if (interviewDateTime.isBeforeNow()) {
                // taken place
                takenPlace = true;
                rejectIfNotNull(comment, errors, "interviewerInstructions", "forbidden");
                rejectIfNotNull(comment, errors, "intervieweeInstructions", "forbidden");
                rejectIfNotNull(comment, errors, "interviewLocation", "forbidden");
            } else {
                // scheduled
                rejectIfNotNull(comment, errors, "appointmentTimeslots", "forbidden");
            }
        }

        if(!takenPlace) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interviewerInstructions", "notEmpty");
        }
    }
}
