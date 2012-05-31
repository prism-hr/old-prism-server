package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;

public class GenericCommentValidatorTest {
	private GenericCommentValidator validator;
	private Comment comment;

	@Test
	public void shouldSupportComment() {
		assertTrue(validator.supports(Comment.class));
	}

	@Test
	public void shouldRejectIfCommentIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(comment, "comment");
		comment.setComment("");
		validator.validate(comment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("comment.comment.notempty", mappingResult.getFieldError("comment").getCode());
	}

	@Before
	public void setup() {
		validator = new GenericCommentValidator();
		comment = new CommentBuilder().comment("hi").toComment();
	}
}
