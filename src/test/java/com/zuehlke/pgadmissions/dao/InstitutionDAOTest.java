package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.PrismSystem;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class InstitutionDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldReturnInstitutionForDomicileCode() {
        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of London")
                .withDomicileCode("UK").withCode("ABC");
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Cambridge")
                .withDomicileCode("UK").withCode("ABCD");
        Institution institution3 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zurich")
                .withDomicileCode("CH").withCode("ABCDE");

        Country country1 = new CountryBuilder().enabled(true).name("United Kingdom").code("XK").build();
        Country country2 = new CountryBuilder().enabled(true).name("Switzerland").code("CH").build();

        save(institution1, country1, institution2, country2, institution3);

        flushAndClearSession();

        InstitutionDAO qualificationInstitutionDAO = new InstitutionDAO(sessionFactory);

        List<Institution> returnList = qualificationInstitutionDAO.getByDomicileCode("UK");

        assertNotNull(returnList);
        assertEquals(1, returnList.size());
        assertEquals(institution1.getCode(), returnList.get(0).getCode());
        assertEquals(institution1.getName(), returnList.get(0).getName());
    }

    @Test
    public void shouldGetInstitutionByCode() {
        PrismSystem system = (PrismSystem) sessionFactory.getCurrentSession().createCriteria(PrismSystem.class).uniqueResult();

        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of London")
                .withDomicileCode("UK").withCode("ABC").withSystem(system);
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Cambridge")
                .withDomicileCode("UK").withCode("ABCD").withSystem(system);
        Institution institution3 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zurich")
                .withDomicileCode("CH").withCode("ABCDE").withSystem(system);

        save(institution1, institution2, institution3);

        flushAndClearSession();

        InstitutionDAO qualificationInstitutionDAO = new InstitutionDAO(sessionFactory);

        Institution returned = qualificationInstitutionDAO.getByCode("ABC");

        assertEquals(institution1.getCode(), returned.getCode());
        assertEquals(institution1.getName(), returned.getName());
    }

    @Test
    public void shouldGetLastCustomInstitution() {
        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of London")
                .withDomicileCode("UK").withCode("ABC");
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Cambridge")
                .withDomicileCode("UK").withCode("ABCD");
        Institution institution3 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zurich")
                .withDomicileCode("CH").withCode("ABCDE");

        save(institution1, institution2, institution3);

        flushAndClearSession();

        InstitutionDAO qualificationInstitutionDAO = new InstitutionDAO(sessionFactory);

        Institution returned = qualificationInstitutionDAO.getLastCustomInstitution();

        assertEquals(institution2.getCode(), returned.getCode());
        assertEquals(institution2.getName(), returned.getName());
    }

    @Test
    public void shouldGetInstitutionByDomicileAndName() {
        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of London")
                .withDomicileCode("UK").withCode("ABC");
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Cambridge")
                .withDomicileCode("UK").withCode("ABCD");
        Institution institution3 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zurich")
                .withDomicileCode("CH").withCode("ABCDE");

        save(institution1, institution2, institution3);

        flushAndClearSession();

        InstitutionDAO qualificationInstitutionDAO = new InstitutionDAO(sessionFactory);

        Institution returned = qualificationInstitutionDAO.getByDomicileAndName("PL", "University of London");

        assertEquals(institution2.getId(), returned.getId());
    }

}
