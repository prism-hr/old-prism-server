package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ReferenceDAOTest extends AutomaticRollbackTestCase {

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		ReferenceDAO referenceDAO = new ReferenceDAO();
		referenceDAO.getReferenceById(1);
	}
	
	@Test
	public void shouldGetReferenceById(){
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		save(user);
		
		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();	
		
		save(program);
		
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		
		ReferenceComment reference = new ReferenceCommentBuilder().user(user).comment("comment").application(application).toReferenceComment();
		sessionFactory.getCurrentSession().save(reference);
		flushAndClearSession();
		
		ReferenceDAO referenceDAO = new ReferenceDAO(sessionFactory);
		ReferenceComment returnedReference = referenceDAO.getReferenceById(reference.getId());
		assertEquals(reference, returnedReference);
	}
	

}
