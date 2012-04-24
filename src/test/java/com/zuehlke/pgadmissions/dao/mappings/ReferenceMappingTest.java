package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

public class ReferenceMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadReferenceWithAndDocument(){
		Document document = new DocumentBuilder().content("aa".getBytes()).fileName("gekko").type(DocumentType.CV).toDocument();


		Reference reference = new ReferenceBuilder().document(document).toReference();
		
		sessionFactory.getCurrentSession().save(reference);
		assertNotNull(reference.getId());
		assertNotNull(document.getId());
		Integer id = reference.getId();
		Reference reloadedReference = (Reference) sessionFactory.getCurrentSession().get(Reference.class, id);

		assertSame(reference, reloadedReference);

		flushAndClearSession();
		reloadedReference = (Reference) sessionFactory.getCurrentSession().get(Reference.class, id);

		assertNotSame(reference, reloadedReference);
		assertEquals(reference, reloadedReference);
		assertEquals(document,reloadedReference.getDocument());

		
		assertNotNull(reloadedReference.getLastUpdated());
	}
	
	
}

