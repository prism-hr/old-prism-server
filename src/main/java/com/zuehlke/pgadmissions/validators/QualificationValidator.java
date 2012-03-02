package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Qualification;

@Service
public class QualificationValidator  implements Validator{
	@Override
	public boolean supports(Class<?> clazz) {
		return Qualification.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "degree", "user.qualification.degree.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institution", "user.qualification.institution.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "date_taken", "user.qualification.date_taken.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "grade", "user.qualification.grade.notempty");
	}


}
