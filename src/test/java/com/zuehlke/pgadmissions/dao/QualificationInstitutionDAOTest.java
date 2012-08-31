package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;

public class QualificationInstitutionDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldGetQualificationInstitutionById() {
        QualificationInstitution qualificationInstitution1 = new QualificationInstitutionBuilder().enabled(true).name("ZZZZZZ").country_name("United Kingdom").toQualificationInstitution();
        QualificationInstitution qualificationInstitution2 = new QualificationInstitutionBuilder().enabled(true).name("mmmmmm").country_name("United Kingdom").toQualificationInstitution();

        save(qualificationInstitution1, qualificationInstitution2);
        flushAndClearSession();
        Integer id = qualificationInstitution1.getId();
        QualificationInstitutionDAO qualificationInstitutionsDAO = new QualificationInstitutionDAO(sessionFactory);
        QualificationInstitution reloadedInstitution = qualificationInstitutionsDAO.getInstitutionById(id);
        assertEquals(qualificationInstitution1, reloadedInstitution);
    }

    @Test
    public void shouldReturnInstitutionByCountryName() {
        QualificationInstitution institution = new QualificationInstitutionBuilder().enabled(false).name("dd").country_name("name").toQualificationInstitution();
        Country country = new CountryBuilder().enabled(true).name("name").toCountry();
        save(institution, country);
        flushAndClearSession();
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        assertEquals(institution, qualificationInstitutionDAO.getInstitutionsByCountryName(country.getName()).get(0));
    }

    @Test
    public void shouldReturnInstitutionByCountryCodeAndNameStartMatching() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().enabled(false).name("University of London").country_name("UK").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().enabled(false).name("University of Cambridge").country_name("UK").toQualificationInstitution();
        QualificationInstitution institution3 = new QualificationInstitutionBuilder().enabled(false).name("University of Zurich").country_name("CH").toQualificationInstitution();
        Country country1 = new CountryBuilder().enabled(true).name("United Kingdom").toCountry();
        Country country2 = new CountryBuilder().enabled(true).name("Switzerland").toCountry();
        save(institution1, country1, institution2, country2, institution3);
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> resultList = qualificationInstitutionDAO.getInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive("UK", "Univers");
        assertEquals(2, resultList.size());
        assertEquals(institution2, resultList.get(0));
        assertEquals(institution1, resultList.get(1));
    }
    
    @Test
    public void shouldReturnInstitutionByCountryCodeAndNameMiddleMatching() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().enabled(false).name("University of London").country_name("UK").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().enabled(false).name("University of Zurich").country_name("UK").toQualificationInstitution();
        Country country1 = new CountryBuilder().enabled(true).name("United Kingdom").toCountry();
        Country country2 = new CountryBuilder().enabled(true).name("Switzerland").toCountry();
        save(institution1, country1, institution2, country2);
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> resultList = qualificationInstitutionDAO.getInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive("UK", "urich");
        assertEquals(1, resultList.size());
        assertEquals(institution2, resultList.get(0));
    }    
    
    @Test
    public void shouldReturnInstitutionByCountryCodeAndNameEndtMatching() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().enabled(false).name("University of London").country_name("UK").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().enabled(false).name("University of Zurich").country_name("UK").toQualificationInstitution();
        Country country1 = new CountryBuilder().enabled(true).name("United Kingdom").toCountry();
        Country country2 = new CountryBuilder().enabled(true).name("Switzerland").toCountry();
        save(institution1, country1, institution2, country2);
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> resultList = qualificationInstitutionDAO.getInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive("UK", "London");
        assertEquals(1, resultList.size());
        assertEquals(institution1, resultList.get(0));
    }    
}
