package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;

public class DocumentTypeTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("CV / resume", DocumentType.CV.getDisplayValue());
		Assert.assertEquals("Personal Statement", DocumentType.PERSONAL_STATEMENT.getDisplayValue());
		Assert.assertEquals("Reference", DocumentType.REFERENCE.getDisplayValue());
		Assert.assertEquals("Supporting documentation for funding", DocumentType.SUPPORTING_FUNDING.getDisplayValue());
		
	}
}
