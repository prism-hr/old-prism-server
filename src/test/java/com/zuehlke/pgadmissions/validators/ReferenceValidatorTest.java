package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class ReferenceValidatorTest {

    @Autowired
	private ReferenceValidator validator;
	private ReferenceComment reference;

	@Test
	public void shouldSupporReference(){
		assertTrue(validator.supports(ReferenceComment.class));
	}
	@Test
	public void shouldAcceptReference(){

		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "documents");
		validator.validate(reference, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	@Test
	public void shouldRejectIfFileIsMissing(){
		reference.setDocuments(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "documents");
		validator.validate(reference, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfCommentIsMissing(){
		reference.setComment(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "comment");
		validator.validate(reference, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfSuitableForUCLIsNotSelected(){
		reference.setSuitableForUCL(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "suitableForUCL");
		validator.validate(reference, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForUCL").getCode());
	}
	
	@Test
	public void shouldRejectIfSuitableForProgrammeIsNotSelected(){
		reference.setSuitableForProgramme(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "suitableForProgramme");
		validator.validate(reference, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForProgramme").getCode());

	}

	@Before
	public void setup() throws ParseException{
		reference = new ReferenceComment();
		reference.getDocuments().add(new DocumentBuilder().id(1).toDocument());
		reference.setComment("comment");
		reference.setSuitableForProgramme(false);
		reference.setSuitableForUCL(false);
	}
}
