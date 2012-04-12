package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Supervisor;

@Component
public class ProgrammeDetailsValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ProgrammeDetails.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeName", "user.programmeName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyOption", "user.studyOption.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "user.programmeStartDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "referrer", "user.programmeReferrer.notempty");

		ProgrammeDetails programmeDetail = (ProgrammeDetails) target;

		List<Supervisor> supervisors = programmeDetail.getSupervisors();
		for (int i = 0; i < supervisors.size(); i++) {
//			if (!EmailValidator.getInstance().isValid(supervisors.get(i).getEmail())) {
//				errors.rejectValue("supervisors", "programmeDetails.email.invalid");
//			}
			if (supervisors.get(i).getFirstname() == "" || supervisors.get(i).getFirstname() == null) {
				errors.rejectValue("supervisors", "programmeDetails.firstname.notempty");
			}
			if (supervisors.get(i).getLastname() == "" || supervisors.get(i).getLastname() == null) {
				errors.rejectValue("supervisors", "programmeDetails.lastname.notempty");
			}
		}
	}
}
