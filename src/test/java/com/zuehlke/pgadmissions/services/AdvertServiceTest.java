package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.dto.AdvertDTO;

public class AdvertServiceTest {

	private AdvertDAO dao;
	private AdvertService advertService;
	
	@Before
	public void setUp(){
		dao = createMock(AdvertDAO.class);
		advertService = new AdvertService();
	}
	@Test
	public void shouldGetAdvertsFromDAO(){
		List<AdvertDTO> result = Collections.<AdvertDTO>emptyList();
		expect(dao.getAdvertFeed(null, null, null)).andReturn(result);
		replay(dao);
		List<AdvertDTO> activeAdverts = advertService.getAdvertFeed(null, null, null);
		assertThat(activeAdverts, Matchers.is(result));
		
	}
	
	@Test
	public void shouldGetRecommendedAdverts() {
        List<AdvertDTO> result = Collections.<AdvertDTO>emptyList();
        expect(dao.getAdvertFeed(null, null, null)).andReturn(result);
        replay(dao);
        List<AdvertDTO> activeAdverts = advertService.getAdvertFeed(null, null, null);
        assertThat(activeAdverts, Matchers.is(result));
	}
	
}
