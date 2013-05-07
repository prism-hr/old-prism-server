package com.zuehlke.pgadmissions.validators;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;

@Component
public class InterviewValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Interview.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        Interview interview = (Interview) target;

        if (interview.getStage() == null || interview.getStage() == InterviewStage.INITIAL) {
            errors.rejectValue("stage", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        if (StringUtils.isBlank(interview.getTimeHours())) {
            errors.rejectValue("timeHours", EMPTY_FIELD_ERROR_MESSAGE);
        } else if (StringUtils.isBlank(interview.getTimeMinutes())) {
            errors.rejectValue("timeMinutes", EMPTY_FIELD_ERROR_MESSAGE);
        }

        if (interview.getInterviewDueDate() == null) {
            errors.rejectValue("interviewDueDate", EMPTY_FIELD_ERROR_MESSAGE);
        } else if (interview.getStage() == InterviewStage.SCHEDULED && interview.getInterviewDueDate().before(today)) {
            errors.rejectValue("interviewDueDate", "date.field.notfuture");
        } else if (interview.getStage() == InterviewStage.TAKEN_PLACE && interview.getInterviewDueDate().after(today)) {
            errors.rejectValue("interviewDueDate", "date.field.notpast");
        }

        if (interview.getStage() == InterviewStage.SCHEDULED || interview.getStage() == InterviewStage.SCHEDULING) {
            if(interview.getDuration() == null){
                errors.rejectValue("duration", EMPTY_FIELD_ERROR_MESSAGE);
            }
        }

        if (interview.getInterviewers().isEmpty()) {
            errors.rejectValue("interviewers", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
    }

}
