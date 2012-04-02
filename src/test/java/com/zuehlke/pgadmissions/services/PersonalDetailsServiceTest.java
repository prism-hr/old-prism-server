package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.PersonalDetailDAO;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;

public class PersonalDetailsServiceTest {

	@Test
	public void shouldGetPersonalDetailsFromDAO(){
		PersonalDetailDAO personalDetailDAOMock = EasyMock.createMock(PersonalDetailDAO.class);
		PersonalDetailsService detailsService = new PersonalDetailsService(personalDetailDAOMock);
		
		PersonalDetail personalDetails = new PersonalDetailsBuilder().id(1).toPersonalDetails();
		EasyMock.expect(personalDetailDAOMock.getPersonalDetailsById(1)).andReturn(personalDetails);
		EasyMock.replay(personalDetailDAOMock);
		
		PersonalDetail details = detailsService.getPersonalDetailsById(1);
		assertEquals(personalDetails, details);
		
	}
	
	@Test
	public void shouldUserDAOToSavePersonalDetails(){
		PersonalDetailDAO personalDetailDAOMock = EasyMock.createMock(PersonalDetailDAO.class);
		PersonalDetailsService detailsService = new PersonalDetailsService(personalDetailDAOMock);
		
		PersonalDetail personalDetails = new PersonalDetailsBuilder().id(1).toPersonalDetails();
		personalDetailDAOMock.save(personalDetails);
		EasyMock.replay(personalDetailDAOMock);
		
		detailsService.save(personalDetails);
		EasyMock.verify(personalDetailDAOMock);
		
	}
}
