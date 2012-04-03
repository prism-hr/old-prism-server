package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;


public class QualificationLevelTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("College", QualificationLevel.COLLEGE.getDisplayValue());
		Assert.assertEquals("Professional", QualificationLevel.PROFESSIONAL.getDisplayValue());
		Assert.assertEquals("School", QualificationLevel.SCHOOL.getDisplayValue());
		Assert.assertEquals("University", QualificationLevel.UNIVERSITY.getDisplayValue());
	}

}
