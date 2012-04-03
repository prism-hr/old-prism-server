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
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.QualificationLevel;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class QualificationDAOTest extends AutomaticRollbackTestCase {

	
	private RegisteredUser user;
	private Program program;
	private Project project;
	private LanguageDAO languageDAO;
	private QualificationDAO qualificationDAO;
	private CountriesDAO countriesDAO;

		
	@Test
	public void shouldGetQualificationById() throws ParseException{
		Qualification qualification =new QualificationBuilder().awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("")
				.institution("").languageOfStudy(languageDAO.getLanguageById(1)).level(QualificationLevel.COLLEGE).subject("").isCompleted(CheckedStatus.YES)
				.startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).type("").institutionCountry(countriesDAO.getAllCountries().get(0)).toQualification();
		sessionFactory.getCurrentSession().save(qualification);
		Integer id = qualification.getId();
		flushAndClearSession();
		
		assertEquals(qualification, qualificationDAO.getQualificationById(id));
		
		
	}
	
	@Test
	public void shouldDeleteQualification() throws ParseException{
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);

		Qualification qualification =new QualificationBuilder().awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("")
				.institution("").languageOfStudy(languageDAO.getLanguageById(1)).level(QualificationLevel.COLLEGE).subject("").isCompleted(CheckedStatus.YES)
				.startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).type("").institutionCountry(countriesDAO.getAllCountries().get(0)).toQualification();
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
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").description("blahblab").title("another title").toProgram();
		project = new ProjectBuilder().code("neitherdoesthis").description("hello").title("title two").program(program).toProject();
		save(user, program, project);
		languageDAO = new LanguageDAO(sessionFactory);
		countriesDAO = new CountriesDAO(sessionFactory);
		flushAndClearSession();
	}
}
