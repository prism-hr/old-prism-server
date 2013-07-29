package com.zuehlke.pgadmissions.validators;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.builders.PersonBuilder;

public class PersonValidatorTest extends ValidatorTest<Person> {
    
    private Person person;
	
	@Test
	public void shouldRejectIf_FirstName_IsEmpty() {
		person.setFirstname("");
		assertThatObjectFieldHasErrorCode(person, "firstname", "text.field.empty");
	}		

	@Test
	public void shouldRejectIf_FirstName_IsMissing() {
		person.setFirstname("");
	person.setFirstname(null);
	assertThatObjectFieldHasErrorCode(person, "firstname", "text.field.empty");
	}

	@Test
	public void shouldRejectIf_LastName_IsEmpty() {
		person.setLastname("");
		assertThatObjectFieldHasErrorCode(person, "lastname", "text.field.empty");
	}

	@Test
	public void shouldRejectIf_LastName_IsMissing() {
		person.setLastname(null);
		assertThatObjectFieldHasErrorCode(person, "lastname", "text.field.empty");
	}

	@Test
	public void shouldRejectIf_Email_IsEmpty() {
		person.setEmail("");
		assertThatObjectFieldHasErrorCode(person, "email", "text.field.empty");
	}
	
	@Test
	public void shouldRejectIf_Email_IsMissing() {
		person.setEmail(null);
		assertThatObjectFieldHasErrorCode(person, "email", "text.field.empty");
	}

	@Test
	public void shouldRejectIf_Email_IsInvalid() {
		person.setEmail("invalidEmail");
		assertThatObjectFieldHasErrorMessage(person, "email", "You must enter a valid email address.");
	}
	
	@Override
	protected void setObject(Person dto) {
		person = dto;
		
	}

	@Override
	protected Person createValidObject() {
		PersonBuilder builder = new PersonBuilder();
		builder.id(1).firstname("First").lastname("Last").email("email@test.com");
		return builder.build();
	}

	@Override
	protected AbstractValidator createValidator() {
		return new PersonValidator();
	}

	@Override
	protected String getObjectName() {
		return "person";
	}
}
