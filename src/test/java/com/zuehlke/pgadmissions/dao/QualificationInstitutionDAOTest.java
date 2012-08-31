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
        QualificationInstitution qualificationInstitution1 = new QualificationInstitutionBuilder().code("zz").enabled(true).name("ZZZZZZ").toQualificationInstitution();
        QualificationInstitution qualificationInstitution2 = new QualificationInstitutionBuilder().code("MM").enabled(true).name("mmmmmm").toQualificationInstitution();

        save(qualificationInstitution1, qualificationInstitution2);
        flushAndClearSession();
        Integer id = qualificationInstitution1.getId();
        QualificationInstitutionDAO qualificationInstitutionsDAO = new QualificationInstitutionDAO(sessionFactory);
        QualificationInstitution reloadedInstitution = qualificationInstitutionsDAO.getInstitutionById(id);
        assertEquals(qualificationInstitution1, reloadedInstitution);
    }

    @Test
    public void shouldReturnInstitutionByCountryCode() {
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("zz").enabled(false).name("dd").toQualificationInstitution();
        Country country = new CountryBuilder().code("zz").enabled(true).name("name").toCountry();
        save(institution, country);
        flushAndClearSession();
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        assertEquals(institution, qualificationInstitutionDAO.getInstitutionsByCountryCode(country.getCode()).get(0));
    }

    @Test
    public void shouldReturnInstitutionByCountryCodeAndNameStartMatching() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().code("Z1").enabled(false).name("University of London").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().code("Z1").enabled(false).name("University of Cambridge").toQualificationInstitution();
        QualificationInstitution institution3 = new QualificationInstitutionBuilder().code("Z2").enabled(false).name("University of Zurich").toQualificationInstitution();
        Country country1 = new CountryBuilder().code("Z1").enabled(true).name("United Kingdom").toCountry();
        Country country2 = new CountryBuilder().code("Z2").enabled(true).name("Switzerland").toCountry();
        save(institution1, country1, institution2, country2, institution3);
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> resultList = qualificationInstitutionDAO.getInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive("Z1", "Univers");
        assertEquals(2, resultList.size());
        assertEquals(institution2, resultList.get(0));
        assertEquals(institution1, resultList.get(1));
    }
    
    @Test
    public void shouldReturnInstitutionByCountryCodeAndNameMiddleMatching() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().code("Z1").enabled(false).name("University of London").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().code("Z2").enabled(false).name("University of Zurich").toQualificationInstitution();
        Country country1 = new CountryBuilder().code("Z1").enabled(true).name("United Kingdom").toCountry();
        Country country2 = new CountryBuilder().code("Z2").enabled(true).name("Switzerland").toCountry();
        save(institution1, country1, institution2, country2);
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> resultList = qualificationInstitutionDAO.getInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive("Z2", "urich");
        assertEquals(1, resultList.size());
        assertEquals(institution2, resultList.get(0));
    }    
    
    @Test
    public void shouldReturnInstitutionByCountryCodeAndNameEndtMatching() {
        QualificationInstitution institution1 = new QualificationInstitutionBuilder().code("Z1").enabled(false).name("University of London").toQualificationInstitution();
        QualificationInstitution institution2 = new QualificationInstitutionBuilder().code("Z2").enabled(false).name("University of Zurich").toQualificationInstitution();
        Country country1 = new CountryBuilder().code("Z1").enabled(true).name("United Kingdom").toCountry();
        Country country2 = new CountryBuilder().code("Z2").enabled(true).name("Switzerland").toCountry();
        save(institution1, country1, institution2, country2);
        flushAndClearSession();
        
        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        List<QualificationInstitution> resultList = qualificationInstitutionDAO.getInstitutionsByCountryCodeFilteredByNameLikeCaseInsensitive("Z1", "London");
        assertEquals(1, resultList.size());
        assertEquals(institution1, resultList.get(0));
    }    
}
