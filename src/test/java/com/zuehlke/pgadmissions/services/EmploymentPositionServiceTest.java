package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.EmploymentPositionDAO;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;

public class EmploymentPositionServiceTest {

	
	private EmploymentPositionDAO employmentPositionDAOMock;
	private EmploymentPositionService employmentPositionService;

	@Test
	public void shouldGetEmploymentFromDAO(){
		EmploymentPosition employmentPosition = new EmploymentPositionBuilder().id(1).toEmploymentPosition();
		EasyMock.expect(employmentPositionDAOMock.getEmploymentPositionById(1)).andReturn(employmentPosition);
		EasyMock.replay(employmentPositionDAOMock);
		EmploymentPosition returnedEmployment = employmentPositionService.getEmploymentPositionById(1);
		assertEquals(employmentPosition, returnedEmployment);
	}
	
	@Test
	public void shouldDelegateSaveToDAO(){
		EmploymentPosition employmentPosition = new EmploymentPositionBuilder().id(1).toEmploymentPosition();
		employmentPositionDAOMock.save(employmentPosition);
		EasyMock.replay(employmentPositionDAOMock);
		employmentPositionService.save(employmentPosition);
		EasyMock.verify(employmentPositionDAOMock);
	}
	
	
	@Test
	public void shouldDelegateDeleteToDAO(){
		EmploymentPosition employmentPosition = new EmploymentPositionBuilder().id(1).toEmploymentPosition();
		employmentPositionDAOMock.delete(employmentPosition);
		EasyMock.replay(employmentPositionDAOMock);
		employmentPositionService.delete(employmentPosition);
		EasyMock.verify(employmentPositionDAOMock);
	}
	
	
	
	@Before
	public void setup(){
		employmentPositionDAOMock = EasyMock.createMock(EmploymentPositionDAO.class);
		employmentPositionService = new EmploymentPositionService(employmentPositionDAOMock);
	}
}
