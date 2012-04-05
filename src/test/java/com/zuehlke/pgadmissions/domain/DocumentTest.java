package com.zuehlke.pgadmissions.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

public class DocumentTest {
	
	@Test
	public void shouldCreateDocument() throws ParseException{
		Document document = new DocumentBuilder().id(1).applicationForm(new ApplicationForm()).fileName("file.txt")
				.type(DocumentType.CV).dateUploaded(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"))
				.toDocument();
		Assert.assertNotNull(document.getFileName());
		Assert.assertNotNull(document.getApplicationForm());
		Assert.assertNotNull(document.getDateUploaded());
		Assert.assertNotNull(document.getId());
		Assert.assertNotNull(document.getType());
		
		
	}

}
