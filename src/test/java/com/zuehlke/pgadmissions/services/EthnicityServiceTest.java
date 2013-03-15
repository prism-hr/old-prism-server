package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.EthnicityDAO;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;

public class EthnicityServiceTest {

	@Test
	public void shouldGetAllEthnicityFromDAO(){
		EthnicityDAO ethnicityDAOMock = EasyMock.createMock(EthnicityDAO.class);
		EthnicityService service = new EthnicityService(ethnicityDAOMock);
		Ethnicity country1 = new EthnicityBuilder().id(1).build();
		Ethnicity country2 = new EthnicityBuilder().id(2).build();
		EasyMock.expect(ethnicityDAOMock.getAllEthnicities()).andReturn(Arrays.asList(country1, country2));
		EasyMock.replay(ethnicityDAOMock);
		
		List<Ethnicity> ethnicity = service.getAllEthnicities();
		assertEquals(2, ethnicity.size());
		assertEquals(country1, ethnicity.get(0));
		assertEquals(country2, ethnicity.get(1));
		EasyMock.verify(ethnicityDAOMock);
	}
	
	@Test
	public void shouldGetEthnicityFromDAO(){
		EthnicityDAO ethnicityDAOMock = EasyMock.createMock(EthnicityDAO.class);
		EthnicityService service = new EthnicityService(ethnicityDAOMock);
		Ethnicity country = new EthnicityBuilder().id(1).build();
		EasyMock.expect(ethnicityDAOMock.getEthnicityById(1)).andReturn(country);
		EasyMock.replay(ethnicityDAOMock);
		
		Ethnicity fetchedEthnicity = service.getEthnicityById(1);
		assertEquals(country, fetchedEthnicity);
		EasyMock.verify(ethnicityDAOMock);
	}
}