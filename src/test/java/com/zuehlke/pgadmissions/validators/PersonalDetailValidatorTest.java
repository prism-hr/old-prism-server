package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;

public class PersonalDetailValidatorTest {

	private PersonalDetail personalDetails;
	private PersonalDetailValidator personalDetailValidator;

	@Test
	public void shouldSupportPersonalDetail() {
		assertTrue(personalDetailValidator.supports(PersonalDetail.class));
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
	public void shouldRejectIfDOBISFutureDate(){
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
	
	
	
	@Before
	public void setup(){
		Country nationality = new Country();
		personalDetails = new PersonalDetailsBuilder().candiateNationalities(nationality).maternalGuardianNationalities(nationality).paternalGuardianNationalities(nationality).applicationForm(new ApplicationFormBuilder().id(2).toApplicationForm()).country(new CountryBuilder().toCountry()).dateOfBirth(new Date()).email("email@test.com").firstName("bob")
		.gender(Gender.PREFER_NOT_TO_SAY).lastName("smith").residenceCountry(new CountryBuilder().toCountry()).toPersonalDetails();
		
		personalDetailValidator = new PersonalDetailValidator();
	}
}
