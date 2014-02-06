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
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.dto.AdvertDTO;

public class AdvertServiceTest {

	private AdvertDAO dao;
	private AdvertService advertService;
	
	@Before
	public void setUp(){
		dao = createMock(AdvertDAO.class);
		advertService = new AdvertService(dao);
	}
	@Test
	public void shouldGetAdvertsFromDAO(){
		List<AdvertDTO> result = Collections.<AdvertDTO>emptyList();
		expect(dao.getActiveAdverts(0)).andReturn(result);
		replay(dao);
		List<AdvertDTO> activeAdverts = advertService.getActiveAdverts(0);
		assertThat(activeAdverts, Matchers.is(result));
		
	}
	
	@Test
	public void shouldGetRecommendedAdverts() {
        List<Advert> result = Collections.<Advert>emptyList();
        expect(dao.getRecommendedAdverts(null)).andReturn(result);
        replay(dao);
        List<Advert> activeAdverts = advertService.getRecommendedAdverts(null);
        assertThat(activeAdverts, Matchers.is(result));
	}
	
}
