package com.zuehlke.pgadmissions.validators;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Interview;

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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "furtherDetails", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interviewDueDate", EMPTY_FIELD_ERROR_MESSAGE);
		String dueDate = interview.getInterviewDueDate() == null ? "": interview.getInterviewDueDate().toString();

		if (StringUtils.isBlank(interview.getTimeHours())) {
		    errors.rejectValue("timeHours", EMPTY_FIELD_ERROR_MESSAGE);
		} else if (StringUtils.isBlank(interview.getTimeMinutes())) {
		    errors.rejectValue("timeMinutes", EMPTY_FIELD_ERROR_MESSAGE);
		}
		
		if (StringUtils.isNotBlank(dueDate) && interview.getInterviewDueDate().before(today)) {
			errors.rejectValue("interviewDueDate", "date.field.notfuture");
		}
		
		if(interview.getInterviewers().isEmpty()){
			errors.rejectValue("interviewers", EMPTY_DROPDOWN_ERROR_MESSAGE);
		}
	}
	
}
