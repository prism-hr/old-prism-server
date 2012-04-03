package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;


public class ResidenceStatusTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("Exceptional leave to remain", ResidenceStatus.EXCEPTIONAL_LEAVE_TO_REMAIN.getDisplayValue());
		Assert.assertEquals("Indefinite right to remain", ResidenceStatus.INDEFINITE_RIGHT_TO_REMAIN.getDisplayValue());
		Assert.assertEquals("Nationality", ResidenceStatus.NATIONALITY.getDisplayValue());
		Assert.assertEquals("Refugee Status", ResidenceStatus.REFUGEE_STATUS.getDisplayValue());
		Assert.assertEquals("Right of abode", ResidenceStatus.RIGHT_OF_ABODE.getDisplayValue());
	}
}
