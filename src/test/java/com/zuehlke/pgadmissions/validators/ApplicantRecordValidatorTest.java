package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.ApplicantRecord;
import com.zuehlke.pgadmissions.domain.builders.ApplicantRecordBuilder;

public class ApplicantRecordValidatorTest {

	private ApplicantRecord record;
	private ApplicantRecordValidator recordValidator;
	
	@Test
	public void shouldSupportApplicantRecordValidator() {
		assertTrue(recordValidator.supports(ApplicantRecord.class));
	}

	@Before
	public void setup(){
		record = new ApplicantRecordBuilder().email("test@gmail.com").firstname("firstname").lastname("lastname").password("1234").confirmPassword("1234").toApplicantRecord();
		recordValidator = new ApplicantRecordValidator();
	}
	
	@Test
	public void shouldRejectIfFirstNameIsEmpty() {
		record.setFirstname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "firstname");
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.firstname.notempty", mappingResult.getFieldError("firstname").getCode());
	}

	@Test
	public void shouldRejectIfLasttNameIsEmpty() {
		record.setLastname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "lastname");
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.lastname.notempty", mappingResult.getFieldError("lastname").getCode());
	}

	@Ignore
	@Test
	public void shouldRejectIfEmailIsEmpty() {
		record.setEmail(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "email");
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.email.notempty", mappingResult.getFieldError("email").getCode());
	}
	
	@Test
	public void shouldRejectIfEmailNotValidEmail() {
		record.setEmail("nonvalidemail");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "email");
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.email.invalid", mappingResult.getFieldError("email").getCode());
	}
	@Test
	public void shouldRejectIfNoPassword() {
		record.setPassword(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "password");
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.password.notempty", mappingResult.getFieldError("password").getCode());
	}
	
	@Test
	public void shouldRejectIfNoConfirmPassword() {
		record.setConfirmPassword(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "confirmPassword");
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("record.confirmPassword.notempty", mappingResult.getFieldError("confirmPassword").getCode());
	}
	
	@Test
	public void shouldRejectIfPasswordsDoNotMatch() {
		record.setConfirmPassword("1234");
		record.setPassword("12");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "confirmPassword");
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("record.confirmPassword.notvalid", mappingResult.getFieldError("confirmPassword").getCode());
		Assert.assertEquals("record.password.notvalid", mappingResult.getFieldError("password").getCode());
	}
	
	@Test
	public void shouldNotRejectIfPasswordsMatch() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(record, "confirmPassword");
		recordValidator.validate(record, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
}
