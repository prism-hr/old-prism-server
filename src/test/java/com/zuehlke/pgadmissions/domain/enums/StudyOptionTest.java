package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;


public class StudyOptionTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("Full time", StudyOption.FULL_TIME.displayValue());
		Assert.assertEquals("Full time by distance", StudyOption.FULL_TIME_DISTANCE.displayValue());
		Assert.assertEquals("Part time", StudyOption.PART_TIME.displayValue());
		Assert.assertEquals("Part time by distance", StudyOption.PART_TIME_DISTANCE.displayValue());
		Assert.assertEquals("Modular/flexible study", StudyOption.MODULAR.displayValue());
	}
	
	@Test
	public void shouldReturnCorrectValuesFromString(){
		Assert.assertEquals(StudyOption.FULL_TIME, StudyOption.fromString("Full time"));
		Assert.assertEquals(StudyOption.FULL_TIME_DISTANCE, StudyOption.fromString("Full time by distance"));
		Assert.assertEquals(StudyOption.PART_TIME, StudyOption.fromString("Part time"));
		Assert.assertEquals(StudyOption.PART_TIME_DISTANCE, StudyOption.fromString("Part time by distance"));
		Assert.assertEquals(StudyOption.MODULAR, StudyOption.fromString("Modular/flexible study"));
	}
	
	@Test
	public void shouldReturnNullFromNullString(){
		Assert.assertNull(StudyOption.fromString(null));
	}
}
