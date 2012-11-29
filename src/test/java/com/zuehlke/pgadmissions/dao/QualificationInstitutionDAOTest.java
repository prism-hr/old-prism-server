package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.QualificationInstitutionReference;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;

public class QualificationInstitutionDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldGetQualificationInstitutionById() {
        QualificationInstitution qualificationInstitution1 = new QualificationInstitutionBuilder().enabled(true).name("ZZZZZZ").countryCode("XK").code("ZZ").toQualificationInstitution();
        QualificationInstitution qualificationInstitution2 = new QualificationInstitutionBuilder().enabled(true).name("mmmmmm").countryCode("XK").code("mm").toQualificationInstitution();

        save(qualificationInstitution1, qualificationInstitution2);
        flushAndClearSession();
        Integer id = qualificationInstitution1.getId();
        QualificationInstitutionDAO qualificationInstitutionsDAO = new QualificationInstitutionDAO(sessionFactory);
        QualificationInstitution reloadedInstitution = qualificationInstitutionsDAO.getInstitutionById(id);
        assertEquals(qualificationInstitution1, reloadedInstitution);
    }

    @Test
    public void shouldReturnInstitutionByCountryCodeAndNameStartMatching() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().enabled(true).name("University of London").countryCode("UK").code("ABC").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().enabled(true).name("University of Cambridge").countryCode("UK").code("ABCD").toQualificationInstitution();
        QualificationInstitution institution3 = new QualificationInstitutionBuilder().enabled(true).name("University of Zurich").countryCode("CH").code("ABCDE").toQualificationInstitution();
        Country country1 = new CountryBuilder().enabled(true).name("United Kingdom").code("XK").toCountry();
        Country country2 = new CountryBuilder().enabled(true).name("Switzerland").code("CH").toCountry();
        save(institution1, country1, institution2, country2, institution3);
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> resultList = qualificationInstitutionDAO.getEnabledInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive("UK", "Univers");
        assertEquals(2, resultList.size());
        assertEquals(institution2, resultList.get(0));
        assertEquals(institution1, resultList.get(1));
    }
    
    @Test
    public void shouldReturnInstitutionByCountryCodeAndNameMiddleMatching() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().enabled(true).name("University of London").countryCode("UK").code("AB").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().enabled(true).name("University of Zurich").countryCode("UK").code("BC").toQualificationInstitution();
        Country country1 = new CountryBuilder().enabled(true).code("XK").name("United Kingdom").toCountry();
        Country country2 = new CountryBuilder().enabled(true).code("CH").name("Switzerland").toCountry();
        save(institution1, country1, institution2, country2);
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> resultList = qualificationInstitutionDAO.getEnabledInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive("UK", "urich");
        assertEquals(1, resultList.size());
        assertEquals(institution2, resultList.get(0));
    }    
    
    @Test
    public void shouldReturnInstitutionByCountryCodeAndNameEndtMatching() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().enabled(true).name("University of London").countryCode("UK").code("AC").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().enabled(true).name("University of Zurich").countryCode("UK").code("DC").toQualificationInstitution();
        Country country1 = new CountryBuilder().enabled(true).name("United Kingdom").code("XK").toCountry();
        Country country2 = new CountryBuilder().enabled(true).name("Switzerland").code("CH").toCountry();
        save(institution1, country1, institution2, country2);
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> resultList = qualificationInstitutionDAO.getEnabledInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive("UK", "London");
        assertEquals(1, resultList.size());
        assertEquals(institution1, resultList.get(0));
    }
    
    @Test
    public void shouldReturnInstitutionByCountryCodeAndNameStartMatchingButOnlyEnabledOnes() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().enabled(true).name("University of London").countryCode("UK").code("ABC").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().enabled(false).name("University of Cambridge").countryCode("UK").code("ABCD").toQualificationInstitution();
        QualificationInstitution institution3 = new QualificationInstitutionBuilder().enabled(true).name("University of Zurich").countryCode("CH").code("ABCDE").toQualificationInstitution();
        Country country1 = new CountryBuilder().enabled(true).name("United Kingdom").code("XK").toCountry();
        Country country2 = new CountryBuilder().enabled(true).name("Switzerland").code("CH").toCountry();
        save(institution1, country1, institution2, country2, institution3);
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> resultList = qualificationInstitutionDAO.getEnabledInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive("UK", "Univers");
        assertEquals(1, resultList.size());
        assertEquals(institution1, resultList.get(0));
    }
    
    @Test
    public void shouldReturnInstitutionByName() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().enabled(true).name("University of London").countryCode("UK").code("ABC").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().enabled(false).name("University of Cambridge").countryCode("UK").code("ABCD").toQualificationInstitution();
        QualificationInstitution institution3 = new QualificationInstitutionBuilder().enabled(true).name("University of Zurich").countryCode("CH").code("ABCDE").toQualificationInstitution();
        Country country1 = new CountryBuilder().enabled(true).name("United Kingdom").code("XK").toCountry();
        Country country2 = new CountryBuilder().enabled(true).name("Switzerland").code("CH").toCountry();
        save(institution1, country1, institution2, country2, institution3);
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> institutionByName = qualificationInstitutionDAO.getAllInstitutionByName("University of Cambridge");
        assertNotNull(institutionByName);
        assertEquals(2, institutionByName.size());
        assertEquals("University of Cambridge", institutionByName.get(0).getName());
    }
    
    @Test
    public void shouldReturnInstitutionsWhichMatchCodeAndTermAndAreInTheReferenceData() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().enabled(true).name("University of London").countryCode("UK").code("ABC").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().enabled(true).name("University of Cambridge").countryCode("UK").code("ABCD").toQualificationInstitution();
        QualificationInstitution institution3 = new QualificationInstitutionBuilder().enabled(true).name("University of Zurich").countryCode("CH").code("ABCDE").toQualificationInstitution();
        
        QualificationInstitutionReference institution4 = new QualificationInstitutionBuilder().enabled(true).name("University of   Cambridge .").countryCode("UK").code("ABCD").toQualificationInstitutionReference();
        QualificationInstitutionReference institution5 = new QualificationInstitutionBuilder().enabled(true).name("University of Zurich").countryCode("CH").code("ABCDE").toQualificationInstitutionReference();
        
        Country country1 = new CountryBuilder().enabled(true).name("United Kingdom").code("XK").toCountry();
        Country country2 = new CountryBuilder().enabled(true).name("Switzerland").code("CH").toCountry();
        
        save(institution1, country1, institution2, country2, institution3, institution4, institution5);
        
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> returnList = qualificationInstitutionDAO.getEnableAndValiddInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive("UK", "Univers");

        assertNotNull(returnList);
        assertEquals(1, returnList.size());
        assertEquals(institution2.getCode(), returnList.get(0).getCode());
        assertEquals(institution2.getName(), returnList.get(0).getName());
    }
}
