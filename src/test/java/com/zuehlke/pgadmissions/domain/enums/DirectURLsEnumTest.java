package com.zuehlke.pgadmissions.domain.enums;

import junit.framework.Assert;

import org.junit.Test;

public class DirectURLsEnumTest {

	@Test
	public void shouldOutputCorrectDisplayValues(){
		Assert.assertEquals("/application?view=view&applicationId=", DirectURLsEnum.VIEW_APPLIATION_PRIOR_TO_INTERVIEW.displayValue());
		Assert.assertEquals("/reviewFeedback?applicationId=", DirectURLsEnum.ADD_REVIEW.displayValue());
	}
}
