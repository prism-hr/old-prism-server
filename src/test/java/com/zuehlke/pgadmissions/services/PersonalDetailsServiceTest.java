package com.zuehlke.pgadmissions.services;

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
		
		PersonalDetails personalDetails = new PersonalDetailsBuilder().id(1).build();
		personalDetailDAOMock.save(personalDetails);
		EasyMock.replay(personalDetailDAOMock);
		
		detailsService.save(personalDetails);
		EasyMock.verify(personalDetailDAOMock);
	}
	
	@Test
	public void shouldSetPassportInformationToNullIfNotAvailable() {
	    PersonalDetailDAO personalDetailDAOMock = EasyMock.createMock(PersonalDetailDAO.class);
	    PersonalDetails personalDetailsMock = EasyMock.createMock(PersonalDetails.class);
	    
        PersonalDetailsService detailsService = new PersonalDetailsService(personalDetailDAOMock);
        
        EasyMock.expect(personalDetailsMock.getPassportAvailable()).andReturn(false);
        personalDetailsMock.setPassportInformation(null);
        personalDetailsMock.setPassportAvailable(false);
        personalDetailDAOMock.save(personalDetailsMock);

        EasyMock.replay(personalDetailDAOMock, personalDetailsMock);
        
        detailsService.save(personalDetailsMock);
        
        EasyMock.verify(personalDetailDAOMock, personalDetailsMock);
	}
}
