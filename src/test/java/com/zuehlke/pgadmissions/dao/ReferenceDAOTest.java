package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.builders.ReferenceBuilder;

public class ReferenceDAOTest extends AutomaticRollbackTestCase {

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		ReferenceDAO referenceDAO = new ReferenceDAO();
		referenceDAO.getReferenceById(1);
	}
	
	@Test
	public void shouldGetReferenceById(){
		
		Reference reference = new ReferenceBuilder().toReference();
		sessionFactory.getCurrentSession().save(reference);
		flushAndClearSession();
		
		ReferenceDAO referenceDAO = new ReferenceDAO(sessionFactory);
		Reference returnedReference = referenceDAO.getReferenceById(reference.getId());
		assertEquals(reference, returnedReference);
	}
	

}
