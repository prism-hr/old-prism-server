package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;

public class QualificationInstitutionDAOTest extends AutomaticRollbackTestCase{

        @Test
        public void shouldGetQualificationInstitutionById(){

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
}
