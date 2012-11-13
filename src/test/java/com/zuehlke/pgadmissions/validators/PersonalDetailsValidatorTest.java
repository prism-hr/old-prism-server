package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.StringEscapeUtils;
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
	public void shouldRejectIfFirstNameIsEmpty() {
		personalDetails.setFirstName(null);
		BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("firstName").getCode());
	}
	
	@Test
    public void shouldRejectIfFirstNameIsLongerThan30() {
        personalDetails.setFirstName("1234567890123456789012345678901234567890");
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 30 characters are allowed.", mappingResult.getFieldError("firstName").getDefaultMessage());
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
        Assert.assertEquals("You must enter telephone numbers in the following format +44 (0) 123 123 1234.", mappingResult.getFieldError("phoneNumber").getDefaultMessage());
    }	
	
	@Test
    public void shouldRejectIfFirstNameContainsInvalidCharacters() {
	    String chineseName = StringEscapeUtils.unescapeJava("\\u5b9d\\u8912\\u82de\\n");
        personalDetails.setFirstName(chineseName);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter ASCII compliant characters.", mappingResult.getFieldError("firstName").getDefaultMessage());
    }

	@Test
	public void shouldRejectIfLasttNameIsEmpty() {
		personalDetails.setLastName(null);
		BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastName").getCode());
	}
	
    @Test
    public void shouldRejectIfLasttNameIsLongerThan40() {
        personalDetails.setLastName("12345678901234567890123456789012345678900123456789001234567890");
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 40 characters are allowed.", mappingResult.getFieldError("lastName").getDefaultMessage());
    }
    
    @Test
    public void shouldRejectIfLastNameContainsInvalidCharacters() {
        String chineseName = StringEscapeUtils.unescapeJava("\\u5b9d\\u8912\\u82de\\n");
        personalDetails.setLastName(chineseName);
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter ASCII compliant characters.", mappingResult.getFieldError("lastName").getDefaultMessage());
    }

	@Test
	public void shouldRejectIfEmailIsEmpty() {
		personalDetails.setEmail(null);
		BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("email").getCode());
	}
	
	@Test
	public void shouldRejectIfEmailNotValidEmail() {
		personalDetails.setEmail("rerewrew");
		BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("You must enter a valid email address.", mappingResult.getFieldError("email").getDefaultMessage());
	}
	
	@Test
    public void shouldRejectIfEmailIsLongerThan255Characters() {
	    StringBuilder builder = new StringBuilder();
	    for (int i = 0; i < 300; i++) {
            builder.append("a");
        }
        personalDetails.setEmail(builder.append("@1.com").toString());
        BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 255 characters are allowed.", mappingResult.getFieldError("email").getDefaultMessage());
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
		personalDetails.getCandidateNationalities().clear();
		BindingResult mappingResult = new BeanPropertyBindingResult(personalDetails, "personalDetails");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("candidateNationalities").getCode());
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
	
	@Before
	public void setup() {
		Language nationality = new Language();
		personalDetails = new PersonalDetailsBuilder()//
				.candiateNationalities(nationality)//
				.applicationForm(new ApplicationFormBuilder().id(2).toApplicationForm())//
				.country(new CountryBuilder().toCountry())//
				.dateOfBirth(DateUtils.addYears(new Date(), -28))
				.email("email@test.com")
				.firstName("bob")//
				.gender(Gender.INDETERMINATE_GENDER).lastName("smith")//
				.title(Title.PROFESSOR)//
				.residenceDomicile(new DomicileBuilder().toDomicile())//
				.phoneNumber("0123456")//
				.ethnicity(new EthnicityBuilder().id(23).toEthnicity())//
				.disability(new DisabilityBuilder().id(23213).toDisability())//
				.requiresVisa(true)
				.englishFirstLanguage(true)
				.languageQualificationAvailable(true)
                .passportInformation(
                        new PassportInformationBuilder().nameOnPassport("Kevin Francis Denver").passportNumber("000")
                                .passportExpiryDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), 20))
                                .passportIssueDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -10))
                                .toPassportInformation())
                .languageQualifications(
                        new LanguageQualificationBuilder().id(1).dateOfExamination(new Date()).examTakenOnline(false)
                                .languageQualification(LanguageQualificationEnum.OTHER)
                                .otherQualificationTypeName("foobar").listeningScore("1").overallScore("1")
                                .readingScore("1").writingScore("1").speakingScore("1").toLanguageQualification())
				.toPersonalDetails();
		
		passportInformationValidator = new PassportInformationValidator();
		passportInformationValidator.setValidator((javax.validation.Validator) validator);
		
		languageQualificationValidator = new LanguageQualificationValidator();
		languageQualificationValidator.setValidator((javax.validation.Validator) validator);
		
		personalDetailValidator = new PersonalDetailsValidator(passportInformationValidator, languageQualificationValidator);
		personalDetailValidator.setValidator((javax.validation.Validator) validator);
	}
}
