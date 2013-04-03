package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationTypeDAO;
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

public class DocumentMappingTest extends AutomaticRollbackTestCase {

	private ApplicationForm applicationForm;
	private RegisteredUser applicant;

	@Test
	public void shouldSaveAndLoadDocument() {
		Document document = new Document();
		document.setUploadedBy(applicant);
		document.setContent("s".getBytes());
		document.setFileName("name.txt");
		document.setContentType("bob");
		document.setType(DocumentType.PERSONAL_STATEMENT);

		sessionFactory.getCurrentSession().saveOrUpdate(document);

		flushAndClearSession();

		Document reloadedDoc = (Document) sessionFactory.getCurrentSession().get(Document.class, document.getId());

		assertArrayEquals("s".getBytes(), reloadedDoc.getContent());
		assertEquals("name.txt", reloadedDoc.getFileName());
		assertEquals("bob", reloadedDoc.getContentType());
		assertEquals(DocumentType.PERSONAL_STATEMENT, reloadedDoc.getType());
		assertNotNull(reloadedDoc.getDateUploaded());
	}

	@Test
	public void shouldSLoadDocumentWithQualification() throws ParseException {
		Document document = new Document();
		document.setContent("s".getBytes());
		document.setFileName("name.txt");
		document.setContentType("bob");
		document.setType(DocumentType.PROOF_OF_AWARD);
		document.setUploadedBy(applicant);
		sessionFactory.getCurrentSession().save(document);

		flushAndClearSession();

		DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
		QualificationTypeDAO qualificationTypeDAO = new QualificationTypeDAO(sessionFactory);
        Qualification qualification = new QualificationBuilder().id(3)
                .awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2001/02/02")).grade("").institution("")
                .institutionCode("ABC").languageOfStudy("Abkhazian").subject("").title("").isCompleted(CheckedStatus.YES)
                .proofOfAward(document).startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"))
                .type(qualificationTypeDAO.getAllQualificationTypes().get(0))
                .institutionCountry(domicileDAO.getAllEnabledDomiciles().get(0)).build();

		sessionFactory.getCurrentSession().save(qualification);
		flushAndClearSession();

		Document reloadedDoc = (Document) sessionFactory.getCurrentSession().get(Document.class, document.getId());

		assertEquals(qualification.getId(), reloadedDoc.getQualification().getId());
	}

	@Before
	public void setUp() {
		super.setUp();

		Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
		save(program);

		applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).build();
		save(applicationForm);
		flushAndClearSession();
	}
}
