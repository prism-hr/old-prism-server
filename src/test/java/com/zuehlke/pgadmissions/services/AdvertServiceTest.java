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
		List<Advert> result = Collections.<Advert>emptyList();
		expect(dao.getActiveAdverts()).andReturn(result);
		replay(dao);
		List<Advert> activeAdverts = advertService.getActiveAdverts();
		assertThat(activeAdverts, Matchers.is(result));
		
	}
	
}
