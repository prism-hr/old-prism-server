package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;


public class StudyOptionTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("Full-time", StudyOption.FULL_TIME.displayValue());
		Assert.assertEquals("Full-time: distance learning", StudyOption.FULL_TIME_DISTANCE_LEARNING.displayValue());
		Assert.assertEquals("Part-time", StudyOption.PART_TIME.displayValue());
		Assert.assertEquals("Part-time: distance learning", StudyOption.PART_TIME_DISTANCE_LEARNING.displayValue());
		Assert.assertEquals("Modular/flexible study", StudyOption.MODULAR_FLEXIBLE_STUDY.displayValue());
	}
	
	@Test
	public void shouldReturnCorrectValuesFromString(){
		Assert.assertEquals(StudyOption.FULL_TIME, StudyOption.fromString("Full-time"));
		Assert.assertEquals(StudyOption.FULL_TIME_DISTANCE_LEARNING, StudyOption.fromString("Full-time: distance learning"));
		Assert.assertEquals(StudyOption.PART_TIME, StudyOption.fromString("Part-time"));
		Assert.assertEquals(StudyOption.PART_TIME_DISTANCE_LEARNING, StudyOption.fromString("Part-time: distance learning"));
		Assert.assertEquals(StudyOption.MODULAR_FLEXIBLE_STUDY, StudyOption.fromString("Modular/flexible study"));
	}
	
	@Test
	public void shouldReturnNullFromNullString(){
		Assert.assertNull(StudyOption.fromString(null));
	}
}
