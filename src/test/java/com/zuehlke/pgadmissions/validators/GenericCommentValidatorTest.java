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
import org.springframework.validation.Validator;

import com.google.common.base.Strings;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class GenericCommentValidatorTest {

    @Autowired  
    private Validator validator;
    
    private GenericCommentValidator genericCommentValidator;
    
	private Comment comment;

	@Test
	public void shouldSupportComment() {
		assertTrue(genericCommentValidator.supports(Comment.class));
	}

	@Test
	public void shouldRejectIfCommentIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(comment, "comment");
		comment.setComment("");
		genericCommentValidator.validate(comment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
    public void shouldRejectIfCommentIsLongerThan2000Characters() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(comment, "comment");
        comment.setComment(Strings.repeat("a", 2100));
        genericCommentValidator.validate(comment, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 2000 characters are allowed.", mappingResult.getFieldError("comment").getDefaultMessage());
    }
	
	@Test
    public void shouldNotRejectIfCommentIsShorterThan2000Characters() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(comment, "comment");
        comment.setComment(Strings.repeat("a", 1900));
        genericCommentValidator.validate(comment, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

	@Before
	public void setup() {
		comment = new CommentBuilder().comment("hi").build();
		genericCommentValidator = new GenericCommentValidator();
		genericCommentValidator.setValidator((javax.validation.Validator) validator);
	}
}
