package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class PersonalDetailsValidatorTest {

	private PersonalDetails personalDetails;
	
	@Autowired
	private PersonalDetailsValidator personalDetailValidator;

	@Test
	public void shouldSupportPersonalDetail() {
		assertTrue(personalDetailValidator.supports(PersonalDetails.class));
	}

	@Test
	public void shouldRejectIfFirstNameIsEmpty() {
		personalDetails.setFirstName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "firstName");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("firstName").getCode());
	}
	
	@Test
    public void shouldRejectIfFirstNameIsLongerThan30() {
        personalDetails.setFirstName("1234567890123456789012345678901234567890");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "firstName");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 30 characters are allowed.", mappingResult.getFieldError("firstName").getDefaultMessage());
    }
	
	@Test
    public void shouldRejectIfPhoneNumberIsLongerThan35() {
        personalDetails.setPhoneNumber("1234567890123456789012345678901234567890");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "phoneNumber");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 35 characters are allowed.", mappingResult.getFieldError("phoneNumber").getDefaultMessage());
    }
	
	@Test
    public void shouldRejectIfFirstNameContainsInvalidCharacters() {
	    String chineseName = StringEscapeUtils.unescapeJava("\\u5b9d\\u8912\\u82de\\n");
        personalDetails.setFirstName(chineseName);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "firstName");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("You must only enter valid characters.", mappingResult.getFieldError("firstName").getDefaultMessage());
    }

	@Test
	public void shouldRejectIfLasttNameIsEmpty() {
		personalDetails.setLastName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "lastName");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("lastName").getCode());
	}
	
    @Test
    public void shouldRejectIfLasttNameIsLongerThan40() {
        personalDetails.setLastName("12345678901234567890123456789012345678900123456789001234567890");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "lastName");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 40 characters are allowed.", mappingResult.getFieldError("lastName").getDefaultMessage());
    }
    
    @Test
    public void shouldRejectIfLastNameContainsInvalidCharacters() {
        String chineseName = StringEscapeUtils.unescapeJava("\\u5b9d\\u8912\\u82de\\n");
        personalDetails.setLastName(chineseName);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "lastName");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("You must only enter valid characters.", mappingResult.getFieldError("lastName").getDefaultMessage());
    }

	@Test
	public void shouldRejectIfEmailIsEmpty() {
		personalDetails.setEmail(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "email");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("email").getCode());
	}
	
	@Test
	public void shouldRejectIfEmailNotValidEmail() {
		personalDetails.setEmail("rerewrew");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "email");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("You must enter a valid email address.", mappingResult.getFieldError("email").getDefaultMessage());
	}
	
	@Test
    public void shouldRejectIfEmailIsLongerThan255Characters() {
        personalDetails.setEmail("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890@a.com");
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "email");
        personalDetailValidator.validate(personalDetails, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 255 characters are allowed.", mappingResult.getFieldError("email").getDefaultMessage());
    }

	@Test
	public void shouldRejectIfGenderisNull() {
		personalDetails.setGender(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "gender");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("gender").getCode());
	}

	@Test
	public void shouldRejectIfDateOfBirthisNull() {
		personalDetails.setDateOfBirth(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "dateOfBirth");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("dateOfBirth").getCode());
	}

	@Test
	public void shouldRejectIfCountryIsNull() {
		personalDetails.setCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "country");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("country").getCode());
	}

	@Test
	public void shouldRejectIfResidenceCountryIsNull() {
		personalDetails.setResidenceCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "residenceCountry");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("residenceCountry").getCode());
	}

	@Test
	public void shouldRejectIfApplicationFormIsNull() {
		personalDetails.setApplication(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "application");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("application").getCode());
	}

	@Test
	public void shouldRejectIfNoCandidateNationality() {
		personalDetails.getCandidateNationalities().clear();
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "candidateNationalities");
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
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "dateOfBirth");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("date.field.notpast", mappingResult.getFieldError("dateOfBirth").getCode());
	}

	@Test
	public void shouldRejectIfPhoneNumberIsEmpty() {
		personalDetails.setPhoneNumber(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "phoneNumber");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("phoneNumber").getCode());
	}

	@Test
	public void shouldRejectIfDisabilityIsNull() {
		personalDetails.setDisability(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "disability");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("disability").getCode());
	}

	@Test
	public void shouldRejectIfEthnicityIsNull() {
		personalDetails.setEthnicity(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "ethnicity");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("ethnicity").getCode());
	}
	
	@Test
	public void shouldRejectIfRequiresVisaIsNull() {
		personalDetails.setRequiresVisa(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "requiresVisa");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("requiresVisa").getCode());
	}
	
	@Test
	public void shouldRejectIfEnglishFirstLanguageIsNull() {
		personalDetails.setEnglishFirstLanguage(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "englishFirstLanguage");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("englishFirstLanguage").getCode());
	}
	
	@Before
	public void setup() {
		Country nationality = new Country();
		personalDetails = new PersonalDetailsBuilder()//
				.candiateNationalities(nationality)//
				.maternalGuardianNationalities(nationality)//
				.paternalGuardianNationalities(nationality)//
				.applicationForm(new ApplicationFormBuilder().id(2).toApplicationForm())//
				.country(new CountryBuilder().toCountry())//
				.dateOfBirth(new Date()).email("email@test.com").firstName("bob")//
				.gender(Gender.PREFER_NOT_TO_SAY).lastName("smith")//
				.residenceCountry(new CountryBuilder().toCountry())//
				.phoneNumber("abc")//
				.ethnicity(new EthnicityBuilder().id(23).toEthnicity())//
				.disability(new DisabilityBuilder().id(23213).toDisability())//
				.requiresVisa(true)
				.englishFirstLanguage(true)
				.toPersonalDetails();
	}
}
