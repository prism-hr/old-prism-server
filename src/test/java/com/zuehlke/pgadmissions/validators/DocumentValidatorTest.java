package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
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
	public void shouldSupportDocument() {
		assertTrue(documentValidator.supports(Document.class));
	}
	
	@Test
	public void shouldRejectIfMoreThan10Mb() {

		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 11000000; i++ ){
			sb.append("a");
		}
		document.setContent(sb.toString().getBytes());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(document, "document");
			
		documentValidator.validate(document, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("file.upload.large",mappingResult.getFieldError("content").getCode());
	}
	
	@Test
	public void shouldRejectIfFileNameisMissing() {
		document.setFileName("");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(document, "document");
			
		documentValidator.validate(document, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("file.upload.empty",mappingResult.getFieldError("fileName").getCode());
	}
	
	@Test
	public void shouldRejectIfFileNameMoreThan200Chars() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 200; i++ ){
			sb.append("a");
		}
		document.setFileName(sb.toString() + ".pdf");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(document, "document");
			
		documentValidator.validate(document, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("upload.file.toolong",mappingResult.getFieldError("fileName").getCode());
	}
	
	@Test
	public void shouldRejectIfFileNameNotInWhitelist() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(document, "document");
		document.setFileName("bob.exe");			
		documentValidator.validate(document, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("file.upload.notPDF",mappingResult.getFieldError("fileName").getCode());
		
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
	@Test
	public void shouldAllowDotsInFilelanes() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(document, "document");
		
		document.setFileName("bob_v1.1.pdf");			
		documentValidator.validate(document, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
		
	
	}

	@Before
	public void setup() {
		document = new DocumentBuilder().fileName("bob.pdf").type(DocumentType.CV).toDocument();
		documentValidator = new DocumentValidator();
	}
}
