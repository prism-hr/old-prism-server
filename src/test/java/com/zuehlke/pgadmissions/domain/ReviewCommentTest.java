package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ReviewCommentTest {
	@Test
	public void shouldReturnReview(){
		assertEquals(CommentType.REVIEW, new ReviewComment().getType());
	}
}
