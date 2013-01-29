package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
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
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "speakingScore", "text.field.empty");
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
        
        if (qualification.getQualificationType() == LanguageQualificationEnum.TOEFL) {
            if (StringUtils.isNotBlank(qualification.getOverallScore())) {
                try {
                    Integer overallScore = Integer.valueOf(qualification.getOverallScore());
                    if (!(overallScore >= 0 && overallScore <= 120)) {
                        errors.rejectValue("overallScore", "languageQualification.overallScore.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("overallScore", "languageQualification.overallScore.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getListeningScore())) {
                try {
                    Integer score = Integer.valueOf(qualification.getListeningScore());
                    if (!(score >= 0 && score <= 30)) {
                        errors.rejectValue("listeningScore", "languageQualification.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("listeningScore", "languageQualification.score.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getReadingScore())) {
                try {
                    Integer score = Integer.valueOf(qualification.getReadingScore());
                    if (!(score >= 0 && score <= 30)) {
                        errors.rejectValue("readingScore", "languageQualification.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("readingScore", "languageQualification.score.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getSpeakingScore())) {
                try {
                    Integer score = Integer.valueOf(qualification.getSpeakingScore());
                    if (!(score >= 0 && score <= 30)) {
                        errors.rejectValue("speakingScore", "languageQualification.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("speakingScore", "languageQualification.score.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getWritingScore())) {
                try {
                    Integer score = Integer.valueOf(qualification.getWritingScore());
                    if (!(score >= 0 && score <= 30)) {
                        errors.rejectValue("writingScore", "languageQualification.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("writingScore", "languageQualification.score.notvalid");
                }
            }
        } else if (qualification.getQualificationType() == LanguageQualificationEnum.IELTS_ACADEMIC) {
            if (StringUtils.isNotBlank(qualification.getOverallScore())) {
                try {
                    Double overallScore = Double.valueOf(qualification.getOverallScore());
                    if (!(overallScore >= 4 && overallScore <= 9)) {
                        errors.rejectValue("overallScore", "languageQualification.general.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("overallScore", "languageQualification.general.score.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getListeningScore())) {
                try {
                    Double score = Double.valueOf(qualification.getListeningScore());
                    if (!(score >= 4 && score <= 9)) {
                        errors.rejectValue("listeningScore", "languageQualification.general.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("listeningScore", "languageQualification.general.score.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getReadingScore())) {
                try {
                    Double score = Double.valueOf(qualification.getReadingScore());
                    if (!(score >= 4 && score <= 9)) {
                        errors.rejectValue("readingScore", "languageQualification.general.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("readingScore", "languageQualification.general.score.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getSpeakingScore())) {
                try {
                    Double score = Double.valueOf(qualification.getSpeakingScore());
                    if (!(score >= 4 && score <= 9)) {
                        errors.rejectValue("speakingScore", "languageQualification.general.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("speakingScore", "languageQualification.general.score.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getWritingScore())) {
                try {
                    Double score = Double.valueOf(qualification.getWritingScore());
                    if (!(score >= 4 && score <= 9)) {
                        errors.rejectValue("writingScore", "languageQualification.general.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("writingScore", "languageQualification.general.score.notvalid");
                }
            }
        } else {
            if (StringUtils.isNotBlank(qualification.getOverallScore())) {
                try {
                    Integer overallScore = Integer.valueOf(qualification.getOverallScore());
                    if (!(overallScore >= 0 && overallScore <= 99)) {
                        errors.rejectValue("overallScore", "languageQualification.general.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("overallScore", "languageQualification.general.score.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getListeningScore())) {
                try {
                    Integer score = Integer.valueOf(qualification.getListeningScore());
                    if (!(score >= 0 && score <= 99)) {
                        errors.rejectValue("listeningScore", "languageQualification.general.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("listeningScore", "languageQualification.general.score.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getReadingScore())) {
                try {
                    Integer score = Integer.valueOf(qualification.getReadingScore());
                    if (!(score >= 0 && score <= 99)) {
                        errors.rejectValue("readingScore", "languageQualification.general.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("readingScore", "languageQualification.general.score.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getSpeakingScore())) {
                try {
                    Integer score = Integer.valueOf(qualification.getSpeakingScore());
                    if (!(score >= 0 && score <= 99)) {
                        errors.rejectValue("speakingScore", "languageQualification.general.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("speakingScore", "languageQualification.general.score.notvalid");
                }
            }
            
            if (StringUtils.isNotBlank(qualification.getWritingScore())) {
                try {
                    Integer score = Integer.valueOf(qualification.getWritingScore());
                    if (!(score >= 0 && score <= 99)) {
                        errors.rejectValue("writingScore", "languageQualification.general.score.notvalid");
                    }
                } catch (NumberFormatException e) {
                    errors.rejectValue("writingScore", "languageQualification.general.score.notvalid");
                }
            }
        }
    }
}
