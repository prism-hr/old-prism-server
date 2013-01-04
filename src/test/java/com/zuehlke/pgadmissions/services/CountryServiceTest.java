package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;

public class CountryServiceTest {

	@Test
	public void shouldGetAllCountriesFromDAO(){
		CountriesDAO countriesDAOMock = EasyMock.createMock(CountriesDAO.class);
		CountryService service = new CountryService(countriesDAOMock);
		Country country1 = new CountryBuilder().id(1).build();
		Country country2 = new CountryBuilder().id(2).build();
		EasyMock.expect(countriesDAOMock.getAllCountries()).andReturn(Arrays.asList(country1, country2));
		EasyMock.replay(countriesDAOMock);
		
		List<Country> countries = service.getAllCountries();
		assertEquals(2, countries.size());
		assertEquals(country1, countries.get(0));
		assertEquals(country2, countries.get(1));
		
	}
	
	
	@Test
	public void shouldGetCountryFromDAO(){
		CountriesDAO countriesDAOMock = EasyMock.createMock(CountriesDAO.class);
		CountryService service = new CountryService(countriesDAOMock);
		Country country = new CountryBuilder().id(1).build();
		EasyMock.expect(countriesDAOMock.getCountryById(1)).andReturn(country);
		EasyMock.replay(countriesDAOMock);
		
		Country fetchedCountry = service.getCountryById(1);
		assertEquals(country, fetchedCountry);
	
		
	}
}
