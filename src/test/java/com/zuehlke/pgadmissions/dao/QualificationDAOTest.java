package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class QualificationDAOTest extends AutomaticRollbackTestCase {

    private User user;
    private Program program;
    private QualificationDAO qualificationDAO;

    @Test
    public void shouldGetQualificationById() throws ParseException {
        ApplicationForm application = TestData.anApplicationForm(user, program, testObjectProvider.getState(PrismState.APPLICATION_UNSUBMITTED));
        Qualification qualification = TestData.aQualification(application, testObjectProvider.getQualificationType(), TestData.aDocument(), testObjectProvider.getInstitution());
        save(application, qualification);
        flushAndClearSession();
        
        Integer id = qualification.getId();
        flushAndClearSession();

        assertEquals(qualification.getId(), qualificationDAO.getById(id).getId());
    }

    @Test
    public void shouldDeleteQualification() throws ParseException {
        ApplicationForm application = TestData.anApplicationForm(user, program, testObjectProvider.getState(PrismState.APPLICATION_UNSUBMITTED));
        Qualification qualification = TestData.aQualification(application, testObjectProvider.getQualificationType(), TestData.aDocument(), testObjectProvider.getInstitution()); 
        save(application, qualification);
        flushAndClearSession();

        Integer id = qualification.getId();
        qualificationDAO.delete(qualification);
        flushAndClearSession();
        assertNull(sessionFactory.getCurrentSession().get(Qualification.class, id));
    }

    @Before
    public void prepare() {
        qualificationDAO = new QualificationDAO(sessionFactory);
        user = TestData.aUser(null);
        save(user);
        flushAndClearSession();
        program = testObjectProvider.getEnabledProgram();
    }
}
