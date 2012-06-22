package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.apache.struts.action.ActionFormBean;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

public class ReferenceMappingTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;

	@Test
	public void shouldSaveAndLoadReferenceWithAndDocument(){
		Document document = new DocumentBuilder().content("aa".getBytes()).fileName("gekko").type(DocumentType.CV).toDocument();


		Reference reference = new ReferenceBuilder().document(document).toReference();
		
		sessionFactory.getCurrentSession().save(reference);
		assertNotNull(reference.getId());
		assertNotNull(document.getId());
		Integer id = reference.getId();
		Reference reloadedReference = (Reference) sessionFactory.getCurrentSession().get(Reference.class, id);

		assertSame(reference, reloadedReference);

		flushAndClearSession();
		reloadedReference = (Reference) sessionFactory.getCurrentSession().get(Reference.class, id);

		assertNotSame(reference, reloadedReference);
		assertEquals(reference, reloadedReference);
		assertEquals(document,reloadedReference.getDocument());

		
		assertNotNull(reloadedReference.getLastUpdated());
	}
	
	@Test
	public void shouldSaveAndLoadReferenceWithAndComment(){
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		save(user, program);
		ApplicationForm application = new ApplicationFormBuilder().applicant(user).program(program).toApplicationForm();

		sessionFactory.getCurrentSession().save(application);

		Comment comment = new CommentBuilder().comment("lala").user(new RegisteredUserBuilder().id(1).toUser()).application(application).toComment();
		sessionFactory.getCurrentSession().save(comment);
		
		Reference reference = new ReferenceBuilder().comment(comment).toReference();
		
		sessionFactory.getCurrentSession().save(reference);
		assertNotNull(reference.getId());
		assertNotNull(comment.getId());
		Integer id = reference.getId();
		Reference reloadedReference = (Reference) sessionFactory.getCurrentSession().get(Reference.class, id);
		
		assertSame(reference, reloadedReference);
		
		flushAndClearSession();
		reloadedReference = (Reference) sessionFactory.getCurrentSession().get(Reference.class, id);
		
		assertNotSame(reference, reloadedReference);
		assertEquals(reference, reloadedReference);
		assertEquals(comment,reloadedReference.getComment());
		
		
		assertNotNull(reloadedReference.getLastUpdated());
	}
	
	
}

