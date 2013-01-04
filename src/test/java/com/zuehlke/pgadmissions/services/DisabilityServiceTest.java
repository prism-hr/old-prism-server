package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.DisabilityDAO;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;

public class DisabilityServiceTest {

	@Test
	public void getAllDisabilitiesFromDAO() {
		DisabilityDAO disabilityDAOMock = EasyMock.createMock(DisabilityDAO.class);
		DisabilityService service = new DisabilityService(disabilityDAOMock);
		Disability disability1 = new DisabilityBuilder().id(1).build();
		Disability disability2 = new DisabilityBuilder().id(2).build();
		EasyMock.expect(disabilityDAOMock.getAllDisabilities()).andReturn(Arrays.asList(disability1, disability2));
		EasyMock.replay(disabilityDAOMock);

		List<Disability> disability = service.getAllDisabilities();
		assertEquals(2, disability.size());
		assertEquals(disability1, disability.get(0));
		assertEquals(disability2, disability.get(1));
		EasyMock.verify(disabilityDAOMock);
	}

	@Test
	public void getDisabilityFromDAO() {
		DisabilityDAO disabilityDAOMock = EasyMock.createMock(DisabilityDAO.class);
		DisabilityService service = new DisabilityService(disabilityDAOMock);
		Disability disability = new DisabilityBuilder().id(1).build();
		EasyMock.expect(disabilityDAOMock.getDisabilityById(1)).andReturn(disability);
		EasyMock.replay(disabilityDAOMock);

		Disability fetchedDisability = service.getDisabilityById(1);
		assertEquals(disability, fetchedDisability);
		EasyMock.verify(disabilityDAOMock);
	}
}
