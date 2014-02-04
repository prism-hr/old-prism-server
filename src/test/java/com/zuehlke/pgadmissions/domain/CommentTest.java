package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class CommentTest {

	@Test
	public void shouldReturnGEneric(){
		assertEquals(CommentType.GENERIC, new Comment().getType());
	}
}
