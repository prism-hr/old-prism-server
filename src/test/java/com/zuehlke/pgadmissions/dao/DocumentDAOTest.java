package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;

public class DocumentDAOTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveDocument(){
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).toDocument();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);
		
		flushAndClearSession();
		
		Document reloadDocument = (Document) sessionFactory.getCurrentSession().get(Document.class, document.getId());
		assertEquals(document, reloadDocument);
	}
	

	@Test
	public void shouldGetDocumentById(){
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).toDocument();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);
		
		flushAndClearSession();
		
		Document reloadDocument = dao.getDocumentbyId(document.getId());
		assertEquals(document, reloadDocument);
	}
}
