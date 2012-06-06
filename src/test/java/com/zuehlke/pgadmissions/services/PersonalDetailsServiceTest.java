package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.PersonalDetailDAO;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;

public class PersonalDetailsServiceTest {


	
	@Test
	public void shouldUserDAOToSavePersonalDetails(){
		PersonalDetailDAO personalDetailDAOMock = EasyMock.createMock(PersonalDetailDAO.class);
		PersonalDetailsService detailsService = new PersonalDetailsService(personalDetailDAOMock);
		
		PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).toPersonalDetails();
		personalDetailDAOMock.save(personalDetails);
		EasyMock.replay(personalDetailDAOMock);
		
		detailsService.save(personalDetails);
		EasyMock.verify(personalDetailDAOMock);
		
	}
}
