package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.PersonDAO;
import com.zuehlke.pgadmissions.domain.Person;


public class PersonServiceTest {

	@Test
	public void voidShouldGetRegistryUserWithIdFromDAO() {
		PersonDAO registryuserDAOMock = EasyMock.createMock(PersonDAO.class);
		PersonService service = new PersonService(registryuserDAOMock);
		Person registryUser = new Person();
		registryUser.setId(2);
		EasyMock.expect(registryuserDAOMock.getPersonWithId(2)).andReturn(registryUser);
		EasyMock.replay(registryuserDAOMock);
		
		Assert.assertEquals(registryUser, service.getRegistryUserWithId(2));
	}
}
