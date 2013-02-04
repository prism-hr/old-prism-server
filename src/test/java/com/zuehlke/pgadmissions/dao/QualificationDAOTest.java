package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

public class QualificationDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;	
	private QualificationDAO qualificationDAO;

	@Test
	public void shouldGetQualificationById() throws ParseException{
	    QualificationTypeDAO qualificationTypeDAO = new QualificationTypeDAO(sessionFactory);
	    DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Qualification qualification = new QualificationBuilder()
                .awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("").institution("").title("")
                .languageOfStudy("Abkhazian").subject("").isCompleted(CheckedStatus.YES).institutionCode("AS009Z")
                .startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"))
                .type(qualificationTypeDAO.getAllQualificationTypes().get(0))
                .institutionCountry(domicileDAO.getAllEnabledDomiciles().get(0)).build();
		sessionFactory.getCurrentSession().save(qualification);
		Integer id = qualification.getId();
		flushAndClearSession();
		
		assertEquals(qualification.getId(), qualificationDAO.getQualificationById(id).getId());
	}

	@Test
	public void shouldSaveQualification() throws ParseException{
	    QualificationTypeDAO qualificationTypeDAO = new QualificationTypeDAO(sessionFactory);
	    DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Qualification qualification = new QualificationBuilder()
                .awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("").institution("").title("")
                .languageOfStudy("Abkhazian").subject("").isCompleted(CheckedStatus.YES).institutionCode("AS009Z")
                .startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"))
                .type(qualificationTypeDAO.getAllQualificationTypes().get(0))
                .institutionCountry(domicileDAO.getAllEnabledDomiciles().get(0)).build();
		qualificationDAO.save(qualification);
		flushAndClearSession();		
		Integer id = qualification.getId();
		assertEquals(qualification.getId(), ((Qualification) sessionFactory.getCurrentSession().get(Qualification.class, id)).getId());
	}
	
	@Test
	public void shouldDeleteQualification() throws ParseException{
		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);
		application.setApplicant(user);		
		
		QualificationTypeDAO qualificationTypeDAO = new QualificationTypeDAO(sessionFactory);
		 DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
        Qualification qualification = new QualificationBuilder()
                .awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("").institution("").title("")
                .languageOfStudy("Abkhazian").subject("").isCompleted(CheckedStatus.YES).institutionCode("AS009Z")
                .startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"))
                .type(qualificationTypeDAO.getAllQualificationTypes().get(0))
                .institutionCountry(domicileDAO.getAllEnabledDomiciles().get(0)).build();
		save(application, qualification);
		flushAndClearSession();
		
		Integer id = qualification.getId();
		qualificationDAO.delete(qualification);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Qualification.class, id));
	}
	
	@Before
	public void setup() {
		qualificationDAO = new QualificationDAO(sessionFactory);
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		program = new ProgramBuilder().code("doesntexist").title("another title").build();
		save(user, program);
		flushAndClearSession();
	}
}
