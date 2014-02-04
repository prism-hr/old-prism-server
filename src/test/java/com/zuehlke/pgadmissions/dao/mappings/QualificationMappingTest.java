package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationTypeDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
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
		
		DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
		QualificationTypeDAO qualificationTypeDAO = new QualificationTypeDAO(sessionFactory);
		
        Qualification qualification = new QualificationBuilder().id(3)
                .awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2001/02/02")).grade("").institution("").title("")
                .languageOfStudy("Abkhazian").subject("").isCompleted(true).proofOfAward(document)
                .institutionCode("ASZ009").startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"))
                .type(qualificationTypeDAO.getAllQualificationTypes().get(0))
                .institutionCountry(domicileDAO.getAllEnabledDomiciles().get(0)).build();

		sessionFactory.getCurrentSession().save(qualification);
		assertNotNull(qualification.getId());
		Integer id = qualification.getId();
		Qualification qualificationDetails = (Qualification) sessionFactory.getCurrentSession().get(Qualification.class, id);

		assertSame(qualificationDetails, qualificationDetails);

		flushAndClearSession();
		qualificationDetails = (Qualification) sessionFactory.getCurrentSession().get(Qualification.class, id);

		assertNotSame(qualification, qualificationDetails);
		assertEquals(qualification.getId(), qualificationDetails.getId());

		assertEquals(qualification.getQualificationAwardDate(), qualificationDetails.getQualificationAwardDate());
		assertEquals(qualification.getQualificationGrade(), qualificationDetails.getQualificationGrade());
		assertEquals(qualification.getQualificationInstitution(), qualificationDetails.getQualificationInstitution());
		assertEquals(qualification.getInstitutionCountry().getId(), qualificationDetails.getInstitutionCountry().getId());
		assertEquals(qualification.getQualificationLanguage(),	qualificationDetails.getQualificationLanguage());
		assertEquals(qualification.getQualificationSubject(), qualificationDetails.getQualificationSubject());		
		assertEquals(qualification.getQualificationStartDate(), qualificationDetails.getQualificationStartDate());
		assertEquals(qualification.getQualificationType().getId(), qualificationDetails.getQualificationType().getId());
		assertEquals(qualification.getCompleted(), qualificationDetails.getCompleted());
		assertEquals(qualification.getProofOfAward().getId(), qualificationDetails.getProofOfAward().getId());
	}
	
	@Before
	public void initialise() {
	    QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
		Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();
		
		save(institution, program);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).build();
		save(applicationForm);
		flushAndClearSession();
	}
	
}
