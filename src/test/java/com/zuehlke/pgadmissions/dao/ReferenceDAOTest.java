package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
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
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		
		save(user);
		
		QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
		Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();	
		
		save(institution, program);
		
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		
		ReferenceComment reference = new ReferenceCommentBuilder().user(user).comment("comment").application(application).build();
		sessionFactory.getCurrentSession().save(reference);
		flushAndClearSession();
		
		ReferenceDAO referenceDAO = new ReferenceDAO(sessionFactory);
		ReferenceComment returnedReference = referenceDAO.getReferenceById(reference.getId());
		assertEquals(reference.getId(), returnedReference.getId());
	}
	

}
