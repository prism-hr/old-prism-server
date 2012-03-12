package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Document;

public class DocumentMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadDocument(){
		Document document = new Document();
		StringBuilder builder = new StringBuilder();
		for(int i =0 ; i < 50000;i++){
			builder.append("a");
		}
		String contentString = builder.toString();
		document.setContent(contentString.getBytes());
		document.setFileName("name.txt");
		
		sessionFactory.getCurrentSession().save(document);
		assertNotNull(document.getId());
		
		Document reloadedDoc = (Document) sessionFactory.getCurrentSession().get(Document.class, document.getId());
		assertSame(document, reloadedDoc);
		
		flushAndClearSession();
		
		reloadedDoc = (Document) sessionFactory.getCurrentSession().get(Document.class, document.getId());
		assertNotSame(document, reloadedDoc);
		assertEquals(document,reloadedDoc);
		assertEquals("name.txt", reloadedDoc.getFileName());
		assertEquals(contentString, new String(reloadedDoc.getContent()));
		
	}
}
