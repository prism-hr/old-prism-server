package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Component
public class LanguageQualificationValidator extends AbstractValidator {

    @Override
    protected void addExtraValidation(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationType", "dropdown.radio.select.none");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfExamination", "text.field.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "overallScore", "text.field.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "readingScore", "text.field.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "writingScore", "text.field.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "speakingcore", "text.field.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listeningScore", "text.field.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "examTakenOnline", "text.field.empty");

        LanguageQualification qualification = (LanguageQualification) target;
        if (qualification == null) {
            return;
        }

        Date examDate = qualification.getDateOfExamination();
        if (examDate != null && examDate.after(new Date()) && !DateUtils.isToday(qualification.getDateOfExamination())) {
            errors.rejectValue("dateOfExamination", "date.field.notpast");
        }
        
        if (qualification.getQualificationType() == LanguageQualificationEnum.OTHER) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherQualificationTypeName", "text.field.empty");
        }
    }
}
