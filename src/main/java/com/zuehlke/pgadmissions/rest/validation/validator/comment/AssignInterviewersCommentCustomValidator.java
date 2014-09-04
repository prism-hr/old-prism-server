package com.zuehlke.pgadmissions.rest.validation.validator.comment;

import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

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
            DateTime interviewDateTime = new DateTime(comment.getInterviewDateTime().getMillis(), DateTimeZone.forTimeZone(comment.getInterviewTimeZone()));
            if (interviewDateTime.isBeforeNow()) {
                // taken place
                takenPlace = true;
                if (comment.getInterviewerInstructions() != null) {
                    errors.rejectValue("interviewerInstructions", "forbidden");
                }
                if (comment.getIntervieweeInstructions() != null) {
                    errors.rejectValue("intervieweeInstructions", "forbidden");
                }
                if (comment.getInterviewLocation() != null) {
                    errors.rejectValue("interviewLocation", "forbidden");
                }
            } else {
                // scheduled
                if (comment.getAppointmentTimeslots() != null) {
                    errors.rejectValue("appointmentTimeslots", "forbidden");
                }
            }
        }

        if(!takenPlace) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interviewerInstructions", "notEmpty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interviewerInstructions", "notEmpty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interviewLocation", "notEmpty");
        }
    }
}
