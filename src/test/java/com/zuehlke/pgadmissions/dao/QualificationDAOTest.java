package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.builders.TestData;

public class QualificationDAOTest extends AutomaticRollbackTestCase {

    private QualificationDAO qualificationDAO;

    @Test
    public void shouldGetQualificationById() throws ParseException {
        Application application = testObjectProvider.getApplication();
        Qualification qualification = TestData.aQualification(application, testObjectProvider.getQualificationType(), TestData.aDocument(), testObjectProvider.getImportedInstitution());
        save(application, qualification);
        flushAndClearSession();
        
        Integer id = qualification.getId();
        flushAndClearSession();

        assertEquals(qualification.getId(), qualificationDAO.getById(id).getId());
    }

    @Test
    public void shouldDeleteQualification() throws ParseException {
        Application application = testObjectProvider.getApplication();
        Qualification qualification = TestData.aQualification(application, testObjectProvider.getQualificationType(), TestData.aDocument(), testObjectProvider.getImportedInstitution()); 
        save(qualification);
        flushAndClearSession();

        Integer id = qualification.getId();
        qualificationDAO.delete(qualification);
        flushAndClearSession();
        assertNull(sessionFactory.getCurrentSession().get(Qualification.class, id));
    }

    @Before
    public void prepare() {
        qualificationDAO = new QualificationDAO(sessionFactory);
    }
}
