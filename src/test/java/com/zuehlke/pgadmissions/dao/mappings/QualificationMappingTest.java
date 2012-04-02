package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.LanguageDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.QualificationLevel;

public class QualificationMappingTest extends AutomaticRollbackTestCase{

	
	private ApplicationForm applicationForm;
	
	@Test
	public void shouldSaveAndLoadQualification() throws Exception {

		LanguageDAO languageDAO = new LanguageDAO(sessionFactory);
		
		Qualification qualification = new QualificationBuilder().id(3)
				.q_award_date(new SimpleDateFormat("yyyy/MM/dd").parse("2001/02/02")).q_grade("").q_institution("").isCompleted(CheckedStatus.YES)
				.q_language_of_study(languageDAO.getLanguageById(1)).q_level(QualificationLevel.COLLEGE).q_name_of_programme("").q_score("")
				.q_start_date(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).q_type("").toQualification();

		sessionFactory.getCurrentSession().save(qualification);
		assertNotNull(qualification.getId());
		Integer id = qualification.getId();
		Qualification qualificationDetails = (Qualification) sessionFactory.getCurrentSession().get(Qualification.class, id);

		assertSame(qualificationDetails, qualificationDetails);

		flushAndClearSession();
		qualificationDetails = (Qualification) sessionFactory.getCurrentSession().get(Qualification.class, id);

		assertNotSame(qualification, qualificationDetails);
		assertEquals(qualification, qualificationDetails);

		assertEquals(qualification.getApplication(), qualificationDetails.getApplication());
		assertEquals(qualification.getQualificationAwardDate(), qualificationDetails.getQualificationAwardDate());
		assertEquals(qualification.getQualificationGrade(), qualificationDetails.getQualificationGrade());
		assertEquals(qualification.getQualificationInstitution(), qualificationDetails.getQualificationInstitution());
		assertEquals(qualification.getQualificationLanguage(),	qualificationDetails.getQualificationLanguage());
		assertEquals(qualification.getQualificationLevel(), qualificationDetails.getQualificationLevel());
		assertEquals(qualification.getQualificationProgramName(), qualificationDetails.getQualificationProgramName());
		assertEquals(qualification.getQualificationScore(), qualificationDetails.getQualificationScore());
		assertEquals(qualification.getQualificationStartDate(), qualificationDetails.getQualificationStartDate());
		assertEquals(qualification.getQualificationType(), qualificationDetails.getQualificationType());

	}
	
	
	@Before
	public void setUp() {
		super.setUp();

		Program program = new ProgramBuilder().code("doesntexist").description("blahblab").title("another title").toProgram();
		Project project = new ProjectBuilder().code("neitherdoesthis").description("hello").title("title two").program(program).toProject();
		save(program, project);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).project(project).toApplicationForm();
		save(applicationForm);
		flushAndClearSession();
	}
	
}
