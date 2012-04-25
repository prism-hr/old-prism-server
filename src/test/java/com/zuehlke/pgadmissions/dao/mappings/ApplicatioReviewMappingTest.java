package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationReviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ApplicatioReviewMappingTest extends AutomaticRollbackTestCase{

	@Test
	public void shouldSaveAndLoadApplicationReview(){
		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();		
		save( program);
		
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		RegisteredUser reviewer = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		save(applicant, reviewer);
		
		ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).toApplicationForm();		
		save(applicationForm);
		
		flushAndClearSession();
		
		
		ApplicationReview applicationReview = new ApplicationReviewBuilder().application(applicationForm).comment("comment").user(reviewer).toApplicationReview();
		save(applicationReview);
		
		assertNotNull(applicationReview.getId());
		Integer id = applicationReview.getId();
		ApplicationReview reloadedApplicationReview = (ApplicationReview) sessionFactory.getCurrentSession().get(ApplicationReview.class, id);
		assertSame(applicationReview, reloadedApplicationReview);

		flushAndClearSession();

		reloadedApplicationReview  =(ApplicationReview) sessionFactory.getCurrentSession().get(ApplicationReview.class, id);
		assertNotSame(applicationReview, reloadedApplicationReview);
		assertEquals(applicationReview, reloadedApplicationReview);

		assertEquals(reviewer, reloadedApplicationReview.getUser());
		assertEquals("comment", reloadedApplicationReview.getComment());
		
	
	}

}
