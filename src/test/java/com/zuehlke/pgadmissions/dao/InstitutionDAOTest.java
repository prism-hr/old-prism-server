package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.utils.HibernateUtils;

public class InstitutionDAOTest extends AutomaticRollbackTestCase {
    
    private InstitutionDAO institutionDAO;
    
    @Test
    public void shouldReturnInstitutionForDomicileCode() {
        User user = testObjectProvider.getUser();
        System system = testObjectProvider.getSystem();
        InstitutionDomicile domicile1 = testObjectProvider.getInstitutionDomicile();
        InstitutionDomicile domicile2 = testObjectProvider.getAlternativeInstitutionDomicile(domicile1);
        
        Institution institution1 = TestData.aInstitution(user, system, domicile1).withName("dupa1");
        Institution institution2 = TestData.aInstitution(user, system, domicile2).withName("dupa2");
        Institution institution3 = TestData.aInstitution(user, system, domicile2).withName("dupa3");

        save(institution1, institution2, institution3);

        flushAndClearSession();

        List<Institution> returnList = institutionDAO.getEnabledByDomicile(domicile1);

        assertTrue(HibernateUtils.containsEntity(returnList, institution1));
    }


    @Test
    public void shouldGetInstitutionByDomicileAndName() {
        User user = testObjectProvider.getUser();
        System system = testObjectProvider.getSystem();
        InstitutionDomicile domicile1 = testObjectProvider.getInstitutionDomicile();
        InstitutionDomicile domicile2 = testObjectProvider.getAlternativeInstitutionDomicile(domicile1);
        
        Institution institution1 = TestData.aInstitution(user, system, domicile1).withName("dupa1");
        Institution institution2 = TestData.aInstitution(user, system, domicile2).withName("dupa2");
        Institution institution3 = TestData.aInstitution(user, system, domicile2).withName("dupa3");
        
        save(institution1, institution2, institution3);

        flushAndClearSession();

        Institution returned = institutionDAO.getByDomicileAndName(domicile1, "dupa1");

        assertEquals(institution1.getId(), returned.getId());
    }
    
    public void setup(){
        super.setup();
        institutionDAO = new InstitutionDAO(sessionFactory);
    }

}
