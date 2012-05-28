package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.domain.Supervisor;


public class SupervisorServiceTest {

	@Test
	public void voidShouldGetSupervisorWithIdFromDAO() {
		SupervisorDAO supervisorDAOMock = EasyMock.createMock(SupervisorDAO.class);
		SupervisorService service = new SupervisorService(supervisorDAOMock);
		Supervisor supervisorOne = new Supervisor();
		supervisorOne.setId(2);
		EasyMock.expect(supervisorDAOMock.getSupervisorWithId(2)).andReturn(supervisorOne);
		EasyMock.replay(supervisorDAOMock);
		
		Assert.assertEquals(supervisorOne, service.getSupervisorWithId(2));
	}
}
