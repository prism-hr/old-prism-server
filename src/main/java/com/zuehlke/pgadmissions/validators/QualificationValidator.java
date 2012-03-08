package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.QualificationDTO;


@Service
public class QualificationValidator  implements Validator{
	@Override
	public boolean supports(Class<?> clazz) {
		return QualificationDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationInstitution", "qualification.institution.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationProgramName", "qualification.name_of_programme.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationStartDate", "qualification.start_date.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationLanguage", "qualification.language_of_study.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationLevel", "qualification.level.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationType", "qualification.type.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationGrade", "qualification.grade.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationScore", "qualification.score.notempty");
	}


}
