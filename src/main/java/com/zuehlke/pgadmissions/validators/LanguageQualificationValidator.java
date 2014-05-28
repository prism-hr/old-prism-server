package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;

@Component
public class LanguageQualificationValidator extends AbstractValidator {

    @Override
    protected void addExtraValidation(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationType", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "examDate", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "overallScore", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "readingScore", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "writingScore", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "speakingScore", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listeningScore", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "examOnline", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "proofOfAward", "file.upload.empty");

        LanguageQualification qualification = (LanguageQualification) target;
        if (qualification == null) {
            return;
        }

        LocalDate examDate = qualification.getExamDate();

        LocalDate today = new LocalDate();
        if (examDate != null && examDate.isAfter(today)) {
            errors.rejectValue("examDate", "date.field.notpast");
        }

        if (qualification.getQualificationType() == LanguageQualificationEnum.OTHER) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationTypeOther", EMPTY_FIELD_ERROR_MESSAGE);
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
