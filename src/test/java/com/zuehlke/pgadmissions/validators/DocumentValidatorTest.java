package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

public class DocumentValidatorTest {

	private Document document;
	private DocumentValidator documentValidator;

	@Test
	public void shouldSuppoertDocument() {
		assertTrue(documentValidator.supports(Document.class));
	}
	
	

	@Test
	public void shouldRejectIfFileNameisMissing() {
		document.setFileName("");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(document, "document");
			
		documentValidator.validate(document, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("upload.file.missing",mappingResult.getFieldError("fileName").getCode());
	}
	
	@Test
	public void shouldRejectIfFileNameNotInWhitelist() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(document, "document");
		document.setFileName("bob.exe");			
		documentValidator.validate(document, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("upload.file.invalidtype",mappingResult.getFieldError("fileName").getCode());
		
		mappingResult = new DirectFieldBindingResult(document, "document");
		document.setFileName("bob");			
		documentValidator.validate(document, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldAllowPDFonly() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(document, "document");
		
		document.setFileName("bob.pdf");			
		documentValidator.validate(document, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
		
	
	}
	

	@Before
	public void setup() {
		document = new DocumentBuilder().fileName("bob.txt").type(DocumentType.CV).toDocument();
		documentValidator = new DocumentValidator();
	}
}
