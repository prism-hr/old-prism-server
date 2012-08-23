package com.zuehlke.pgadmissions.validators;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Interview;

@Service
public class InterviewValidator extends AbstractValidator {


	@Override
	public boolean supports(Class<?> clazz) {
		return Interview.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		Interview interview = (Interview) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "furtherDetails", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "timeHours", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "timeMinutes", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interviewDueDate", "text.field.empty");
		String dueDate = interview.getInterviewDueDate() == null ? "": interview.getInterviewDueDate().toString();
		if (StringUtils.isNotBlank(dueDate) && interview.getInterviewDueDate().before(today)) {
			errors.rejectValue("interviewDueDate", "date.field.notfuture");
		}
		if(interview.getInterviewers().isEmpty()){
			errors.rejectValue("interviewers", "dropdown.radio.select.none");
		}
	}
	
}
