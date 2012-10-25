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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class DocumentSectionValidatorTest {

    @Autowired
	private DocumentSectionValidator validator;
    
	private ApplicationForm applicationForm;

	@Test
	public void shouldSupportApplicationForm() {
		assertTrue(validator.supports(ApplicationForm.class));
	}

	@Test
	public void shoulRejectIfPersonalStatementNotUploaded() {
		applicationForm.setPersonalStatement(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "personalStatement");
		
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("file.upload.empty",mappingResult.getFieldError("personalStatement").getCode());
	}
	
	@Before
	public void setup() {
		applicationForm = new ApplicationFormBuilder().cv(new DocumentBuilder().type(DocumentType.CV).toDocument())
				.personalStatement(new DocumentBuilder().type(DocumentType.PERSONAL_STATEMENT).toDocument()).toApplicationForm();
	}
}
