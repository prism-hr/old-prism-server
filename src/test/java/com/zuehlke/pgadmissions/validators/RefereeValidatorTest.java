package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;

public class RefereeValidatorTest {

	
	private Referee referee;
	private RefereeValidator refereeValidator;
	
	@Test
	public void shouldSupportRefereeValidator() {
		assertTrue(refereeValidator.supports(Referee.class));
	}
	
	@Test
	public void shouldRejectIfFirstNameIsEmpty() {
		referee.setFirstname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "firstname");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.firstname.notempty", mappingResult.getFieldError("firstname").getCode());
	}

	@Test
	public void shouldRejectIfLasttNameIsEmpty() {
		referee.setLastname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "lastname");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.lastname.notempty", mappingResult.getFieldError("lastname").getCode());
	}

	@Test
	public void shouldRejectIfAddressLocationIsEmpty() {
		referee.setAddressLocation(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "addressLocation");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.addressLocation.notempty", mappingResult.getFieldError("addressLocation").getCode());
	}
	
	
	@Test
	public void shouldRejectIfAddressCountryIsEmpty() {
		referee.setAddressCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "addressCountry");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.addressCountry.notempty", mappingResult.getFieldError("addressCountry").getCode());
	}
	

	@Test
	public void shouldRejectIfJobEmployeeIsEmpty() {
		referee.setJobEmployer(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "jobEmployer");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.jobEmployer.notempty", mappingResult.getFieldError("jobEmployer").getCode());
	}
	@Test
	public void shouldRejectIfJobTitleIsEmpty() {
		referee.setJobTitle(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "jobTitle");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.jobTitle.notempty", mappingResult.getFieldError("jobTitle").getCode());
	}	
		
	@Test
	public void shouldRejectIfEmailNotValidEmail() {
		referee.setEmail("nonvalidemail");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "email");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.email.invalid", mappingResult.getFieldError("email").getCode());
	}
	
	@Test
	public void shouldRejectIfNoTelephone() {
		referee.setPhoneNumber(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "phoneNumber");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.phoneNumber.notempty", mappingResult.getFieldError("phoneNumber").getCode());
	}
	
	@Test
	public void shouldRejectIfAddressTooLong() {
		StringBuilder addressLoc = new StringBuilder();
		for (int i = 0; i <=200; i++) {
			addressLoc.append("a");
		}
		referee.setAddressLocation(addressLoc.toString());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "phoneNumber");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Before
	public void setup(){
		referee = new RefereeBuilder().application(new ApplicationFormBuilder().id(2).toApplicationForm()).email("email@test.com").firstname("bob")
				.lastname("smith").addressCountry(new Country()).addressLocation("london").jobEmployer("zuhlke").jobTitle("se")
				.messenger("skypeAddress").phoneNumber("hallihallo").toReferee();
		
		refereeValidator = new RefereeValidator();
	}
	
}
