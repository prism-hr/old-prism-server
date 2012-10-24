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
public class QualificationValidator  extends FormSectionObjectValidator implements Validator {
    
    private static final int MAX_NUMBER_OF_POSITIONS = 6;
    
	@Override
	public boolean supports(Class<?> clazz) {
		return Qualification.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		super.addExtraValidation(target, errors);
		
		Date today = new Date();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationInstitution", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationSubject", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationStartDate", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationLanguage", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionCountry", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationType", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationGrade", "text.field.empty");		
		Qualification qualification = (Qualification) target;
		String startDate = qualification.getQualificationStartDate() == null ? "": qualification.getQualificationStartDate().toString();
		String awardDate = qualification.getQualificationAwardDate() == null ? "": qualification.getQualificationAwardDate().toString();
		if (StringUtils.isNotBlank(startDate) && qualification.getQualificationAwardDate() != null && qualification.getQualificationStartDate().after(qualification.getQualificationAwardDate())) {
			errors.rejectValue("qualificationStartDate", "qualification.start_date.notvalid");
		}
		if (StringUtils.isNotBlank(startDate) && qualification.getQualificationStartDate().after(today)) {
			errors.rejectValue("qualificationStartDate", "date.field.notpast");
		}
		if (StringUtils.isNotBlank(awardDate) && qualification.getQualificationAwardDate().after(today)) {
			errors.rejectValue("qualificationAwardDate", "date.field.notpast");
		}
		if (qualification.getCompleted()== CheckedStatus.YES && StringUtils.isBlank(awardDate)){
			errors.rejectValue("qualificationAwardDate", "text.field.empty");
		}
		if (qualification.getCompleted() == CheckedStatus.NO && StringUtils.isNotBlank(awardDate)){
			errors.rejectValue("qualificationAwardDate", "text.field.empty");
		}
		if (qualification.getApplication().getEmploymentPositions().size() >= MAX_NUMBER_OF_POSITIONS + 1) {
            errors.reject("");
        }
	}
}
