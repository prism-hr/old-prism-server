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
		Country country = new CountryBuilder().name("ZZZZZZ").code("ZZ").enabled(true).build();
		sessionFactory.getCurrentSession().save(country);
		assertNotNull(country.getId());
		Integer id = country.getId();
		Country	 reloadedCountry = (Country) sessionFactory.getCurrentSession().get(Country.class, id);
		assertSame(country, reloadedCountry);
		
		flushAndClearSession();
		
		reloadedCountry = (Country) sessionFactory.getCurrentSession().get(Country.class, id);
		assertNotSame(country, reloadedCountry);
		assertEquals(country.getId(), reloadedCountry.getId());
		assertEquals("ZZZZZZ", reloadedCountry.getName());
	}
}
