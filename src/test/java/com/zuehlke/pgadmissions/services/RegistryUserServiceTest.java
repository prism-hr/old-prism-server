package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.RegistryUserDAO;
import com.zuehlke.pgadmissions.domain.RegistryUser;


public class RegistryUserServiceTest {

	@Test
	public void voidShouldGetRegistryUserWithIdFromDAO() {
		RegistryUserDAO registryuserDAOMock = EasyMock.createMock(RegistryUserDAO.class);
		RegistryUserService service = new RegistryUserService(registryuserDAOMock);
		RegistryUser registryUser = new RegistryUser();
		registryUser.setId(2);
		EasyMock.expect(registryuserDAOMock.getRegistryUserWithId(2)).andReturn(registryUser);
		EasyMock.replay(registryuserDAOMock);
		
		Assert.assertEquals(registryUser, service.getRegistryUserWithId(2));
	}
}
