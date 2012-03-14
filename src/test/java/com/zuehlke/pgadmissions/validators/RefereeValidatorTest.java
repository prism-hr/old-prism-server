package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Telephone;
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

	@Ignore
	@Test
	public void shouldRejectIfEmailIsEmpty() {
		referee.setEmail(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "email");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.email.notempty", mappingResult.getFieldError("email").getCode());
	}
	
	@Test
	public void shouldRejectIfRelationshipIsEmpty() {
		referee.setRelationship(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "relationship");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.relationship.notempty", mappingResult.getFieldError("relationship").getCode());
	}
	
	@Test
	public void shouldRejectIfAddressLocationIsEmptyButAddressPostcodeAndCountryAreNot() {
		referee.setAddressLocation(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "addressLocation");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.addressLocation.notempty", mappingResult.getFieldError("addressLocation").getCode());
	}
	
	@Test
	public void shouldRejectIfAddressPostCodeIsEmptyButAddressLocationAndCountryAreNot() {
		referee.setAddressPostcode(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "addressPostcode");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.addressPostcode.notempty", mappingResult.getFieldError("addressPostcode").getCode());
	}
	
	@Test
	public void shouldRejectIfAddressCountryIsEmptyButAddressPostcodeAndLocationAreNot() {
		referee.setAddressCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "addressCountry");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.addressCountry.notempty", mappingResult.getFieldError("addressCountry").getCode());
	}
	
	@Test
	public void shouldRejectIfAddressCountryAndAddressPostcodeAreEmptyButAddressLocationIsNot() {
		referee.setAddressCountry(null);
		referee.setAddressPostcode(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "addressCountry");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("referee.addressCountry.notempty", mappingResult.getFieldError("addressCountry").getCode());
		Assert.assertEquals("referee.addressPostcode.notempty", mappingResult.getFieldError("addressPostcode").getCode());
	}
	@Test
	public void shouldRejectIfJobEmployeeIsEmptyAndJobTitleIsNot() {
		referee.setJobEmployer(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "jobEmployer");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.jobEmployer.notempty", mappingResult.getFieldError("jobEmployer").getCode());
	}
	@Test
	public void shouldRejectIfJobTitleIsEmptyAndJobEmployeeIsNot() {
		referee.setJobTitle(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "jobTitle");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.jobTitle.notempty", mappingResult.getFieldError("jobTitle").getCode());
	}
	@Test
	public void shouldNotRejectIfJobFieldsareBothEmpty() {
		referee.setJobTitle(null);
		referee.setJobEmployer(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "jobTitle");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldNotRejectIfJobFieldsareBothSet() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "jobTitle");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldNotRejectIfAllAddressFieldsAreEmpty() {
		referee.setAddressCountry(null);
		referee.setAddressLocation(null);
		referee.setAddressPostcode(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "addressCountry");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	@Test
	public void shouldNotRejectIfAllAddressFieldsAreSet() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "addressCountry");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
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
	public void shouldRejectIfNoTelephones() {
		referee.setPhoneNumbersRef(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(referee, "phoneNumbersRef");
		refereeValidator.validate(referee, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("referee.phoneNumbersRef.notempty", mappingResult.getFieldError("phoneNumbersRef").getCode());
	}
	
	@Before
	public void setup(){
		referee = new RefereeBuilder().application(new ApplicationFormBuilder().id(2).toApplicationForm()).email("email@test.com").firstname("bob")
				.lastname("smith").addressCountry("uk").addressLocation("london").addressPostcode("postcode").jobEmployer("zuhlke").jobTitle("se")
				.messenger(new Messenger()).phoneNumbers(new Telephone()).relationship("friend").toReferee();
		
		refereeValidator = new RefereeValidator();
	}
	
}
