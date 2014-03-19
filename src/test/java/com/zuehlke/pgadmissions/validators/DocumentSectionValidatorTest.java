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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class DocumentSectionValidatorTest {

    @Autowired  
    private Validator validator;  
    
	private DocumentSectionValidator documentSectionValidator;
    
	private ApplicationForm applicationForm;

	@Test
	public void shouldSupportApplicationForm() {
		assertTrue(documentSectionValidator.supports(ApplicationForm.class));
	}

	@Test
	public void shoulRejectIfPersonalStatementNotUploaded() {
		applicationForm.setPersonalStatement(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "personalStatement");
		
		documentSectionValidator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("file.upload.empty",mappingResult.getFieldError("personalStatement").getCode());
	}
	
	@Before
	public void setup() {
		applicationForm = new ApplicationFormBuilder().cv(new DocumentBuilder().type(DocumentType.CV).build())
				.personalStatement(new DocumentBuilder().type(DocumentType.PERSONAL_STATEMENT).build()).build();
		
		documentSectionValidator = new DocumentSectionValidator();
		documentSectionValidator.setValidator((javax.validation.Validator) validator);
	}
}
