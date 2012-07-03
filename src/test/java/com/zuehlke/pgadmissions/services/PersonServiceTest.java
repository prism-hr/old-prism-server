package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.PersonDAO;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;

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

	@Test
	public void shouldGetAllPersonsWHoAreNotSuggestedSupervisorsASRegistryContacts() {
		PersonDAO registryuserDAOMock = EasyMock.createMock(PersonDAO.class);
		PersonService service = new PersonService(registryuserDAOMock);
		Person registryUser = new Person();
		registryUser.setId(2);
		SuggestedSupervisor suggestedSupervisor = new SuggestedSupervisor();
		suggestedSupervisor.setId(5);
		EasyMock.expect(registryuserDAOMock.getAllPersons()).andReturn(Arrays.asList(registryUser, suggestedSupervisor)).anyTimes();
		EasyMock.replay(registryuserDAOMock);
		assertEquals(1, service.getAllRegistryUsers().size());
		Assert.assertEquals(registryUser, service.getAllRegistryUsers().get(0));
	}
}
