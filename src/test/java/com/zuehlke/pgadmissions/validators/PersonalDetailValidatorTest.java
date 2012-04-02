package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageProficiencyBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;

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
	public void shouldRejectIfResidenceFromDateIsNull() {
		personalDetails.setResidenceFromDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "residenceFromDate");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.residenceFromDate.notempty", mappingResult.getFieldError("residenceFromDate").getCode());
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
	public void shouldRejectIfResidenceStatusIsNull() {
		personalDetails.setResidenceStatus(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "residenceStatus");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.residenceStatus.notempty", mappingResult.getFieldError("residenceStatus").getCode());
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
	public void shouldRejectIfNolanguageProficiency() {
		personalDetails.getLanguageProficiencies().clear();
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(personalDetails, "languageProficiencies");
		personalDetailValidator.validate(personalDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("personalDetails.languageProficiencies.notempty", mappingResult.getFieldError("languageProficiencies").getCode());
	}
	
	
	@Before
	public void setup(){
		Nationality nationality = new Nationality();
		LanguageProficiency languageProficiency = new LanguageProficiencyBuilder().id(1).toLanguageProficiency();
		personalDetails = new PersonalDetailsBuilder().candiateNationalities(nationality).languageProficiencies(languageProficiency).maternalGuardianNationalities(nationality).paternalGuardianNationalities(nationality).applicationForm(new ApplicationFormBuilder().id(2).toApplicationForm()).country(new CountryBuilder().toCountry()).dateOfBirth(new Date()).email("email@test.com").firstName("bob")
		.gender(Gender.PREFER_NOT_TO_SAY).lastName("smith").residenceCountry(new CountryBuilder().toCountry()).residenceFromDate(new Date()).residenceStatus(ResidenceStatus.EXCEPTIONAL_LEAVE_TO_REMAIN).toPersonalDetails();
		
		personalDetailValidator = new PersonalDetailValidator();
	}
}
