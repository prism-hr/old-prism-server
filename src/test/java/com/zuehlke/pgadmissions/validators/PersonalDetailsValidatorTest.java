package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.Passport;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.Title;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class PersonalDetailsValidatorTest {

    @Autowired
    private Validator validator;

    private PersonalDetails personalDetails;

    private PersonalDetailsValidator personalDetailValidator;

    private PassportValidator passportValidator;

    private LanguageQualificationValidator languageQualificationValidator;

    @Test
    public void shouldSupportPersonalDetail() {
        assertTrue(personalDetailValidator.supports(PersonalDetails.class));
    }

    @Test
    public void shouldRejectIfTitleIsEmpty() {
        personalDetails.setTitle(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("title").getCode());
    }

    @Test
    public void shouldRejectIfPhoneNumberIsLongerThan35() {
        personalDetails.setPhoneNumber("1234567890123456789012345678901234567890");
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 35 characters are allowed.", mappingResult.getFieldError("phoneNumber").getDefaultMessage());
    }

    @Test
    public void shouldRejectIfPhoneNumberIsNotValid() {
        personalDetails.setPhoneNumber("+41-00-0-000--000");
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter telephone numbers in the following format +44 (0) 123 123 1234.", mappingResult.getFieldError("phoneNumber")
                .getDefaultMessage());
    }

    @Test
    public void shouldRejectIfGenderisNull() {
        personalDetails.setGender(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("gender").getCode());
    }

    @Test
    public void shouldRejectIfDateOfBirthisNull() {
        personalDetails.setDateOfBirth(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("dateOfBirth").getCode());
    }

    @Test
    public void shouldRejectIfCountryIsNull() {
        personalDetails.setCountry(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("country").getCode());
    }

    @Test
    public void shouldRejectIfResidenceCountryIsNull() {
        personalDetails.setResidenceCountry(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("residenceCountry").getCode());
    }

    @Test
    public void shouldRejectIfApplicationFormIsNull() {
        personalDetails.setApplication(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("application").getCode());
    }

    @Test
    public void shouldRejectIfNoCandidateNationality() {
        personalDetails.setFirstNationality(null);
        personalDetails.setSecondNationality(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("firstNationality").getCode());
    }

    @Test
    public void shouldRejectIfDOBISFutureDate() {
        LocalDate tomorrow = new LocalDate().plusDays(1);
        personalDetails.setDateOfBirth(tomorrow);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("dateOfBirth").getCode());
    }

    @Test
    public void shouldRejectIfPhoneNumberIsEmpty() {
        personalDetails.setPhoneNumber(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("phoneNumber").getCode());
    }

    @Test
    public void shouldRejectIfDisabilityIsNull() {
        personalDetails.setDisability(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("disability").getCode());
    }

    @Test
    public void shouldRejectIfEthnicityIsNull() {
        personalDetails.setEthnicity(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("ethnicity").getCode());
    }

    @Test
    public void shouldRejectIfRequiresVisaIsNull() {
        personalDetails.setVisaRequired(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("requiresVisa").getCode());
    }

    @Test
    public void shouldRejectIfEnglishFirstLanguageIsNull() {
        personalDetails.setFirstLanguageEnglish(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("englishFirstLanguage").getCode());
    }

    @Test
    public void shouldRejectIfLanguageQualificationsIsNull() {
        personalDetails.setFirstLanguageEnglish(false);
        personalDetails.setLanguageQualificationAvailable(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("languageQualificationAvailable").getCode());
    }

    @Test
    public void shouldRejectPassportNumberIfEmpty() {
        personalDetails.getPassport().setNumber(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("passport.number").getCode());
    }

    @Test
    public void shouldRejectPassportNumberIfLongerThan35() {
        personalDetails.getPassport().setNumber("0123456789012345678901234567890123456789");
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 35 characters are allowed.", mappingResult.getFieldError("passport.number").getDefaultMessage());
    }

    @Test
    public void shouldRejectNameOnPassportIfEmpty() {
        personalDetails.getPassport().setName(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("passport.name").getCode());
    }

    @Test
    public void shouldRejectPassportIssueDateIfEmpty() {
        personalDetails.getPassport().setIssueDate(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("passport.issueDate").getCode());
    }

    @Test
    public void shouldRejectPassportExpiryDateIfEmpty() {
        personalDetails.getPassport().setExpiryDate(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("passport.expiryDate").getCode());
    }

    @Test
    public void shouldRejectPassportExpiryAndIssueDateAreTheSame() {
        LocalDate oneMonthAgo = new LocalDate().minusMonths(1);
        personalDetails.getPassport().setExpiryDate(oneMonthAgo);
        personalDetails.getPassport().setIssueDate(oneMonthAgo);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(3, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notfuture", mappingResult.getFieldErrors().get(0).getCode());
        Assert.assertEquals("date.field.same", mappingResult.getFieldErrors().get(1).getCode());
        Assert.assertEquals("date.field.same", mappingResult.getFieldErrors().get(2).getCode());
    }

    @Test
    public void shouldRejectPassportExpiryDateIsInThePast() {
        LocalDate oneMonthAgo = new LocalDate().minusMonths(1);
        personalDetails.getPassport().setExpiryDate(oneMonthAgo);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notfuture", mappingResult.getFieldError("passport.expiryDate").getCode());
    }

    @Test
    public void shouldRejectPassportIssueDateIsInTheFuture() {
        LocalDate oneMonthAhead = new LocalDate().plusMonths(1);
        personalDetails.getPassport().setIssueDate(oneMonthAhead);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("passport.issueDate").getCode());
    }

    @Test
    public void shouldRejectDateOfBirthIfAgeIsLessThan10() {
        LocalDate infant = new LocalDate().minusYears(9);
        personalDetails.setDateOfBirth(infant);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.age", mappingResult.getFieldError("dateOfBirth").getCode());
    }

    @Test
    public void shouldRejectDateOfBirthIfAgeIsBiggerThan80() {
        LocalDate oldGeezer = new LocalDate().minusYears(81);
        personalDetails.setDateOfBirth(oldGeezer);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.age", mappingResult.getFieldError("dateOfBirth").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfOtherIsSelectedAnNoTitle() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.OTHER);
        personalDetails.getLanguageQualification().setQualificationTypeOther(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("languageQualification.qualificationTypeOther").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfExamDateIsInTheFuture() {
        personalDetails.getLanguageQualification().setExamDate(new LocalDate().plusWeeks(5));
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("languageQualification.examDate").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfOverallScoreIsNotNumeric() {
        personalDetails.getLanguageQualification().setOverallScore("1 2");
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.general.score.notvalid", mappingResult.getFieldError("languageQualification.overallScore").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfReadingScoreIsIsNotNumeric() {
        personalDetails.getLanguageQualification().setReadingScore("1 2");
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.general.score.notvalid", mappingResult.getFieldError("languageQualification.readingScore").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfWritingScoreIsNotNumeric() {
        personalDetails.getLanguageQualification().setWritingScore("1 2");
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.general.score.notvalid", mappingResult.getFieldError("languageQualification.writingScore").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfSpeakingScoreIsNotNumeric() {
        personalDetails.getLanguageQualification().setSpeakingScore("1 2");
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.general.score.notvalid", mappingResult.getFieldError("languageQualification.speakingScore").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfListeningScoreIsIsNotNumeric() {
        personalDetails.getLanguageQualification().setListeningScore("1 2");
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.general.score.notvalid", mappingResult.getFieldError("languageQualification.listeningScore").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfOverallScoreIsEmpty() {
        personalDetails.getLanguageQualification().setOverallScore(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("languageQualification.overallScore").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfReadingScoreIsEmpty() {
        personalDetails.getLanguageQualification().setReadingScore(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("languageQualification.readingScore").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfWritingScoreIsEmpty() {
        personalDetails.getLanguageQualification().setWritingScore(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("languageQualification.writingScore").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfSpeakingScoreIsEmpty() {
        personalDetails.getLanguageQualification().setSpeakingScore(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("languageQualification.speakingScore").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfListeningScoreIsEmpty() {
        personalDetails.getLanguageQualification().setListeningScore(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("languageQualification.listeningScore").getCode());
    }

    @Test
    public void shouldRejectToeflLanguageQualificationIfOverallScoreIsAbove120() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setOverallScore("150");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.overallScore.notvalid", mappingResult.getFieldError("languageQualification.overallScore").getCode());
    }

    @Test
    public void shouldRejectToeflLanguageQualificationIfReadingScoreIsAbove30() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setReadingScore("150");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.score.notvalid", mappingResult.getFieldError("languageQualification.readingScore").getCode());
    }

    @Test
    public void shouldRejectToeflLanguageQualificationIfWritingScoreIsAbove30() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setWritingScore("150");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.score.notvalid", mappingResult.getFieldError("languageQualification.writingScore").getCode());
    }

    @Test
    public void shouldRejectToeflLanguageQualificationIfSpeakingScoreIsAbove30() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setSpeakingScore("150");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.score.notvalid", mappingResult.getFieldError("languageQualification.speakingScore").getCode());
    }

    @Test
    public void shouldRejectToeflLanguageQualificationIfListeningScoreIsAbove30() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setListeningScore("150");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.score.notvalid", mappingResult.getFieldError("languageQualification.listeningScore").getCode());
    }

    @Test
    public void shouldRejectToeflLanguageQualificationIfOverallScoreIsBelow0() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setOverallScore("-1");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.overallScore.notvalid", mappingResult.getFieldError("languageQualification.overallScore").getCode());
    }

    @Test
    public void shouldRejectToeflLanguageQualificationIfReadingScoreIsBelow0() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setReadingScore("-1");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.score.notvalid", mappingResult.getFieldError("languageQualification.readingScore").getCode());
    }

    @Test
    public void shouldRejectToeflLanguageQualificationIfWritingScoreIsBelow0() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setWritingScore("-1");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.score.notvalid", mappingResult.getFieldError("languageQualification.writingScore").getCode());
    }

    @Test
    public void shouldRejectToeflLanguageQualificationIfSpeakingScoreIsBelow0() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setSpeakingScore("-1");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.score.notvalid", mappingResult.getFieldError("languageQualification.speakingScore").getCode());
    }

    @Test
    public void shouldRejectToeflLanguageQualificationIfListeningScoreIsBelow0() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setListeningScore("-1");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("languageQualification.score.notvalid", mappingResult.getFieldError("languageQualification.listeningScore").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfNoDocument() {
        personalDetails.getLanguageQualification().setProofOfAward(null);

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("file.upload.empty", mappingResult.getFieldError("languageQualification.proofOfAward").getCode());
    }

    @Test
    public void shouldAcceptToeflLanguageQualificationIfOverallScoreIsBetween0And120() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setOverallScore("80");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldAcceptToeflLanguageQualificationIfReadingScoreIsBetween0And30() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setReadingScore("0");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldAcceptToeflLanguageQualificationIfWritingScoreIsBetween0And30() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setWritingScore("30");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldAcceptToeflLanguageQualificationIfSpeakingScoreIsBetween0And30() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setSpeakingScore("25");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldAcceptToeflLanguageQualificationIfListeningScoreIsBetween0And30() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.TOEFL);
        personalDetails.getLanguageQualification().setListeningScore("28");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldAcceptIeltsLanguageQualificationIfOverallScoreIsBetween4And9() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.IELTS_ACADEMIC);
        personalDetails.getLanguageQualification().setOverallScore("4");
        personalDetails.getLanguageQualification().setListeningScore("4");
        personalDetails.getLanguageQualification().setReadingScore("4");
        personalDetails.getLanguageQualification().setSpeakingScore("4");
        personalDetails.getLanguageQualification().setWritingScore("4");

        personalDetails.getLanguageQualification().setOverallScore("4");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldAcceptIeltsLanguageQualificationIfReadingScoreIsBetween4And9() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.IELTS_ACADEMIC);
        personalDetails.getLanguageQualification().setOverallScore("4");
        personalDetails.getLanguageQualification().setListeningScore("4");
        personalDetails.getLanguageQualification().setReadingScore("4");
        personalDetails.getLanguageQualification().setSpeakingScore("4");
        personalDetails.getLanguageQualification().setWritingScore("4");

        personalDetails.getLanguageQualification().setReadingScore("4.5");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldAcceptIeltsLanguageQualificationIfWritingScoreIsBetween4And9() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.IELTS_ACADEMIC);
        personalDetails.getLanguageQualification().setOverallScore("4");
        personalDetails.getLanguageQualification().setListeningScore("4");
        personalDetails.getLanguageQualification().setReadingScore("4");
        personalDetails.getLanguageQualification().setSpeakingScore("4");
        personalDetails.getLanguageQualification().setWritingScore("4");

        personalDetails.getLanguageQualification().setWritingScore("5");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldAcceptIeltsLanguageQualificationIfSpeakingScoreIsBetween4And9() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.IELTS_ACADEMIC);
        personalDetails.getLanguageQualification().setOverallScore("4");
        personalDetails.getLanguageQualification().setListeningScore("4");
        personalDetails.getLanguageQualification().setReadingScore("4");
        personalDetails.getLanguageQualification().setSpeakingScore("4");
        personalDetails.getLanguageQualification().setWritingScore("4");

        personalDetails.getLanguageQualification().setSpeakingScore("9");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldAcceptIeltsLanguageQualificationIfListeningScoreIsBetween4And9() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.IELTS_ACADEMIC);
        personalDetails.getLanguageQualification().setOverallScore("4");
        personalDetails.getLanguageQualification().setListeningScore("4");
        personalDetails.getLanguageQualification().setReadingScore("4");
        personalDetails.getLanguageQualification().setSpeakingScore("4");
        personalDetails.getLanguageQualification().setWritingScore("4");

        personalDetails.getLanguageQualification().setListeningScore("8.5");

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldNotThrowNullPointerExceptionForPassportInformationValidator() {
        personalDetails.setPassportAvailable(null);
        personalDetails.setVisaRequired(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
    }

    @Before
    public void setup() {
        Language nationality = new Language();
        personalDetails = new PersonalDetails()
                .withFirstNationality(nationality)
                .withApplication(new Application().withState(new State().withId(PrismState.APPLICATION_UNSUBMITTED)))
                .withCountry(new Country())
                .withDateOfBirth(new LocalDate().minusYears(28))
                .withGender(Gender.INDETERMINATE_GENDER)
                .withTitle(Title.PROFESSOR)
                .withResidenceCountry(new Domicile())
                .withPhoneNumber("+44 (0) 20 7911 5000")
                .withEthnicity(new Ethnicity().withId(23))
                .withDisability(new Disability())
                .withRequiresVisa(true)
                .withPassportAvailable(true)
                .withEnglishFirstLanguage(true)
                .withLanguageQualificationAvailable(true)
                .withPassportInformation(
                        new Passport().withName("Kevin Francis Denver").withNumber("000")
                                .withExpiryDate(new LocalDate().plusYears(20))
                                .withIssueDate(new LocalDate().minusYears(10)))
                .withLanguageQualification(
                        new LanguageQualification().withExamDate(new LocalDate()).withExamOnline(false).withQualificationType(LanguageQualificationEnum.OTHER)
                                .withQualificationTypeOther("foobar").withListeningScore("1").withOverallScore("1").withReadingScore("1").withWritingScore("1").withSpeakingScore("1")
                                .withProofOfAward(new Document()));

        passportValidator = new PassportValidator();
        passportValidator.setValidator((javax.validation.Validator) validator);

        languageQualificationValidator = new LanguageQualificationValidator();
        languageQualificationValidator.setValidator((javax.validation.Validator) validator);

        personalDetailValidator = new PersonalDetailsValidator(passportValidator, languageQualificationValidator);
        personalDetailValidator.setValidator((javax.validation.Validator) validator);
    }
}
