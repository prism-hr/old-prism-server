package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;

public class CountryMappingTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldSaveAndLoadCountry(){
		Country country = new CountryBuilder().code("zz").name("ZZZZZZ").toCountry();
		sessionFactory.getCurrentSession().save(country);
		assertNotNull(country.getId());
		Integer id = country.getId();
		Country	 reloadedCountry = (Country) sessionFactory.getCurrentSession().get(Country.class, id);
		assertSame(country, reloadedCountry);
		
		flushAndClearSession();
		
		reloadedCountry = (Country) sessionFactory.getCurrentSession().get(Country.class, id);
		assertNotSame(country, reloadedCountry);
		assertEquals(country, reloadedCountry);
		
		
		assertEquals("zz", reloadedCountry.getCode());
		assertEquals("ZZZZZZ", reloadedCountry.getName());
		
	}
}
