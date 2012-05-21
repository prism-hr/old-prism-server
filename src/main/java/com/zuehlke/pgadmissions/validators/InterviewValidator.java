package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Interview;

@Service
public class InterviewValidator implements Validator {


	@Override
	public boolean supports(Class<?> clazz) {
		return Interview.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Date today = new Date();
		Interview interview = (Interview) target;
		if (!UrlValidator.getInstance().isValid(interview.getLocationURL())) {
			errors.rejectValue("locationURL", "interview.locationURL.invalid");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "furtherDetails", "interview.furtherDetails.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interviewDueDate", "interview.interviewDueDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interviewTime", "interview.interviewTime.notempty");
		String dueDate = interview.getInterviewDueDate() == null ? "": interview.getInterviewDueDate().toString();
		if (StringUtils.isNotBlank(dueDate) && interview.getInterviewDueDate().before(today)) {
			errors.rejectValue("interviewDueDate", "interview.interviewDueDate.past");
		}
	}
	
}
