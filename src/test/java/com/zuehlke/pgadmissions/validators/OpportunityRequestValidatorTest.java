package com.zuehlke.pgadmissions.validators;

import static com.zuehlke.pgadmissions.validators.AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import javax.validation.Validator;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class OpportunityRequestValidatorTest {

	@Autowired
	private Validator validator;

	private RegisterFormValidator registerFormValidatorMock;

	private OpportunityRequestValidator opportunityRequestValidator;

	private OpportunityRequest opportunityRequest;

	private RegisteredUser author;

	@Before
	public void setUp() {
		Domicile institutionCountry = new DomicileBuilder().code("PL").build();
		author = new RegisteredUser();
		opportunityRequest = new OpportunityRequestBuilder().institutionCountry(institutionCountry).institutionCode("AGH").programTitle("Title").programDescription("Desc").author(author).build();

		registerFormValidatorMock = EasyMock.createMock(RegisterFormValidator.class);

		opportunityRequestValidator = new OpportunityRequestValidator();
		opportunityRequestValidator.setValidator(validator);
		opportunityRequestValidator.setRegisterFormValidator(registerFormValidatorMock);
	}

	@Test
	public void shouldSupportProgramAdvertClass() {
		assertTrue(opportunityRequestValidator.supports(OpportunityRequest.class));
	}

	@Test
	public void shouldRejectIfInstitutionCountryIsNull() {
		opportunityRequest.setInstitutionCountry(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

		configureAndReplayRegisterFormValidator(mappingResult);
		opportunityRequestValidator.validate(opportunityRequest, mappingResult);

		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals(EMPTY_DROPDOWN_ERROR_MESSAGE, mappingResult.getFieldError("institutionCountry").getCode());
	}

	@Test
	public void shouldRejectIfInstitutionCodeIsNull() {
		opportunityRequest.setInstitutionCode(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

		configureAndReplayRegisterFormValidator(mappingResult);
		opportunityRequestValidator.validate(opportunityRequest, mappingResult);

		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals(AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE, mappingResult.getFieldError("institutionCode").getCode());
	}

	@Test
	public void shouldRejectIfProgramTitleIsNull() {
		opportunityRequest.setProgramTitle(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

		configureAndReplayRegisterFormValidator(mappingResult);
		opportunityRequestValidator.validate(opportunityRequest, mappingResult);

		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals(AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE, mappingResult.getFieldError("programTitle").getCode());
	}

	@Test
	public void shouldRejectIfOtherInstitutionIsNullWhenOtherInstitutionChosen() {
		opportunityRequest.setInstitutionCode("OTHER");
		opportunityRequest.setOtherInstitution(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

		configureAndReplayRegisterFormValidator(mappingResult);
		opportunityRequestValidator.validate(opportunityRequest, mappingResult);

		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals(AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE, mappingResult.getFieldError("otherInstitution").getCode());
	}

	@Test
	public void shouldRejectIfProgramDescriptionIsNull() {
		opportunityRequest.setProgramDescription(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

		configureAndReplayRegisterFormValidator(mappingResult);
		opportunityRequestValidator.validate(opportunityRequest, mappingResult);

		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals(AbstractValidator.EMPTY_FIELD_ERROR_MESSAGE, mappingResult.getFieldError("programDescription").getCode());
	}

	@Test
	public void shouldFindNoErrorsAndInvokeRegisterFormValidator() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

		configureAndReplayRegisterFormValidator(mappingResult);
		opportunityRequestValidator.validate(opportunityRequest, mappingResult);

		verify(registerFormValidatorMock);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}

	private void configureAndReplayRegisterFormValidator(Errors errors) {
		expect(registerFormValidatorMock.supports(RegisteredUser.class)).andReturn(true);
		registerFormValidatorMock.validate(author, errors);

		replay(registerFormValidatorMock);
	}

}
