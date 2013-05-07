package com.zuehlke.pgadmissions.validators;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;
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
        InterviewStage stage = interview.getStage();

        if (interview.getStage() == null || stage == InterviewStage.INITIAL) {
            errors.rejectValue("stage", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
        
        // TODO: validate fields available in all scenarios.
        // TODO: Timezone.
//        TimeZone timeZone = TimeZone.getTimeZone(interview.getTimezone());
//        if (timeZone == null) {
//            errors.rejectValue("timeZone", EMPTY_DROPDOWN_ERROR_MESSAGE);
//        }
        
        if(!errors.hasFieldErrors("duration") && interview.getDuration() == null){
            errors.rejectValue("duration", EMPTY_FIELD_ERROR_MESSAGE);
        }
        
        if (stage == InterviewStage.TAKEN_PLACE || stage == InterviewStage.SCHEDULED) {
            Date interviewDueDate = interview.getInterviewDueDate();
            
            if (interviewDueDate == null) {
                errors.rejectValue("interviewDueDate", EMPTY_FIELD_ERROR_MESSAGE);
            } else if (stage == InterviewStage.SCHEDULED && interviewDueDate.before(today)) {
                errors.rejectValue("interviewDueDate", "date.field.notfuture");
            } else if (stage == InterviewStage.TAKEN_PLACE && interviewDueDate.after(today)) {
                errors.rejectValue("interviewDueDate", "date.field.notpast");
            }
            
            if (StringUtils.isBlank(interview.getTimeHours())) {
                errors.rejectValue("timeHours", EMPTY_FIELD_ERROR_MESSAGE);
            } else if (StringUtils.isBlank(interview.getTimeMinutes())) {
                errors.rejectValue("timeMinutes", EMPTY_FIELD_ERROR_MESSAGE);
            }
        }
        
        if (stage == InterviewStage.SCHEDULING) {
            List<InterviewTimeslot> timeslots = interview.getTimeslots();
            
            if (timeslots.size() == 0) {
                errors.rejectValue("timeslots", MUST_SELECT_DATE_AND_TIME);
            }
        }
        
        if (interview.getInterviewers().isEmpty()) {
            errors.rejectValue("interviewers", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
    }

}
