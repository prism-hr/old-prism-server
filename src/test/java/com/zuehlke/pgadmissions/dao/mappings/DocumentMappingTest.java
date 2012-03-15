package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

public class DocumentMappingTest extends AutomaticRollbackTestCase {

	private ApplicationForm applicationForm;

	
	@Test
	@Ignore
	public void shouldSaveAndLoadDocument(){
		Document document = new Document();
		StringBuilder builder = new StringBuilder();
		for(int i =0 ; i < 5000000;i++){
			builder.append("a");
		}
		String contentString = builder.toString();
		document.setContent(contentString.getBytes());
		document.setFileName("name.txt");
		document.setContentType("bob");
		document.setType(DocumentType.PERSONAL_STATEMENT);
		sessionFactory.getCurrentSession().save(document);
		assertNotNull(document.getId());
		
		Document reloadedDoc = (Document) sessionFactory.getCurrentSession().get(Document.class, document.getId());
		assertSame(document, reloadedDoc);
		
		flushAndClearSession();
		
		reloadedDoc = (Document) sessionFactory.getCurrentSession().get(Document.class, document.getId());
		assertNotSame(document, reloadedDoc);
		assertEquals(document,reloadedDoc);
		assertEquals("name.txt", reloadedDoc.getFileName());
		assertEquals("bob", reloadedDoc.getContentType());
		assertEquals(contentString, new String(reloadedDoc.getContent()));
		assertEquals(DocumentType.PERSONAL_STATEMENT, reloadedDoc.getType());
		assertNotNull(reloadedDoc.getDateUploaded());
	}
	
	@Test	
	public void shouldLoadDocumentWithApplicaionForm(){
		Document document = new Document();
		document.setContent("s".getBytes());
		document.setFileName("name.txt");
		document.setContentType("bob");
		document.setType(DocumentType.PERSONAL_STATEMENT);
		
		applicationForm.getSupportingDocuments().add(document);	
		
		sessionFactory.getCurrentSession().saveOrUpdate(applicationForm);
		
		flushAndClearSession();
		
		Document reloadedDoc = (Document) sessionFactory.getCurrentSession().get(Document.class, document.getId());

		assertEquals(applicationForm, reloadedDoc.getApplicationForm());
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
