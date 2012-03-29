package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationReviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ApplicationReviewDAOTest extends AutomaticRollbackTestCase{

	private ApplicationReviewDAO applicationReviewDAO;
	private RegisteredUser user;
	private Project project;
	
	@Before
	public void setup() {
		applicationReviewDAO = new ApplicationReviewDAO(sessionFactory);
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		Program program = new ProgramBuilder().code("doesntexist").description("blahblab").title("another title").toProgram();
		project = new ProjectBuilder().code("neitherdoesthis").description("hello").title("title two").program(program).toProject();
		save(user, program, project);

		flushAndClearSession();
		
	}
	
	@Test
	public void shouldSaveAndLoadReview(){

		
		ApplicationForm application = new ApplicationFormBuilder().id(1).project(project).applicant(user).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		ApplicationReview review = new ApplicationReview();
		review.setApplication(application);
		review.setComment("Excellent Application!!!");
		review.setUser(user);
		
		assertNull(review.getId());
		
		applicationReviewDAO.save(review);
		
		assertNotNull(review.getId());
		Integer id = review.getId();
		ApplicationReview reloadedReview = applicationReviewDAO.get(id);
		assertSame(review, reloadedReview);
		
		flushAndClearSession();

		reloadedReview = applicationReviewDAO.get(id);
		assertNotSame(review, reloadedReview);
		assertEquals(review, reloadedReview);
		assertEquals(review.getUser(), user);
		assertEquals(review.getComment(), reloadedReview.getComment());
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldThrowNullPointerException(){
		ApplicationReviewDAO reviewDAO = new ApplicationReviewDAO();
		ApplicationReview review = new ApplicationReviewBuilder().id(1).toApplicationReview();
		reviewDAO.save(review);
	}

	@Test
	public void shouldGetReviewByApplicationForm(){

		
		ApplicationForm applicationOne = new ApplicationFormBuilder().id(1).project(project).applicant(user).toApplicationForm();
		ApplicationForm applicationTwo  = new ApplicationFormBuilder().id(1).project(project).applicant(user).toApplicationForm();
		save(applicationOne, applicationTwo);
		flushAndClearSession();
		
		ApplicationReview reviewOne = new ApplicationReview();
		reviewOne.setApplication(applicationOne);
		reviewOne.setComment("Excellent Application!!!");
		reviewOne.setUser(user);

		ApplicationReview reviewTwo = new ApplicationReview();
		reviewTwo.setApplication(applicationTwo);
		reviewTwo.setComment("Excellent Application!!!");
		reviewTwo.setUser(user);
		
		
		ApplicationReview reviewThree = new ApplicationReview();
		reviewThree.setApplication(applicationOne);
		reviewThree.setComment("Excellent Application!!!");
		reviewThree.setUser(user);
		
		
		save(reviewOne, reviewTwo, reviewThree);
		
		flushAndClearSession();
		
		List<ApplicationReview> reviewsByApplication = applicationReviewDAO.getReviewsByApplication(applicationOne);
		
		assertEquals(2, reviewsByApplication.size());
		assertTrue(reviewsByApplication.containsAll(Arrays.asList(reviewOne, reviewThree)));
		
		
	}
}
