package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;

public class ApplicationFormValidatorTest {

	private ApplicationFormValidator validator;
	private ApplicationForm applicationForm;

	@Test
	public void shouldSupportAppForm() {
		assertTrue(validator.supports(ApplicationForm.class));
	}

	@Test
	public void shouldRejectIfProgrammeDetailsSectionMissing() {
		applicationForm.setProgrammeDetails(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.programmeDetails.incomplete", mappingResult.getFieldError("programmeDetails").getCode());
	}
	@Test
	public void shouldRejectIfProgrammeDetailsSectionNotSaved() {
		applicationForm.setProgrammeDetails(new ProgrammeDetails());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.programmeDetails.incomplete", mappingResult.getFieldError("programmeDetails").getCode());
	}
	@Test
	public void shouldRejectIfPersonalDetailsSectionMissing() {
		applicationForm.setPersonalDetails(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.personalDetails.incomplete", mappingResult.getFieldError("personalDetails").getCode());

	}
	@Test
	public void shouldRejectIfPersonalDetailsSectionNotSaved() {
		applicationForm.setPersonalDetails(new PersonalDetails());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.personalDetails.incomplete", mappingResult.getFieldError("personalDetails").getCode());

	}

	@Test
	public void shouldRejectIfCurrentAddressIsMissing() {
		applicationForm.setCurrentAddress(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.addresses.notempty", mappingResult.getFieldError("currentAddress").getCode());

	}

	@Test
	public void shouldRejectIfContactAddressIsMissing() {
		applicationForm.setContactAddress(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.addresses.notempty", mappingResult.getFieldError("contactAddress").getCode());

	}
	
	@Test
	public void shouldRejectIfFewerThanThreeReferees() {
		applicationForm.getReferees().remove(2);		
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.referees.notvalid", mappingResult.getFieldError("referees").getCode());

	}
	
	@Test
	public void shouldRejectIfPersonalStatementNotProvided() {
		applicationForm.setPersonalStatement(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("documents.section.invalid", mappingResult.getFieldError("personalStatement").getCode());

	}
	@Before
	public void setup() throws ParseException {
		validator = new ApplicationFormValidator();
		applicationForm = new ApplicationFormBuilder().programmeDetails(new ProgrammeDetailsBuilder().id(2).toProgrammeDetails()).personalDetails(new PersonalDetailsBuilder().id(1).toPersonalDetails())
				.currentAddress(new Address()).contactAddress(new Address()).referees(new Referee(), new Referee(), new Referee()).personalStatement(new Document()).toApplicationForm();
	}

}
