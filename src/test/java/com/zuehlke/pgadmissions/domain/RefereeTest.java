package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class RefereeTest {

	@Test
	public void shouldReturnTrueIfCommentOrDocumentProvided(){
		Referee referee = new Referee();
		assertFalse(referee.hasProvidedReference());
		referee.setComment("aha");
		assertTrue(referee.hasProvidedReference());
		referee.setComment(null);
		referee.setDocument(new Document());
		assertTrue(referee.hasProvidedReference());
	}
}
