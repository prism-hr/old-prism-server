package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;

public class PersonalDetailsValidatorTest {

	private PersonalDetails personalDetails;
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
		Assert.assertEquals("personalDetails.firstName.notempty", mappingResult.getFieldError("firstName").getCode());
	}

	@Test
	public void shouldRejectIfLasttNameIsEmpty() {
		personalDetails.setLastName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "lastName");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.lastName.notempty", mappingResult.getFieldError("lastName").getCode());
	}

	@Test
	public void shouldRejectIfEmailIsEmpty() {
		personalDetails.setEmail(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "email");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.email.notempty", mappingResult.getFieldError("email").getCode());
	}

	
	@Test
	public void shouldRejectIfEmailNotValidEmail() {
		personalDetails.setEmail("rerewrew");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "email");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.email.invalid", mappingResult.getFieldError("email").getCode());
	}

	@Test
	public void shouldRejectIfGenderisNull() {
		personalDetails.setGender(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "gender");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.gender.notempty", mappingResult.getFieldError("gender").getCode());
	}

	@Test
	public void shouldRejectIfDateOfBirthisNull() {
		personalDetails.setDateOfBirth(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "dateOfBirth");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.dateOfBirth.notempty", mappingResult.getFieldError("dateOfBirth").getCode());
	}

	@Test
	public void shouldRejectIfCountryIsNull() {
		personalDetails.setCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "country");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.country.notempty", mappingResult.getFieldError("country").getCode());
	}

	@Test
	public void shouldRejectIfResidenceCountryIsNull() {
		personalDetails.setResidenceCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "residenceCountry");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.residenceCountry.notempty", mappingResult.getFieldError("residenceCountry").getCode());
	}

	@Test
	public void shouldRejectIfApplicationFormIsNull() {
		personalDetails.setApplication(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "application");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.application.notempty", mappingResult.getFieldError("application").getCode());
	}

	@Test
	public void shouldRejectIfNoCandidateNationality() {
		personalDetails.getCandidateNationalities().clear();
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "candidateNationalities");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.candidateNationalities.notempty", mappingResult.getFieldError("candidateNationalities").getCode());
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
		Assert.assertEquals("personalDetails.dateOfBirth.future", mappingResult.getFieldError("dateOfBirth").getCode());
	}

	@Test
	public void shouldRejectIfPhoneNumberIsEmpty() {
		personalDetails.setPhoneNumber(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "phoneNumber");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.phoneNumber.notempty", mappingResult.getFieldError("phoneNumber").getCode());
	}

	@Test
	public void shouldRejectIfDisabilityIsNull() {
		personalDetails.setDisability(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "disability");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.disability.notempty", mappingResult.getFieldError("disability").getCode());
	}

	@Test
	public void shouldRejectIfEthnicityIsNull() {
		personalDetails.setEthnicity(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "ethnicity");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.ethnicity.notempty", mappingResult.getFieldError("ethnicity").getCode());
	}
	
	@Test
	public void shouldRejectIfRequiresVisaIsNull() {
		personalDetails.setRequiresVisa(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "requiresVisa");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.requiresVisa.notempty", mappingResult.getFieldError("requiresVisa").getCode());
	}
	
	@Test
	public void shouldRejectIfEnglishFirstLanguageIsNull() {
		personalDetails.setEnglishFirstLanguage(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "englishFirstLanguage");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.englishFirstLanguage.notempty", mappingResult.getFieldError("englishFirstLanguage").getCode());
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

		personalDetailValidator = new PersonalDetailsValidator();
	}
}
