package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;


public class LanguageAptitudeTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("Elementary", LanguageAptitude.ELEMENTARY.getDisplayValue());
		Assert.assertEquals("Full professional proficiency", LanguageAptitude.FULL.getDisplayValue());
		Assert.assertEquals("Limited working proficiency", LanguageAptitude.LIMITED.getDisplayValue());
		Assert.assertEquals("Native/multilingual proficiency", LanguageAptitude.NATIVE.getDisplayValue());
		Assert.assertEquals("Professional working proficiency", LanguageAptitude.PROFESSIONAL.getDisplayValue());
	}
}
