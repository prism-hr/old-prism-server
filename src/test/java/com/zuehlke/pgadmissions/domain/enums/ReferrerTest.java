package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;


public class ReferrerTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("UCL graduate study website", Referrer.OPTION_1.displayValue());
		Assert.assertEquals("UCL graduate study newsletter", Referrer.OPTION_2.displayValue());
		Assert.assertEquals("Student forum website", Referrer.OPTION_3.displayValue());
		Assert.assertEquals("Facebook alert, friend or page", Referrer.OPTION_4.displayValue());
		Assert.assertEquals("Facebook advert", Referrer.OPTION_5.displayValue());
		Assert.assertEquals("Study programme webpage", Referrer.OPTION_6.displayValue());
		Assert.assertEquals("Study programme newsletter", Referrer.OPTION_7.displayValue());
		Assert.assertEquals("Referral by friend or colleague", Referrer.OPTION_8.displayValue());
		Assert.assertEquals("Referral by detapartmental administrator", Referrer.OPTION_9.displayValue());
		Assert.assertEquals("Referral by UCL tutor/researcher", Referrer.OPTION_10.displayValue());
		Assert.assertEquals("Google advert/sponsored link", Referrer.OPTION_11.displayValue());
		Assert.assertEquals("Google search query", Referrer.OPTION_12.displayValue());
		Assert.assertEquals("FindAPhd.com", Referrer.OPTION_13.displayValue());
		Assert.assertEquals("HotCourses.com", Referrer.OPTION_14.displayValue());
		Assert.assertEquals("PostgraduateStudentships.co.uk", Referrer.OPTION_15.displayValue());
		Assert.assertEquals("Jobs.ac.uk", Referrer.OPTION_16.displayValue());
		Assert.assertEquals("FindAScholarship.com", Referrer.OPTION_17.displayValue());
		Assert.assertEquals("Prospects.ac.uk", Referrer.OPTION_18.displayValue());
		Assert.assertEquals("Prospects.ac.uk newsletter", Referrer.OPTION_19.displayValue());
	}
	
	@Test
	public void shouldReturnCorrectValuesFromString(){
		Assert.assertEquals(Referrer.OPTION_1, Referrer.fromString("UCL graduate study website"));
		Assert.assertEquals(Referrer.OPTION_2, Referrer.fromString("UCL graduate study newsletter"));
		Assert.assertEquals(Referrer.OPTION_3, Referrer.fromString("Student forum website"));
		Assert.assertEquals(Referrer.OPTION_4, Referrer.fromString("Facebook alert, friend or page"));
		Assert.assertEquals(Referrer.OPTION_5, Referrer.fromString("Facebook advert"));
		Assert.assertEquals(Referrer.OPTION_6, Referrer.fromString("Study programme webpage"));
		Assert.assertEquals(Referrer.OPTION_7, Referrer.fromString("Study programme newsletter"));
		Assert.assertEquals(Referrer.OPTION_8, Referrer.fromString("Referral by friend or colleague"));
		Assert.assertEquals(Referrer.OPTION_9, Referrer.fromString("Referral by detapartmental administrator"));
		Assert.assertEquals(Referrer.OPTION_10, Referrer.fromString("Referral by UCL tutor/researcher"));
		Assert.assertEquals(Referrer.OPTION_11, Referrer.fromString("Google advert/sponsored link"));
		Assert.assertEquals(Referrer.OPTION_12, Referrer.fromString("Google search query"));
		Assert.assertEquals(Referrer.OPTION_13, Referrer.fromString("FindAPhd.com"));
		Assert.assertEquals(Referrer.OPTION_14, Referrer.fromString("HotCourses.com"));
		Assert.assertEquals(Referrer.OPTION_15, Referrer.fromString("PostgraduateStudentships.co.uk"));
		Assert.assertEquals(Referrer.OPTION_16, Referrer.fromString("Jobs.ac.uk"));
		Assert.assertEquals(Referrer.OPTION_17, Referrer.fromString("FindAScholarship.com"));
		Assert.assertEquals(Referrer.OPTION_18, Referrer.fromString("Prospects.ac.uk"));
		Assert.assertEquals(Referrer.OPTION_19, Referrer.fromString("Prospects.ac.uk newsletter"));
	}
	
	@Test
	public void shouldReturnNullFromNullString(){
		Assert.assertNull(Referrer.fromString(null));
	}
}
