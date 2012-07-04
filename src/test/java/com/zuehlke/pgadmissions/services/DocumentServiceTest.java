package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;

public class DocumentServiceTest {

	private DocumentDAO documentDAOMock;
	private DocumentService documentService;
	private ApplicationFormDAO applicationDAOMock;

	@Test
	public void shouldRemovePersonalStatementFromApplicationAndDelete(){
		Document document = new DocumentBuilder().id(1).toDocument();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).personalStatement(document).toApplicationForm();
		documentDAOMock.deleteDocument(document);
		applicationDAOMock.save(applicationForm);
		EasyMock.replay(documentDAOMock, applicationDAOMock);
		documentService.deletePersonalStatement(applicationForm);
		EasyMock.verify(documentDAOMock, applicationDAOMock);
		assertNull(applicationForm.getPersonalStatement());		
	}

	@Test
	public void shouldNotFailIfPersonalStatementIsNull(){
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();

		applicationDAOMock.save(applicationForm);
		EasyMock.replay(documentDAOMock, applicationDAOMock);
		documentService.deletePersonalStatement(applicationForm);
		EasyMock.verify(documentDAOMock, applicationDAOMock);
	
	}
	@Test
	public void shouldRemoveCVFromApplicationAndDelete(){
		Document document = new DocumentBuilder().id(1).toDocument();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).cv(document).toApplicationForm();
		documentDAOMock.deleteDocument(document);
		applicationDAOMock.save(applicationForm);
		EasyMock.replay(documentDAOMock, applicationDAOMock);
		documentService.deleteCV(applicationForm);
		EasyMock.verify(documentDAOMock, applicationDAOMock);
		assertNull(applicationForm.getCv());		
	}
	
	@Test
	public void shouldNotFailIfCVIsNull(){
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationDAOMock.save(applicationForm);
		EasyMock.replay(documentDAOMock, applicationDAOMock);
		documentService.deleteCV(applicationForm);
		EasyMock.verify(documentDAOMock, applicationDAOMock);
			
	}
	
	@Test
	public void shouldDelegateSaveToDAO() {

		Document document = new DocumentBuilder().id(1).toDocument();
		documentDAOMock.save(document);
		EasyMock.replay(documentDAOMock);
		documentService.save(document);
		EasyMock.verify(documentDAOMock);

	}

	@Test
	public void shouldDelegateDeleteToDAO() {

		Document document = new DocumentBuilder().id(1).toDocument();
		documentDAOMock.deleteDocument(document);
		EasyMock.replay(documentDAOMock);
		documentService.delete(document);
		EasyMock.verify(documentDAOMock);

	}
	@Test
	public void shouldGetDocumentFroMDAO() {

		Document document = new DocumentBuilder().id(1).toDocument();
		EasyMock.expect(documentDAOMock.getDocumentbyId(1)).andReturn(document);
		EasyMock.replay(documentDAOMock);
		Document loadedDocument = documentService.getDocumentById(1);
		assertEquals(document, loadedDocument);

	}

	@Before
	public void setup() {
		documentDAOMock = EasyMock.createMock(DocumentDAO.class);
		applicationDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		documentService = new DocumentService(documentDAOMock, applicationDAOMock);
	}
}
