package com.zuehlke.pgadmissions.rest.validation.validator.comment;

import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationInterviewAppointment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.TimeZone;

import static com.zuehlke.pgadmissions.utils.ValidationUtils.rejectIfNotNull;

@Component
public class CommentAssignInterviewersCustomValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Comment.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Comment comment = (Comment) target;
        PrismAction action = comment.getAction().getId();
        Preconditions.checkArgument(action == PrismAction.APPLICATION_ASSIGN_INTERVIEWERS, "Unexpected action: " + action);

        boolean takenPlace = false;
        CommentApplicationInterviewAppointment interviewAppointment = comment.getInterviewAppointment();
        LocalDateTime interviewLocalDateTime = interviewAppointment == null ? null : interviewAppointment.getInterviewDateTime();

        if (interviewLocalDateTime == null) {
            if (comment.getAppointmentTimeslots() == null || comment.getAppointmentTimeslots().isEmpty()) {
                errors.rejectValue("appointmentTimeslots", "min", new Object[] { 0 }, null);
            }
        } else {
            TimeZone interviewTimezone = interviewAppointment.getInterviewTimeZone();
            if (interviewTimezone == null) {
                ValidationUtils.rejectIfEmpty(errors, "interviewAppointment.interviewTimezone", "notNull");
            }

            DateTime interviewDateTime = interviewLocalDateTime.toDateTime(DateTimeZone.forTimeZone(interviewAppointment.getInterviewTimeZone()));
            if (interviewDateTime.isBeforeNow()) {
                takenPlace = true;
                rejectIfNotNull(comment, errors, "interviewInstruction.interviewerInstructions", "forbidden");
                rejectIfNotNull(comment, errors, "interviewInstruction.intervieweeInstructions", "forbidden");
                rejectIfNotNull(comment, errors, "interviewInstruction.interviewLocation", "forbidden");
            } else {
                rejectIfNotNull(comment, errors, "appointmentTimeslots", "forbidden");
            }
        }

        if (!takenPlace) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interviewInstruction.interviewerInstructions", "notEmpty");
        }
    }

}
