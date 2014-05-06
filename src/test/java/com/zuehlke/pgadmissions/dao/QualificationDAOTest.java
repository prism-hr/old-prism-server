package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class QualificationDAOTest extends AutomaticRollbackTestCase {

    private User user;
    private Program program;
    private QualificationDAO qualificationDAO;

    @Test
    public void shouldGetQualificationById() throws ParseException {
        QualificationTypeDAO qualificationTypeDAO = new QualificationTypeDAO(sessionFactory);
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Qualification qualification = new QualificationBuilder().awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("").title("")
                .languageOfStudy("Abkhazian").subject("").isCompleted(true).startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"))
                .type(qualificationTypeDAO.getAllQualificationTypes().get(0)).build();
        sessionFactory.getCurrentSession().save(qualification);
        Integer id = qualification.getId();
        flushAndClearSession();

        assertEquals(qualification.getId(), qualificationDAO.getById(id).getId());
    }

    @Test
    public void shouldDeleteQualification() throws ParseException {
        ApplicationForm application = TestData.anApplicationForm(user, program, testObjectProvider.getState(PrismState.APPLICATION_UNSUBMITTED));

        QualificationTypeDAO qualificationTypeDAO = new QualificationTypeDAO(sessionFactory);
        DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Qualification qualification = new QualificationBuilder().awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("").title("")
                .languageOfStudy("Abkhazian").subject("").isCompleted(true).startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"))
                .type(qualificationTypeDAO.getAllQualificationTypes().get(0)).application(application).document(new Document().withFileName("dupa").withContent(new byte[0]).withContentType("application/pdf"))
                .build();
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
