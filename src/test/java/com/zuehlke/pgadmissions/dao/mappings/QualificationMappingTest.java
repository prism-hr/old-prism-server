package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.dao.LanguageDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

public class QualificationMappingTest extends AutomaticRollbackTestCase{

	
	private ApplicationForm applicationForm;
	
	@Test
	public void shouldSaveAndLoadQualification() throws Exception {

		Document document = new Document();		
		document.setContent("s".getBytes());
		document.setFileName("name.txt");
		document.setContentType("bob");
		document.setType(DocumentType.PERSONAL_STATEMENT);
		sessionFactory.getCurrentSession().save(document);
		flushAndClearSession();
		
		LanguageDAO languageDAO = new LanguageDAO(sessionFactory);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Qualification qualification = new QualificationBuilder().id(3)
				.awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2001/02/02")).grade("").institution("")
				.languageOfStudy(languageDAO.getLanguageById(1)).subject("").isCompleted(CheckedStatus.YES).proofOfAward(document)
				.startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).type("").institutionCountry(countriesDAO.getAllCountries().get(0)).toQualification();

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
		assertEquals(qualification.getInstitutionCountry(), qualificationDetails.getInstitutionCountry());
		assertEquals(qualification.getQualificationLanguage(),	qualificationDetails.getQualificationLanguage());
		assertEquals(qualification.getQualificationSubject(), qualificationDetails.getQualificationSubject());		
		assertEquals(qualification.getQualificationStartDate(), qualificationDetails.getQualificationStartDate());
		assertEquals(qualification.getQualificationType(), qualificationDetails.getQualificationType());
		assertEquals(qualification.getCompleted(), qualificationDetails.getCompleted());
		assertEquals(qualification.getProofOfAward(), qualificationDetails.getProofOfAward());

	}
	
	
	@Before
	public void setUp() {
		super.setUp();

		Program program = new ProgramBuilder().code("doesntexist").description("blahblab").title("another title").toProgram();
		
		save(program);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).toApplicationForm();
		save(applicationForm);
		flushAndClearSession();
	}
	
}
