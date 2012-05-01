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
import org.junit.Ignore;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class CommentDAOTest extends AutomaticRollbackTestCase {

	private CommentDAO commentDAO;
	private RegisteredUser user;
	private Program program;

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		CommentDAO reviewDAO = new CommentDAO();
		Comment review = new CommentBuilder().id(1).toComment();
		reviewDAO.save(review);
	}

	@Before
	public void setup() {
		commentDAO = new CommentDAO(sessionFactory);
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		save(user, program);

		flushAndClearSession();

	}

	@Test
	public void shouldSaveAndLoadReview() {

		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).toApplicationForm();
		save(application);
		flushAndClearSession();

		Comment review = new Comment();
		review.setApplication(application);
		review.setComment("Excellent Application!!!");
		review.setUser(user);

		assertNull(review.getId());

		commentDAO.save(review);

		assertNotNull(review.getId());
		Integer id = review.getId();
		Comment reloadedReview = commentDAO.get(id);
		assertSame(review, reloadedReview);

		flushAndClearSession();

		reloadedReview = commentDAO.get(id);
		assertNotSame(review, reloadedReview);
		assertEquals(review, reloadedReview);
		assertEquals(review.getUser(), user);
		assertEquals(review.getComment(), reloadedReview.getComment());
	}

	@Ignore
	@Test
	public void shouldGetReviewByApplicationForm() {

		ApplicationForm applicationOne = new ApplicationFormBuilder().id(4).program(program).applicant(user).toApplicationForm();
		save(applicationOne);
		flushAndClearSession();

		Comment reviewOne = new Comment();
		reviewOne.setApplication(applicationOne);
		reviewOne.setComment("Excellent Application!!!");
		reviewOne.setUser(user);

		Comment reviewThree = new Comment();
		reviewThree.setApplication(applicationOne);
		reviewThree.setComment("Excellent Application!!!");
		reviewThree.setUser(user);

		save(reviewOne, reviewThree);

		flushAndClearSession();

		List<Comment> reviewsByApplication = commentDAO.getCommentsByApplication(applicationOne);

		assertEquals(2, reviewsByApplication.size());
		assertTrue(reviewsByApplication.containsAll(Arrays.asList(reviewOne, reviewThree)));

	}
}
