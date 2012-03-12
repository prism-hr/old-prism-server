package com.zuehlke.pgadmissions.services;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;

public class DocumentServiceTest {

	@Test
	public void shouldDelegateSaveToDAO(){
		DocumentDAO documentDAOMock = EasyMock.createMock(DocumentDAO.class);
		DocumentService documentService = new DocumentService(documentDAOMock);
		Document document = new DocumentBuilder().id(1).toDocument();
		documentDAOMock.save(document);
		EasyMock.replay(documentDAOMock);
		documentService.save(document);
		EasyMock.verify(documentDAOMock);
		
	}
}
