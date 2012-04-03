package com.zuehlke.pgadmissions.domain;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationReviewBuilder;


public class ApplicationReviewTest {
	@Test
	public void shouldCreateNewApplicationReview() throws ParseException{
		ApplicationForm application = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser user = new RegisteredUser();
		ApplicationReview applicationReview = new ApplicationReviewBuilder().id(1).application(application)
				.comment("comment").user(user).toApplicationReview();
		Assert.assertNotNull(applicationReview.getComment());
		Assert.assertNotNull(applicationReview.getApplication());
		Assert.assertNotNull(applicationReview.getId());
		Assert.assertNotNull(applicationReview.getUser());
	}
}
