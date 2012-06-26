package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;

public class DocumentServiceTest {

	private DocumentDAO documentDAOMock;
	private DocumentService documentService;

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
		documentService = new DocumentService(documentDAOMock);
	}
}
