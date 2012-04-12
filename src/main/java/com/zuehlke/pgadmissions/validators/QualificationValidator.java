package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;


@Service
public class QualificationValidator  implements Validator{
	@Override
	public boolean supports(Class<?> clazz) {
		return Qualification.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Date today = new Date();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationInstitution", "qualification.institution.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationSubject", "qualification.subject.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationStartDate", "qualification.start_date.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationLanguage", "qualification.language_of_study.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionCountry", "qualification.institutionCountry.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationType", "qualification.type.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationGrade", "qualification.grade.notempty");		
		Qualification qualification = (Qualification) target;
		String startDate = qualification.getQualificationStartDate() == null ? "": qualification.getQualificationStartDate().toString();
		String awardDate = qualification.getQualificationAwardDate() == null ? "": qualification.getQualificationAwardDate().toString();
		if (StringUtils.isNotBlank(startDate) && qualification.getQualificationAwardDate() != null && qualification.getQualificationStartDate().after(qualification.getQualificationAwardDate())) {
			errors.rejectValue("qualificationStartDate", "qualification.start_date.notvalid");
		}
		if (StringUtils.isNotBlank(startDate) && qualification.getQualificationStartDate().after(today)) {
			errors.rejectValue("qualificationStartDate", "qualification.start_date.future");
		}
		if (StringUtils.isNotBlank(awardDate) && qualification.getQualificationAwardDate().after(today)) {
			errors.rejectValue("qualificationAwardDate", "qualification.award_date.future");
		}
		if (qualification.getCompleted()== CheckedStatus.YES && StringUtils.isBlank(awardDate)){
			errors.rejectValue("qualificationAwardDate", "qualification.award_date.notempty");
		}
		if (qualification.getCompleted() == CheckedStatus.NO && StringUtils.isNotBlank(awardDate)){
			errors.rejectValue("qualificationAwardDate", "qualification.award_date.empty");
		}
	}


}
