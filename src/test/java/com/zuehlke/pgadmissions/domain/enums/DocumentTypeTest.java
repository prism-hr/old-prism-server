package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;

public class DocumentTypeTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("CV / resume", DocumentType.CV.getDisplayValue());
		Assert.assertEquals("Personal Statement", DocumentType.PERSONAL_STATEMENT.getDisplayValue());
		Assert.assertEquals("Reference", DocumentType.REFERENCE.getDisplayValue());
		Assert.assertEquals("Supporting documentation for address / residency period", DocumentType.SUPPORTING_ADDRESS.getDisplayValue());
		Assert.assertEquals("Supporting documentation for nationality", DocumentType.SUPPORTING_CANDIDATE_NATIONALITY.getDisplayValue());
		Assert.assertEquals("Supporting documentation for employment", DocumentType.SUPPORTING_EMPLOYMENT.getDisplayValue());
		Assert.assertEquals("Supporting documentation for funding", DocumentType.SUPPORTING_FUNDING.getDisplayValue());
		Assert.assertEquals("Supporting documentation for maternal guardian's nationality", DocumentType.SUPPORTING_MATERNAL_NATIONALITY.getDisplayValue());
		Assert.assertEquals("Supporting documentation for paternal guardian's nationality", DocumentType.SUPPORTING_PATERNAL_NATIONALITY.getDisplayValue());
		Assert.assertEquals("Supporting documentation for qualification", DocumentType.SUPPORTING_QUALIFICATION.getDisplayValue());
		
	}
}
