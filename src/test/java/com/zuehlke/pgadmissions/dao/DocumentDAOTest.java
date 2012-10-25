package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationTypeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;

public class DocumentDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;
	private LanguageDAO languageDAO;
	private CountriesDAO countriesDAO;

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		Document document = new DocumentBuilder().id(1).toDocument();
		DocumentDAO documentDAO = new DocumentDAO();
		documentDAO.save(document);
	}

	@Test
	public void shouldSaveDocument() {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).toDocument();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);

		flushAndClearSession();

		Document reloadDocument = (Document) sessionFactory.getCurrentSession().get(Document.class, document.getId());
		assertEquals(document, reloadDocument);
	}

	@Test
	public void shouldGetDocumentById() {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).toDocument();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);

		flushAndClearSession();

		Document reloadDocument = dao.getDocumentbyId(document.getId());
		assertEquals(document, reloadDocument);
	}

	@Test
	public void shouldDeleteDocument() {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).toDocument();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);

		flushAndClearSession();

		Integer id = document.getId();
		dao.deleteDocument(document);
		assertNull(sessionFactory.getCurrentSession().get(Document.class, id));
	}

	@Test
	public void shouldDeleteQualificationProofOfAward() throws ParseException {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).type(DocumentType.PROOF_OF_AWARD).toDocument();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		//QualificationTypeDAO typeDao = new QualificationTypeDAO(sessionFactory);
		// typeDao.getAllQualificationTypes().get(0)
		DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
		QualificationTypeDAO qualificationTypeDAO = new QualificationTypeDAO(sessionFactory);
		dao.save(document);
		flushAndClearSession();
		Qualification qualification = new QualificationBuilder().awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("").institution("")
				.languageOfStudy("Abkhazian").subject("").isCompleted(CheckedStatus.YES)
				.startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).type(qualificationTypeDAO.getAllQualificationTypes().get(0)).institutionCountry(domicileDAO.getAllEnabledDomiciles().get(0))
				.proofOfAward(document).toQualification();
		sessionFactory.getCurrentSession().save(qualification);
		flushAndClearSession();

		Integer id = document.getId();
		dao.deleteDocument(document);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Document.class, id));

	}
	@Test
	public void shouldDeleteQualificationProofOfAwardNotYetSavedOnQUalification() throws ParseException {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).type(DocumentType.PROOF_OF_AWARD).toDocument();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);
		flushAndClearSession();

		Integer id = document.getId();
		dao.deleteDocument(document);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Document.class, id));

	}

	@Test
	public void shouldDeleteFundingProofOfAward() throws ParseException {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).type(DocumentType.SUPPORTING_FUNDING).toDocument();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);
		flushAndClearSession();
		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);
		application.setApplicant(user);

		Funding funding = new FundingBuilder().application(application).awardDate(new Date()).description("fi").type(FundingType.EMPLOYER).value("34432")
				.document(document).toFunding();
		save(application, funding);
		
		flushAndClearSession();

		Integer id = document.getId();
		dao.deleteDocument(document);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Document.class, id));

	}
	@Test
	public void shouldDeleteFundingProofOfAwardNotYetSetOnFunding() throws ParseException {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).type(DocumentType.SUPPORTING_FUNDING).toDocument();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);
		flushAndClearSession();
		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);
		application.setApplicant(user);
		
		Integer id = document.getId();
		dao.deleteDocument(document);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Document.class, id));

	}
	@Test
	public void shouldDeleteCommentDocument() throws ParseException {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).type(DocumentType.COMMENT).toDocument();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);
		flushAndClearSession();
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).toApplicationForm();
		save(application);
		flushAndClearSession();

		Comment comment = new Comment();
		comment.setApplication(application);
		comment.setComment("Excellent Application!!!");
		comment.setUser(user);
		comment.getDocuments().add(document);
		save(comment);
		
		flushAndClearSession();

		Integer id = document.getId();
		dao.deleteDocument(document);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Document.class, id));

	}

	@Before
	public void setup() {

		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		save(user, program);
		languageDAO = new LanguageDAO(sessionFactory);
		countriesDAO = new CountriesDAO(sessionFactory);
		flushAndClearSession();
	}
}
