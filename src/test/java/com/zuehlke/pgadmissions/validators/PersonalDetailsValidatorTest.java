package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageQualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PassportInformationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.Title;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class PersonalDetailsValidatorTest {

    @Autowired
    private Validator validator;

    private PersonalDetails personalDetails;

    private PersonalDetailsValidator personalDetailValidator;

    private PassportInformationValidator passportInformationValidator;

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
        Date tomorrow;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        tomorrow = calendar.getTime();
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
        personalDetails.setRequiresVisa(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("requiresVisa").getCode());
    }

    @Test
    public void shouldRejectIfEnglishFirstLanguageIsNull() {
        personalDetails.setEnglishFirstLanguage(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("englishFirstLanguage").getCode());
    }

    @Test
    public void shouldRejectIfLanguageQualificationsIsNull() {
        personalDetails.setEnglishFirstLanguage(false);
        personalDetails.setLanguageQualificationAvailable(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("languageQualificationAvailable").getCode());
    }

    @Test
    public void shouldRejectPassportNumberIfEmpty() {
        personalDetails.getPassportInformation().setPassportNumber(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("passportInformation.passportNumber").getCode());
    }

    @Test
    public void shouldRejectPassportNumberIfLongerThan35() {
        personalDetails.getPassportInformation().setPassportNumber("0123456789012345678901234567890123456789");
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 35 characters are allowed.", mappingResult.getFieldError("passportInformation.passportNumber").getDefaultMessage());
    }

    @Test
    public void shouldRejectNameOnPassportIfEmpty() {
        personalDetails.getPassportInformation().setNameOnPassport(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("passportInformation.nameOnPassport").getCode());
    }

    @Test
    public void shouldRejectPassportIssueDateIfEmpty() {
        personalDetails.getPassportInformation().setPassportIssueDate(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("passportInformation.passportIssueDate").getCode());
    }

    @Test
    public void shouldRejectPassportExpiryDateIfEmpty() {
        personalDetails.getPassportInformation().setPassportExpiryDate(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("passportInformation.passportExpiryDate").getCode());
    }

    @Test
    public void shouldRejectPassportExpiryAndIssueDateAreTheSame() {
        Date oneMonthAgo = org.apache.commons.lang.time.DateUtils.addMonths(new Date(), -1);
        personalDetails.getPassportInformation().setPassportExpiryDate(oneMonthAgo);
        personalDetails.getPassportInformation().setPassportIssueDate(oneMonthAgo);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(3, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notfuture", mappingResult.getFieldErrors().get(0).getCode());
        Assert.assertEquals("date.field.same", mappingResult.getFieldErrors().get(1).getCode());
        Assert.assertEquals("date.field.same", mappingResult.getFieldErrors().get(2).getCode());
    }

    @Test
    public void shouldRejectPassportExpiryDateIsInThePast() {
        Date oneMonthAgo = org.apache.commons.lang.time.DateUtils.addMonths(new Date(), -1);
        personalDetails.getPassportInformation().setPassportExpiryDate(oneMonthAgo);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notfuture", mappingResult.getFieldError("passportInformation.passportExpiryDate").getCode());
    }

    @Test
    public void shouldRejectPassportIssueDateIsInTheFuture() {
        Date oneMonthAgo = org.apache.commons.lang.time.DateUtils.addMonths(new Date(), +1);
        personalDetails.getPassportInformation().setPassportIssueDate(oneMonthAgo);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("passportInformation.passportIssueDate").getCode());
    }

    @Test
    public void shouldRejectDateOfBirthIfAgeIsLessThan10() {
        Date infant = org.apache.commons.lang.time.DateUtils.addYears(new Date(), -9);
        personalDetails.setDateOfBirth(infant);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.age", mappingResult.getFieldError("dateOfBirth").getCode());
    }

    @Test
    public void shouldRejectDateOfBirthIfAgeIsBiggerThan80() {
        Date oldGeezer = org.apache.commons.lang.time.DateUtils.addYears(new Date(), -81);
        personalDetails.setDateOfBirth(oldGeezer);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("date.field.age", mappingResult.getFieldError("dateOfBirth").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfOtherIsSelectedAnNoTitle() {
        personalDetails.getLanguageQualification().setQualificationType(LanguageQualificationEnum.OTHER);
        personalDetails.getLanguageQualification().setQualificationTypeName(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("languageQualification.qualificationTypeName").getCode());
    }

    @Test
    public void shouldRejectLanguageQualificationIfExamDateIsInTheFuture() {
        personalDetails.getLanguageQualification().setExamDate(DateUtils.addWeeks(new Date(), 5));
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
        personalDetails.getLanguageQualification().setLanguageQualificationDocument(null);

        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("file.upload.empty", mappingResult.getFieldError("languageQualification.languageQualificationDocument").getCode());
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
        personalDetails.setRequiresVisa(null);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
    }

    @Before
    public void setup() {
        Language nationality = new Language();
        personalDetails = new PersonalDetailsBuilder().firstNationality(nationality)
                .applicationForm(new ApplicationFormBuilder().id(2).build())
                .country(new CountryBuilder().build())
                .dateOfBirth(DateUtils.addYears(new Date(), -28))
                .gender(Gender.INDETERMINATE_GENDER)
                .title(Title.PROFESSOR)
                .residenceDomicile(new DomicileBuilder().build())
                .phoneNumber("+44 (0) 20 7911 5000")
                .ethnicity(new EthnicityBuilder().id(23).build())
                .disability(new DisabilityBuilder().id(23213).build())
                .requiresVisa(true)
                .passportAvailable(true)
                .englishFirstLanguage(true)
                .languageQualificationAvailable(true)
                .passportInformation(
                        new PassportInformationBuilder().nameOnPassport("Kevin Francis Denver").passportNumber("000")
                                .passportExpiryDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), 20))
                                .passportIssueDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -10)).build())
                .languageQualification(
                        new LanguageQualificationBuilder().examDate(new Date()).examOnline(false)
                                .languageQualification(LanguageQualificationEnum.OTHER).qualificationTypeName("foobar").listeningScore("1")
                                .overallScore("1").readingScore("1").writingScore("1").speakingScore("1")
                                .languageQualificationDocument(new DocumentBuilder().build()).build()).build();

        passportInformationValidator = new PassportInformationValidator();
        passportInformationValidator.setValidator((javax.validation.Validator) validator);

        languageQualificationValidator = new LanguageQualificationValidator();
        languageQualificationValidator.setValidator((javax.validation.Validator) validator);

        personalDetailValidator = new PersonalDetailsValidator(passportInformationValidator, languageQualificationValidator);
        personalDetailValidator.setValidator((javax.validation.Validator) validator);
    }
}
