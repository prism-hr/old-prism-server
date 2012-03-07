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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institution", "qualification.institution.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name_of_programme", "qualification.name_of_programme.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "start_date", "qualification.start_date.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "termination_reason", "qualification.termination_reason.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "termination_date", "qualification.termination_date.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "qualification.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "language_of_study", "qualification.language_of_study.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "level", "qualification.level.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "qualification.type.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "grade", "qualification.grade.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "score", "qualification.score.notempty");
	}


}
