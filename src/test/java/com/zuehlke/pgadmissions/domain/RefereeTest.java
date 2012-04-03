package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RefereeTest {

	@Test
	public void shouldReturnTrueIfRefernnceProvided(){
		Referee referee = new Referee();
		assertFalse(referee.hasProvidedReference());
		referee.setReference(new Reference());		
		assertTrue(referee.hasProvidedReference());
	}
}
