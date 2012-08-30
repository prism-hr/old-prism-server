package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;


public class GenderTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("Female", Gender.FEMALE.getDisplayValue());
		Assert.assertEquals("Male", Gender.MALE.getDisplayValue());
		Assert.assertEquals("Indeterminate", Gender.INDETERMINATE.getDisplayValue());
	}
}
