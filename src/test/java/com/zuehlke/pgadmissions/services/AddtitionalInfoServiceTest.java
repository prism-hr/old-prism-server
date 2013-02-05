package com.zuehlke.pgadmissions.services;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.AdditionalInfoDAO;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.builders.AdditionalInformationBuilder;

public class AddtitionalInfoServiceTest {

	@Test
	public void savePersonalDetails(){
		AdditionalInfoDAO daoMock = EasyMock.createMock(AdditionalInfoDAO.class);
		AdditionalInfoService service = new AdditionalInfoService(daoMock);
		
		AdditionalInformation info = new AdditionalInformationBuilder().id(3).build();
		daoMock.save(EasyMock.eq( info));
		EasyMock.replay(daoMock);
		
		service.save(info );
		EasyMock.verify(daoMock);
	}
}
