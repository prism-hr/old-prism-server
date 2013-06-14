package com.zuehlke.pgadmissions.validators;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.validation.Validator;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public abstract class ValidatorTest<T> {

	@Autowired
	private Validator javaxValidator;
	protected AbstractValidator validator;
	protected BindingResult errors;
	private T object;

	public ValidatorTest() {
		super();
	}

	@Before
	public void setUp() {
		validator = createValidator();
		validator.setValidator(javaxValidator);
		object = createValidObject();
		setObject(object);
		errors = new BindException(object, getObjectName());
	}
	
	@Test
	public void shouldSupportObject() {
		resetMocks();
		assertThat(object, notNullValue());
		assertTrue(validator.supports(object.getClass()));
	}
	
	@Test
	public void shouldNotReject_ValidObject() {
		assertThatObjectHasNoErrors(object);
	}
	
	public void assertThatObjectHasNoErrors(T object) {
		validate(object);
		assertThat(errors.hasErrors(), is(false));
	}
	
	public void assertThatObjectFieldHasErrorCode(T object, String fieldName,String errorCode) {
		validate(object);
		assertThat("error count", errors.getErrorCount(), is(equalTo(1)));
		assertThat(String.format("%s error ", fieldName), errors.getFieldError(fieldName), notNullValue());
		assertThat(String.format("error code %s", fieldName), errors.getFieldError(fieldName).getCode(), is(errorCode));
	}

	public void assertThatObjectFieldHasErrorMessage(T object, String fieldName,String errorMessage) {
		validate(object);
		assertThat("error count", errors.getErrorCount(), is(equalTo(1)));
		assertThat(String.format("%s error ", fieldName), errors.getFieldError(fieldName), notNullValue());
		assertThat(String.format("error code %s", fieldName), errors.getFieldError(fieldName).getDefaultMessage(), is(errorMessage));
	}

	protected BindingResult validate(T object) {
		assertThat(object, notNullValue());
		ValidationUtils.invokeValidator(validator, object, errors);
		return errors;
	}

	/**
	 * Allows setting the <code>object</code> value to a local alias
	 * @param testObject target object for the validation
	 */
	protected abstract void setObject(T testObject);

	/**
	 * Creates an example of valid object to use throughout the tests
	 */
	protected abstract T createValidObject();

	/**
	 * Creates an instance of the validator under test
	 */
	protected abstract AbstractValidator createValidator();

	/**
	 * the alias of the target object to validate
	 */
	protected abstract String getObjectName();

	
	/**
	 * should reset all the mocks used,if any
	 */
	protected void resetMocks() {};

}