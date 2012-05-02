package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ValidationCommentTest {
	@Test
	public void shouldReturnValidation(){
		assertEquals(CommentType.VALIDATION, new ValidationComment().getType());
	}
}
