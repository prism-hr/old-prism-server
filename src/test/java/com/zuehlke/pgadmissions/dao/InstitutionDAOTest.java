package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

public class InstitutionDAOTest extends AutomaticRollbackTestCase {
    
    private InstitutionDAO institutionDAO;
    
    @Test
    public void shouldReturnInstitutionForDomicileCode() {
        InstitutionDomicile domicile1 = testObjectProvider.getInstitutionDomicile();
        InstitutionDomicile domicile2 = testObjectProvider.getAlternativeInstitutionDomicile(domicile1);
        
        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Bielsko-Biala")
                .withDomicile(domicile1).withCode("ABC").withSystem(testObjectProvider.getPrismSystem());
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zywiec")
                .withDomicile(domicile2).withCode("ABCD").withSystem(testObjectProvider.getPrismSystem());
        Institution institution3 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zurich")
                .withDomicile(domicile2).withCode("ABCDE").withSystem(testObjectProvider.getPrismSystem());

        save(institution1, institution2, institution3);

        flushAndClearSession();

        List<Institution> returnList = institutionDAO.getEnabledByDomicile(domicile1);

        assertTrue(HibernateUtils.containsEntity(returnList, institution1));
    }

    @Test
    public void shouldGetInstitutionByCode() {
        InstitutionDomicile domicile1 = testObjectProvider.getInstitutionDomicile();
        
        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Bielsko-Biala")
                .withDomicile(domicile1).withCode("ABC").withSystem(testObjectProvider.getPrismSystem());
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zywiec")
                .withDomicile(domicile1).withCode("ABCD").withSystem(testObjectProvider.getPrismSystem());
        Institution institution3 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zurich")
                .withDomicile(domicile1).withCode("ABCDE").withSystem(testObjectProvider.getPrismSystem());

        save(institution1, institution2, institution3);

        flushAndClearSession();

        Institution returned = institutionDAO.getByCode("ABC");

        assertEquals(institution1.getCode(), returned.getCode());
        assertEquals(institution1.getName(), returned.getName());
    }

    @Test
    public void shouldGetLastCustomInstitution() {
        InstitutionDomicile domicile1 = testObjectProvider.getInstitutionDomicile();
        
        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Bielsko-Biala")
                .withDomicile(domicile1).withCode("CUST99995").withSystem(testObjectProvider.getPrismSystem());
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zywiec")
                .withDomicile(domicile1).withCode("CUST99996").withSystem(testObjectProvider.getPrismSystem());
        Institution institution3 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zurich")
                .withDomicile(domicile1).withCode("CUST99994").withSystem(testObjectProvider.getPrismSystem());

        save(institution1, institution2, institution3);

        flushAndClearSession();

        Institution returned = institutionDAO.getLastCustomInstitution();

        assertEquals(institution2.getCode(), returned.getCode());
        assertEquals(institution2.getName(), returned.getName());
    }

    @Test
    public void shouldGetInstitutionByDomicileAndName() {
        InstitutionDomicile domicile1 = testObjectProvider.getInstitutionDomicile();
        InstitutionDomicile domicile2 = testObjectProvider.getAlternativeInstitutionDomicile(domicile1);
        
        Institution institution1 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Bielsko-Biala")
                .withDomicile(domicile1).withCode("ABC").withSystem(testObjectProvider.getPrismSystem());
        Institution institution2 = new Institution().withState(new State().withId(PrismState.INSTITUTION_APPROVED)).withName("University of Zywiec")
                .withDomicile(domicile2).withCode("ABCD").withSystem(testObjectProvider.getPrismSystem());

        save(institution1, institution2);

        flushAndClearSession();

        Institution returned = institutionDAO.getByDomicileAndName(domicile1, "University of Bielsko-Biala");

        assertEquals(institution1.getId(), returned.getId());
    }
    
    public void setup(){
        super.setup();
        institutionDAO = new InstitutionDAO(sessionFactory);
    }

}
