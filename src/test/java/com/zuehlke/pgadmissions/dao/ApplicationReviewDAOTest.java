package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ApplicationReviewDAOTest extends AutomaticRollbackTestCase{

	private ApplicationReviewDAO applicationReviewDAO;
	private RegisteredUser reviewer;
	
	@Before
	public void setup(){
		applicationReviewDAO = new ApplicationReviewDAO(sessionFactory);
	}
	
	@Test
	public void shouldSaveAndLoadReview(){
		RegisteredUser user = new RegisteredUserBuilder().username("username").password("password").accountNonExpired(false).accountNonLocked(false)
				.credentialsNonExpired(false).enabled(false).toUser();
		
		save(user);
		flushAndClearSession();
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).country_ob("UK").description_of_research("Amazing research").dob("1985/03/02").gender("Female").nationality("British").title("Miss").registeredUser(user).toApplicationForm();
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
	

	
}
