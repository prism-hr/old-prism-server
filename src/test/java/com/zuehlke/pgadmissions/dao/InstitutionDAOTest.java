package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class InstitutionDAOTest extends AutomaticRollbackTestCase {
    
    private InstitutionDAO institutionDAO;
    
    @Test
    public void shouldReturnInstitutionForDomicileCode() {
        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of London")
                .withDomicileCode("UK").withCode("ABC").withSystem(testObjectProvider.getPrismSystem());
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Cambridge")
                .withDomicileCode("PL").withCode("ABCD").withSystem(testObjectProvider.getPrismSystem());
        Institution institution3 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zurich")
                .withDomicileCode("PL").withCode("ABCDE").withSystem(testObjectProvider.getPrismSystem());

        Country country1 = new CountryBuilder().enabled(true).name("United Kingdom").code("XK").build();
        Country country2 = new CountryBuilder().enabled(true).name("Switzerland").code("UK").build();

        save(institution1, country1, institution2, country2, institution3);

        flushAndClearSession();

        List<Institution> returnList = institutionDAO.getByDomicileCode("UK");

        assertNotNull(returnList);
        assertEquals(1, returnList.size());
        assertEquals(institution1.getCode(), returnList.get(0).getCode());
        assertEquals(institution1.getName(), returnList.get(0).getName());
    }

    @Test
    public void shouldGetInstitutionByCode() {

        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of London")
                .withDomicileCode("UK").withCode("ABC").withSystem(testObjectProvider.getPrismSystem());
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Cambridge")
                .withDomicileCode("UK").withCode("ABCD").withSystem(testObjectProvider.getPrismSystem());
        Institution institution3 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zurich")
                .withDomicileCode("CH").withCode("ABCDE").withSystem(testObjectProvider.getPrismSystem());

        save(institution1, institution2, institution3);

        flushAndClearSession();

        Institution returned = institutionDAO.getByCode("ABC");

        assertEquals(institution1.getCode(), returned.getCode());
        assertEquals(institution1.getName(), returned.getName());
    }

    @Test
    public void shouldGetLastCustomInstitution() {
        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of London")
                .withDomicileCode("UK").withCode("CUST99995").withSystem(testObjectProvider.getPrismSystem());
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Cambridge")
                .withDomicileCode("UK").withCode("CUST99996").withSystem(testObjectProvider.getPrismSystem());
        Institution institution3 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zurich")
                .withDomicileCode("CH").withCode("CUST99994").withSystem(testObjectProvider.getPrismSystem());

        save(institution1, institution2, institution3);

        flushAndClearSession();

        Institution returned = institutionDAO.getLastCustomInstitution();

        assertEquals(institution2.getCode(), returned.getCode());
        assertEquals(institution2.getName(), returned.getName());
    }

    @Test
    public void shouldGetInstitutionByDomicileAndName() {
        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of London")
                .withDomicileCode("PL").withCode("ABC").withSystem(testObjectProvider.getPrismSystem());
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Cambridge")
                .withDomicileCode("UK").withCode("ABCD").withSystem(testObjectProvider.getPrismSystem());

        save(institution1, institution2);

        flushAndClearSession();

        Institution returned = institutionDAO.getByDomicileAndName("PL", "University of London");

        assertEquals(institution1.getId(), returned.getId());
    }
    
    public void setup(){
        super.setup();
        institutionDAO = new InstitutionDAO(sessionFactory);
    }

}
