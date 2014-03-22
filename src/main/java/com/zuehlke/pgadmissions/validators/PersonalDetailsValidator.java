package com.zuehlke.pgadmissions.validators;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;

@Component
public class PersonalDetailsValidator extends FormSectionObjectValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonalDetails.class.isAssignableFrom(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {

        PersonalDetails personalDetail = (PersonalDetails) target;
        if (personalDetail.getApplication() != null) {
            super.addExtraValidation(target, errors);
        }

        LocalDate baseline = new LocalDate();
        Date today = baseline.toDate();
        Date tomorrow = baseline.plusDays(1).toDate(); 
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstNationality", EMPTY_DROPDOWN_ERROR_MESSAGE);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceCountry", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfBirth", EMPTY_FIELD_ERROR_MESSAGE);
        String dob = personalDetail.getDateOfBirth() == null ? "" : personalDetail.getDateOfBirth().toString();
        if (StringUtils.isNotBlank(dob) && personalDetail.getDateOfBirth().after(today)) {
            errors.rejectValue("dateOfBirth", "date.field.notpast");
        } else if (personalDetail.getDateOfBirth() != null) {
            int age = Years.yearsBetween(new DateTime(personalDetail.getDateOfBirth()).withTimeAtStartOfDay(), new DateTime()).getYears();
            if (!(age >= 10 && age <= 80)) {
                DateTime now = new DateTime().withTimeAtStartOfDay();
                DateTime tenYearsAgo = now.toDateTime().minusYears(10);
                DateTime eightyYearsAgo = now.toDateTime().minusYears(81).plusDays(1);
                errors.rejectValue("dateOfBirth", "date.field.age",
                        new Object[] { eightyYearsAgo.toString("dd-MMM-yyyy"), tenYearsAgo.toString("dd-MMM-yyyy") }, null);
            }
        }

        if (personalDetail.getFirstNationality() != null && personalDetail.getSecondNationality() != null
                && personalDetail.getFirstNationality().getId().equals(personalDetail.getSecondNationality().getId())) {
            errors.rejectValue("secondNationality", "nationality.duplicate", new Object[] {}, null);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "disability", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ethnicity", EMPTY_DROPDOWN_ERROR_MESSAGE);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "application", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "englishFirstLanguage", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "requiresVisa", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (BooleanUtils.isFalse(personalDetail.getEnglishFirstLanguage())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "languageQualificationAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        if (BooleanUtils.isTrue(personalDetail.getPassportAvailable()) && BooleanUtils.isTrue(personalDetail.getRequiresVisa())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passportNumber", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nameOnPassport", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passportIssueDate", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passportExpiryDate", EMPTY_FIELD_ERROR_MESSAGE);

            Date passportExpiryDate = personalDetail.getPassportExpiryDate();
            Date passportIssueDate = personalDetail.getPassportIssueDate();
            
            if (passportExpiryDate != null) {
                if (passportExpiryDate.before(today)) {
                    errors.rejectValue("passportExpiryDate", "date.field.notfuture");
                }
            }
            
            if (passportIssueDate != null) {
                if (passportIssueDate.after(tomorrow)) {
                    errors.rejectValue("passportIssueDate", "date.field.notpast");
                }
            }
            
            if (passportExpiryDate != null && passportIssueDate != null) {
                if (org.apache.commons.lang.time.DateUtils.isSameDay(passportExpiryDate, passportIssueDate)) {
                    errors.rejectValue("passportExpiryDate", "date.field.same");
                    errors.rejectValue("passportIssueDate", "date.field.same");
                }
            }
        }

        if (BooleanUtils.isTrue(personalDetail.getLanguageQualificationAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationType", EMPTY_DROPDOWN_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "examDate", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "overallScore", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "readingScore", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "writingScore", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "speakingScore", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listeningScore", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "examOnline", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "languageQualificationDocument", "file.upload.empty");

            Date examDate = personalDetail.getExamDate();
            if (examDate != null && DateUtils.truncate(examDate, Calendar.DATE).after(today)) {
                errors.rejectValue("examDate", "date.field.notpast");
            }

            if (personalDetail.getQualificationType() == LanguageQualificationEnum.OTHER) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationTypeName", EMPTY_FIELD_ERROR_MESSAGE);
            }

            if (personalDetail.getQualificationType() == LanguageQualificationEnum.TOEFL) {
                if (StringUtils.isNotBlank(personalDetail.getOverallScore())) {
                    try {
                        Integer overallScore = Integer.valueOf(personalDetail.getOverallScore());
                        if (!(overallScore >= 0 && overallScore <= 120)) {
                            errors.rejectValue("overallScore", "languagepersonalDetail.overallScore.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("overallScore", "languagepersonalDetail.overallScore.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getListeningScore())) {
                    try {
                        Integer score = Integer.valueOf(personalDetail.getListeningScore());
                        if (!(score >= 0 && score <= 30)) {
                            errors.rejectValue("listeningScore", "languagepersonalDetail.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("listeningScore", "languagepersonalDetail.score.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getReadingScore())) {
                    try {
                        Integer score = Integer.valueOf(personalDetail.getReadingScore());
                        if (!(score >= 0 && score <= 30)) {
                            errors.rejectValue("readingScore", "languagepersonalDetail.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("readingScore", "languagepersonalDetail.score.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getSpeakingScore())) {
                    try {
                        Integer score = Integer.valueOf(personalDetail.getSpeakingScore());
                        if (!(score >= 0 && score <= 30)) {
                            errors.rejectValue("speakingScore", "languagepersonalDetail.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("speakingScore", "languagepersonalDetail.score.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getWritingScore())) {
                    try {
                        Integer score = Integer.valueOf(personalDetail.getWritingScore());
                        if (!(score >= 0 && score <= 30)) {
                            errors.rejectValue("writingScore", "languagepersonalDetail.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("writingScore", "languagepersonalDetail.score.notvalid");
                    }
                }
            } else if (personalDetail.getQualificationType() == LanguageQualificationEnum.IELTS_ACADEMIC) {
                if (StringUtils.isNotBlank(personalDetail.getOverallScore())) {
                    try {
                        Double overallScore = Double.valueOf(personalDetail.getOverallScore());
                        if (!(overallScore >= 4 && overallScore <= 9)) {
                            errors.rejectValue("overallScore", "languagepersonalDetail.general.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("overallScore", "languagepersonalDetail.general.score.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getListeningScore())) {
                    try {
                        Double score = Double.valueOf(personalDetail.getListeningScore());
                        if (!(score >= 4 && score <= 9)) {
                            errors.rejectValue("listeningScore", "languagepersonalDetail.general.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("listeningScore", "languagepersonalDetail.general.score.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getReadingScore())) {
                    try {
                        Double score = Double.valueOf(personalDetail.getReadingScore());
                        if (!(score >= 4 && score <= 9)) {
                            errors.rejectValue("readingScore", "languagepersonalDetail.general.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("readingScore", "languagepersonalDetail.general.score.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getSpeakingScore())) {
                    try {
                        Double score = Double.valueOf(personalDetail.getSpeakingScore());
                        if (!(score >= 4 && score <= 9)) {
                            errors.rejectValue("speakingScore", "languagepersonalDetail.general.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("speakingScore", "languagepersonalDetail.general.score.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getWritingScore())) {
                    try {
                        Double score = Double.valueOf(personalDetail.getWritingScore());
                        if (!(score >= 4 && score <= 9)) {
                            errors.rejectValue("writingScore", "languagepersonalDetail.general.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("writingScore", "languagepersonalDetail.general.score.notvalid");
                    }
                }
            } else {
                if (StringUtils.isNotBlank(personalDetail.getOverallScore())) {
                    try {
                        Integer overallScore = Integer.valueOf(personalDetail.getOverallScore());
                        if (!(overallScore >= 0 && overallScore <= 99)) {
                            errors.rejectValue("overallScore", "languagepersonalDetail.general.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("overallScore", "languagepersonalDetail.general.score.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getListeningScore())) {
                    try {
                        Integer score = Integer.valueOf(personalDetail.getListeningScore());
                        if (!(score >= 0 && score <= 99)) {
                            errors.rejectValue("listeningScore", "languagepersonalDetail.general.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("listeningScore", "languagepersonalDetail.general.score.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getReadingScore())) {
                    try {
                        Integer score = Integer.valueOf(personalDetail.getReadingScore());
                        if (!(score >= 0 && score <= 99)) {
                            errors.rejectValue("readingScore", "languagepersonalDetail.general.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("readingScore", "languagepersonalDetail.general.score.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getSpeakingScore())) {
                    try {
                        Integer score = Integer.valueOf(personalDetail.getSpeakingScore());
                        if (!(score >= 0 && score <= 99)) {
                            errors.rejectValue("speakingScore", "languagepersonalDetail.general.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("speakingScore", "languagepersonalDetail.general.score.notvalid");
                    }
                }

                if (StringUtils.isNotBlank(personalDetail.getWritingScore())) {
                    try {
                        Integer score = Integer.valueOf(personalDetail.getWritingScore());
                        if (!(score >= 0 && score <= 99)) {
                            errors.rejectValue("writingScore", "languagepersonalDetail.general.score.notvalid");
                        }
                    } catch (NumberFormatException e) {
                        errors.rejectValue("writingScore", "languagepersonalDetail.general.score.notvalid");
                    }
                }
            }
        }
    }
    
}
