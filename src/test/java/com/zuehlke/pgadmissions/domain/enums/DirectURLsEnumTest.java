package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;

public class DirectURLsEnumTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("/pgadmissions/interviewFeedback?applicationId=", DirectURLsEnum.ADD_INTERVIEW.displayValue());
		Assert.assertEquals("/pgadmissions/reviewFeedback?applicationId=", DirectURLsEnum.ADD_REVIEW.displayValue());
		Assert.assertEquals("/pgadmissions/referee/addReferences?application=", DirectURLsEnum.ADD_REFERENCE.displayValue());
	}
}
