package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class DocumentValidatorTest {

    @Autowired  
    private Validator validator;  
    
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
	
	@Test
	@Ignore
	public void shouldNotAllowDamagedPdf() throws IOException {
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(document, "document");
	    document.setFileName("damaged.pdf");
	    document.setContent(FileUtils.readFileToByteArray(new File("src/test/resources/pdf/damaged.pdf")));
	    documentValidator.validate(document, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	@Ignore
    public void shouldAllowValidPdf() throws IOException {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(document, "document");
        documentValidator.validate(document, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

	@Before
	public void setup() throws IOException {
		document = new DocumentBuilder()
		    .fileName("valid.pdf")
		    .type(DocumentType.CV)
		    .content(FileUtils.readFileToByteArray(new File("src/test/resources/pdf/valid.pdf")))
		    .build();
		
		documentValidator = new DocumentValidator();
		documentValidator.setValidator((javax.validation.Validator) validator);
	}
}
