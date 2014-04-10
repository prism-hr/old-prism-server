package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.QualificationInstitutionReference;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.enums.InstitutionState;

public class QualificationInstitutionDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldReturnInstitutionForDomicileCode() {
        Institution institution1 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_APPROVED).name("University of London").domicileCode("UK").code("ABC")
                .build();
        Institution institution2 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_DISABLED).name("University of Cambridge").domicileCode("UK")
                .code("ABCD").build();
        Institution institution3 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_APPROVED).name("University of Zurich").domicileCode("CH")
                .code("ABCDE").build();

        QualificationInstitutionReference institution4 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_DISABLED).name("University of   Cambridge .")
                .domicileCode("UK").code("ABCD").buildAsReference();
        QualificationInstitutionReference institution5 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_APPROVED).name("University of Zurich").domicileCode("CH")
                .code("ABCDE").buildAsReference();

        Country country1 = new CountryBuilder().enabled(true).name("United Kingdom").code("XK").build();
        Country country2 = new CountryBuilder().enabled(true).name("Switzerland").code("CH").build();

        save(institution1, country1, institution2, country2, institution3, institution4, institution5);

        flushAndClearSession();

        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);

        List<Institution> returnList = qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode("UK");

        assertNotNull(returnList);
        assertEquals(1, returnList.size());
        assertEquals(institution1.getCode(), returnList.get(0).getCode());
        assertEquals(institution1.getName(), returnList.get(0).getName());
    }

    @Test
    public void shouldGetInstitutionByCode() {
        Institution institution1 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_APPROVED).name("University of London").domicileCode("UK").code("ABC")
                .build();
        Institution institution2 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_DISABLED).name("University of Cambridge").domicileCode("UK")
                .code("ABCD").build();
        Institution institution3 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_APPROVED).name("University of Zurich").domicileCode("CH")
                .code("ABCDE").build();

        save(institution1, institution2, institution3);

        flushAndClearSession();

        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);

        Institution returned = qualificationInstitutionDAO.getByCode("ABC");

        assertEquals(institution1.getCode(), returned.getCode());
        assertEquals(institution1.getName(), returned.getName());
    }

    @Test
    public void shouldGetLastCustomInstitution() {
        Institution institution1 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_APPROVED).name("University of London").domicileCode("UK")
                .code("CUST00005").build();
        Institution institution2 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_DISABLED).name("University of Cambridge").domicileCode("UK")
                .code("CUST00006").build();
        Institution institution3 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_APPROVED).name("University of Zurich").domicileCode("CH")
                .code("CUST00004").build();

        save(institution1, institution2, institution3);

        flushAndClearSession();

        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);

        Institution returned = qualificationInstitutionDAO.getLastCustomInstitution();

        assertEquals(institution2.getCode(), returned.getCode());
        assertEquals(institution2.getName(), returned.getName());
    }
    
    @Test
    public void shouldGetInstitutionByDomicileAndName() {
        Institution institution1 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_APPROVED).name("University of London").domicileCode("UK").code("ABC")
                .build();
        Institution institution2 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_APPROVED).name("University of London").domicileCode("PL")
                .code("ABCD").build();
        Institution institution3 = new QualificationInstitutionBuilder().state(InstitutionState.INSTITUTION_APPROVED).name("Akademia Gorniczo-Hutnicza").domicileCode("PL")
                .code("ABCDE").build();

        save(institution1, institution2, institution3);

        flushAndClearSession();

        QualificationInstitutionDAO qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);

        Institution returned = qualificationInstitutionDAO.getByDomicileAndName("PL", "University of London");

        assertEquals(institution2.getId(), returned.getId());
    }

}
