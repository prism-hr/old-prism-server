package com.zuehlke.pgadmissions.validators;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.BooleanUtils;
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

        if (interview.getStage() == null || stage == InterviewStage.INITIAL || interview.getTakenPlace() == null) {
            errors.rejectValue("stage", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
        
        if (interview.getTimeZone() == null) {
            errors.rejectValue("timeZone", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
        
        if(!errors.hasFieldErrors("duration") && interview.getDuration() == null){
            errors.rejectValue("duration", EMPTY_FIELD_ERROR_MESSAGE);
        }
        
        if (stage == InterviewStage.SCHEDULED) {
            Date interviewDueDate = interview.getInterviewDueDate();
            
            if (interviewDueDate == null) {
                errors.rejectValue("interviewDueDate", EMPTY_FIELD_ERROR_MESSAGE);
            } else if (stage == InterviewStage.SCHEDULED) {
                if (BooleanUtils.isTrue(interview.getTakenPlace()) && interviewDueDate.after(today)) {
                    errors.rejectValue("interviewDueDate", "date.field.notpast");
                } else if (BooleanUtils.isFalse(interview.getTakenPlace()) && interviewDueDate.before(today)) {
                    errors.rejectValue("interviewDueDate", "date.field.notfuture");
                }
            } 
            
            if (StringUtils.isBlank(interview.getTimeHours())) {
                errors.rejectValue("timeHours", EMPTY_FIELD_ERROR_MESSAGE);
            } else if (StringUtils.isBlank(interview.getTimeMinutes())) {
                errors.rejectValue("timeMinutes", EMPTY_FIELD_ERROR_MESSAGE);
            }
        }
        
        if (stage == InterviewStage.SCHEDULING) {
            boolean hasRejectedTime = false, hasRejectedPastDates = false;
            List<InterviewTimeslot> timeslots = interview.getTimeslots();
            
            if (timeslots.size() == 0) {
                errors.rejectValue("timeslots", MUST_SELECT_DATE_AND_TIME);
            }
            
            for (int i = 0; i < timeslots.size(); i++) {
                InterviewTimeslot timeslot = timeslots.get(i);
                
                Matcher timePatternMatcher = timePattern.matcher(timeslot.getStartTime());
                
                if (!timePatternMatcher.matches()) {
                    if (!hasRejectedTime) {
                        hasRejectedTime = true;
                        errors.rejectValue("timeslots", INVALID_TIME);
                    }
                }
                else {
                    Calendar dueDate = Calendar.getInstance();
                    dueDate.setTime(timeslot.getDueDate());
                    
                    String startTime = timeslot.getStartTime();
                    int hours = Integer.parseInt(startTime.substring(0, 2));
                    int minutes = Integer.parseInt(startTime.substring(3, 5));
                    
                    dueDate.set(Calendar.HOUR_OF_DAY, hours);
                    dueDate.set(Calendar.MINUTE, minutes);
                    
                    Calendar now = Calendar.getInstance();
                    
                    if (dueDate.getTime().before(now.getTime())) {
                        if (!hasRejectedPastDates) {
                            hasRejectedPastDates = true;
                            errors.rejectValue("timeslots", MUST_SELECT_DATE_AND_TIMES_IN_THE_FUTURE);
                        }
                    }
                }
            }
        }
        
        if (interview.getInterviewers().isEmpty()) {
            errors.rejectValue("interviewers", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
    }

}
