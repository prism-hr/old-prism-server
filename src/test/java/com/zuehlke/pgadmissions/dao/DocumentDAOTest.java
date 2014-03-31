package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class DocumentDAOTest extends AutomaticRollbackTestCase {

    private RegisteredUser user;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        Document document = new DocumentBuilder().id(1).build();
        DocumentDAO documentDAO = new DocumentDAO();
        documentDAO.save(document);
    }

    @Test
    public void shouldSaveDocument() {
        Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).build();
        DocumentDAO dao = new DocumentDAO(sessionFactory);
        dao.save(document);

        flushAndClearSession();

        Document reloadDocument = (Document) sessionFactory.getCurrentSession().get(Document.class, document.getId());
        assertEquals(document.getId(), reloadDocument.getId());
    }

    @Test
    public void shouldGetDocumentById() {
        Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).build();
        DocumentDAO dao = new DocumentDAO(sessionFactory);
        dao.save(document);

        flushAndClearSession();

        Document reloadDocument = dao.getDocumentbyId(document.getId());
        assertEquals(document.getId(), reloadDocument.getId());
    }

	@Test
	public void shouldDeleteDocument() {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).build();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);

		flushAndClearSession();

		Integer id = document.getId();
		dao.deleteDocument(document);
		assertNull(sessionFactory.getCurrentSession().get(Document.class, id));
	}

	@Test
	public void shouldDeleteQualificationProofOfAward() throws ParseException {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).type(DocumentType.PROOF_OF_AWARD).build();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
		QualificationTypeDAO qualificationTypeDAO = new QualificationTypeDAO(sessionFactory);
		dao.save(document);
		flushAndClearSession();
        Qualification qualification = new QualificationBuilder()
                .awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("").institution("").title("")
                .languageOfStudy("Abkhazian").subject("").isCompleted(true).institutionCode("AS009Z")
                .startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09"))
                .type(qualificationTypeDAO.getAllQualificationTypes().get(0))
                .institutionCountry(domicileDAO.getAllEnabledDomiciles().get(0)).proofOfAward(document).build();
		sessionFactory.getCurrentSession().save(qualification);
		flushAndClearSession();

		Integer id = document.getId();
		dao.deleteDocument(document);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Document.class, id));

	}

	@Test
	public void shouldDeleteQualificationProofOfAwardNotYetSavedOnQUalification() throws ParseException {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).type(DocumentType.PROOF_OF_AWARD).build();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);
		flushAndClearSession();

		Integer id = document.getId();
		dao.deleteDocument(document);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Document.class, id));

	}

	@Test(expected = ObjectNotFoundException.class)
	public void shouldDeleteFundingProofOfAward() throws ParseException {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).type(DocumentType.SUPPORTING_FUNDING).build();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);
		flushAndClearSession();
		ApplicationForm application = new ApplicationForm();
		application.setAdvert(program);
		application.setApplicant(user);

		Funding funding = new FundingBuilder().application(application).awardDate(new Date()).description("fi").type(FundingType.EMPLOYER).value("34432")
				.document(document).build();
		save(application, funding);

		flushAndClearSession();

		Integer id = document.getId();
		dao.deleteDocument(document);
		
		sessionFactory.getCurrentSession().get(Document.class, id);
	}

	@Test
	public void shouldDeleteFundingProofOfAwardNotYetSetOnFunding() throws ParseException {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).type(DocumentType.SUPPORTING_FUNDING).build();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);
		flushAndClearSession();
		ApplicationForm application = new ApplicationForm();
		application.setAdvert(program);
		application.setApplicant(user);

		Integer id = document.getId();
		dao.deleteDocument(document);
		flushAndClearSession();
		
		assertNull(sessionFactory.getCurrentSession().get(Document.class, id));
	}

	@Test
	public void shouldDeleteCommentDocument() throws ParseException {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).type(DocumentType.COMMENT).build();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);
		flushAndClearSession();
		ApplicationForm application = new ApplicationFormBuilder().id(1).advert(program).applicant(user).build();
		save(application);
		flushAndClearSession();

		Comment comment = new Comment();
		comment.setApplication(application);
		comment.setContent("Excellent Application!!!");
		comment.setUser(user);
		comment.getDocuments().add(document);
		save(comment);

		flushAndClearSession();

		Integer id = document.getId();
		dao.deleteDocument(document);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Document.class, id));
	}

	@Test
	public void shouldDeleteLanguageQualificationDocument() throws ParseException {
		Document document = new DocumentBuilder().fileName("bob").content("aa".getBytes()).type(DocumentType.LANGUAGE_QUALIFICATION).build();
		DocumentDAO dao = new DocumentDAO(sessionFactory);
		dao.save(document);
		flushAndClearSession();
		
		LanguageQualification languageQualification = new LanguageQualificationBuilder()
		.dateOfExamination(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).examTakenOnline(true)
		.languageQualification(LanguageQualificationEnum.IELTS_ACADEMIC).listeningScore("6").overallScore("6").readingScore("6").speakingScore("6")
		.writingScore("6").languageQualificationDocument(document).build();
		
		save(languageQualification);
		flushAndClearSession();

		Integer id = document.getId();
		dao.deleteDocument(document);

		try {
		    sessionFactory.getCurrentSession().get(Document.class, id);
		    fail("Should have thrown an ObjectNotFoundException");
		} catch (ObjectNotFoundException e) {
		    // do nothing
		}
		
		languageQualification = (LanguageQualification) sessionFactory.getCurrentSession().get(LanguageQualification.class, languageQualification.getId());
		
		assertNull(languageQualification.getLanguageQualificationDocument());
	}

	@Before
	public void prepare() {
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();
		save(user);
		flushAndClearSession();
		program = testObjectProvider.getEnabledProgram();
	}
}
