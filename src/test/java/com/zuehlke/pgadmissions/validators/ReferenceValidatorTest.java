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
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ReferenceValidatorTest {

    @Autowired
    private Validator validator;
    
	private ReferenceValidator referenceValidator;
    
	private ReferenceComment reference;

	@Test
	public void shouldSupporReference(){
		assertTrue(referenceValidator.supports(ReferenceComment.class));
	}
	
	@Test
	public void shouldAcceptReference(){

		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "documents");
		referenceValidator.validate(reference, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfFileIsMissing(){
		reference.setDocument(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "documents");
		referenceValidator.validate(reference, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfCommentIsMissing(){
		reference.setContent(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "comment");
		referenceValidator.validate(reference, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfSuitableForUCLIsNotSelected(){
		reference.setSuitableForInstitution(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "suitableForUCL");
		referenceValidator.validate(reference, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForUCL").getCode());
	}
	
	@Test
	public void shouldRejectIfSuitableForProgrammeIsNotSelected(){
		reference.setSuitableForProgramme(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reference, "suitableForProgramme");
		referenceValidator.validate(reference, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("suitableForProgramme").getCode());

	}

	@Before
	public void setup() throws ParseException{
		reference = new ReferenceComment();
		reference.getDocuments().add(new DocumentBuilder().id(1).build());
		reference.setContent("comment");
		reference.setSuitableForProgramme(false);
		reference.setSuitableForInstitution(false);
		
		referenceValidator = new ReferenceValidator();
		referenceValidator.setValidator((javax.validation.Validator) validator);
	}
}
