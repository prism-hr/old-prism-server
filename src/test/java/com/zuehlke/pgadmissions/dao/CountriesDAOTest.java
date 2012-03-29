package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;


public class CountriesDAOTest extends AutomaticRollbackTestCase{

	@Test(expected=NullPointerException.class)
	public void shouldThrowNullPointerException(){
		CountriesDAO countriesDAO = new CountriesDAO();
		Country country = new CountryBuilder().id(1).code("zz").name("ZZZZZZ").toCountry();
		countriesDAO.getCountryById(country.getId());
	}
	
	@Test
	public void shouldGetAllCountriesInAlhphabeticalOrder() {
		BigInteger numberOfCoutnries = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from COUNTRIES").uniqueResult();
		Country country1 = new CountryBuilder().code("zz").name("ZZZZZZ").toCountry();		
		Country country2 = new CountryBuilder().code("aa").name("AAAAAAAA").toCountry();
		save(country1,  country2);
		flushAndClearSession();
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		List<Country> allCountries = countriesDAO.getAllCountries();
		assertEquals(numberOfCoutnries.intValue() +2, allCountries.size());
		
		assertEquals("AAAAAAAA",allCountries.get(0).getName());
		
		assertEquals("ZZZZZZ",allCountries.get(numberOfCoutnries.intValue() +1).getName());
		
	}
	
	
	@Test
	public void shouldGetCountryById(){

		Country country1 = new CountryBuilder().code("zz").name("ZZZZZZ").toCountry();
		Country country2 = new CountryBuilder().code("MM").name("mmmmmm").toCountry();
		
		save(country1, country2);
		flushAndClearSession();
		Integer id = country1.getId();
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Country reloadedCountry = countriesDAO.getCountryById(id);
		assertEquals(country1, reloadedCountry);
		
	
	}
	
}
