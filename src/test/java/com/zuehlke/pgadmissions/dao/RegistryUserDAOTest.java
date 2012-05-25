package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.RegistryUser;
import com.zuehlke.pgadmissions.domain.builders.RegistryUserBuilder;


public class RegistryUserDAOTest extends AutomaticRollbackTestCase {

	
	@Test
	public void shouldSaveAndGetRegistryUserWithId(){
		RegistryUser registryUser = new RegistryUserBuilder().id(1).firstname("john").lastname("mark").email("email").toRegistryUser();
		sessionFactory.getCurrentSession().save(registryUser);
		flushAndClearSession();
		
		RegistryUserDAO registryUserDAO = new RegistryUserDAO(sessionFactory);
		RegistryUser returnedRegistryUser = registryUserDAO.getRegistryUserWithId(registryUser.getId());
		assertEquals(registryUser, returnedRegistryUser);
	}
}
