package com.zuehlke.pgadmissions.dao;

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
import com.zuehlke.pgadmissions.domain.enums.QualificationLevel;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class QualificationDAOTest extends AutomaticRollbackTestCase {

	
	private RegisteredUser user;
	private Program program;
	private Project project;

	
	@Test(expected=NullPointerException.class)
	public void shouldThrowNullPointerException(){
		QualificationDAO qualificationDAO = new QualificationDAO();
		Qualification qualification =new QualificationBuilder().toQualification();
		qualificationDAO.delete(qualification);
	}
	
	@Test
	public void shouldDeleteQualification() throws ParseException{
		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		LanguageDAO languageDAO = new LanguageDAO(sessionFactory);
		Qualification qualification =new QualificationBuilder().q_award_date(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).q_grade("")
				.q_institution("").q_language_of_study(languageDAO.getLanguageById(1)).q_level(QualificationLevel.COLLEGE).q_name_of_programme("")
				.q_start_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).q_type("").toQualification();
		save(application, qualification);
		flushAndClearSession();
		
		Integer id = qualification.getId();
		QualificationDAO qualificationDAO = new QualificationDAO(sessionFactory);
		qualificationDAO.delete(qualification);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Qualification.class, id));
	}
	
	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").description("blahblab").title("another title").toProgram();
		project = new ProjectBuilder().code("neitherdoesthis").description("hello").title("title two").program(program).toProject();
		save(user, program, project);

		flushAndClearSession();
	}
}
