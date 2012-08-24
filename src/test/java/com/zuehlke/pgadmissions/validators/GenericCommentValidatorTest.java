package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class GenericCommentValidatorTest {

    @Autowired
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
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}

	@Before
	public void setup() {
		comment = new CommentBuilder().comment("hi").toComment();
	}
}
