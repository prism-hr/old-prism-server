package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;

public class CountriesDAOTest extends AutomaticRollbackTestCase {

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        CountriesDAO countriesDAO = new CountriesDAO();
        Country country = new CountryBuilder().id(1).name("ZZZZZZ").enabled(true).build();
        countriesDAO.getCountryById(country.getId());
    }

    @Test
    public void shouldGetAllCountriesInAlhphabeticalOrder() {
        BigInteger numberOfCoutnries = (BigInteger) sessionFactory.getCurrentSession()
                .createSQLQuery("select count(*) from country").uniqueResult();
        Country country1 = new CountryBuilder().enabled(true).code("ZZ").name("ZZZZZZ").build();
        Country country2 = new CountryBuilder().enabled(true).code("AA").name("AAAAAAAA").build();
        save(country1, country2);
        flushAndClearSession();
        CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
        List<Country> allCountries = countriesDAO.getAllCountries();

        assertEquals(numberOfCoutnries.intValue() + 2, allCountries.size());
        assertEquals("AAAAAAAA", allCountries.get(0).getName());
        assertEquals("ZZZZZZ", allCountries.get(numberOfCoutnries.intValue() + 1).getName());
    }

    @Test
    public void shouldGetCountryById() {
        Country country1 = new CountryBuilder().enabled(true).code("ZZ").name("ZZZZZZ").build();
        Country country2 = new CountryBuilder().enabled(true).code("AA").name("mmmmmm").build();

        save(country1, country2);
        flushAndClearSession();
        Integer id = country1.getId();
        CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
        Country reloadedCountry = countriesDAO.getCountryById(id);
        assertEquals(country1.getCode(), reloadedCountry.getCode());
    }

    @Test
    public void shouldGetCountryByCode() {
        Country country1 = new CountryBuilder().enabled(true).code("ZZ").name("ZZZZZZ").build();
        Country country2 = new CountryBuilder().enabled(true).code("AA").name("mmmmmm").build();
        Country country3 = new CountryBuilder().enabled(false).code("AA").name("MMMMMM").build();

        save(country1, country2, country3);
        flushAndClearSession();
        String code = "AA";
        CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
        Country reloadedCountry = countriesDAO.getEnabledCountryByCode(code);
        assertEquals(country2.getCode(), reloadedCountry.getCode());
    }
    
    @Test
    public void shouldGetAllEnabledCountries() {
        BigInteger numberOfCoutnries = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from country where enabled = true").uniqueResult();
        
        Country country1 = new CountryBuilder().enabled(true).code("ZZ").name("ZZZZZZ").build();
        Country country2 = new CountryBuilder().enabled(true).code("AA").name("mmmmmm").build();
        Country country3 = new CountryBuilder().enabled(false).code("AA").name("MMMMMM").build();

        save(country1, country2, country3);
        flushAndClearSession();
        
        CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
        List<Country> reloadedCountry = countriesDAO.getAllEnabledCountries();
        assertEquals(numberOfCoutnries.intValue() + 2, reloadedCountry.size());
    }
}
