package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

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
	public void shouldSaveAndLoadGenericComment() {

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
	
	@Test
	public void shouldSaveAndLoadStateChangeComment() {

		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).toApplicationForm();
		save(application);
		flushAndClearSession();

		StateChangeComment validationComment = new StateChangeComment();
		validationComment.setApplication(application);
		validationComment.setComment("Excellent Application!!!");
		validationComment.setUser(user);
		validationComment.setType(CommentType.REVIEW_EVALUATION);
		assertNull(validationComment.getId());

		commentDAO.save(validationComment);

		assertNotNull(validationComment.getId());
		Integer id = validationComment.getId();
		Comment reloadedComment = commentDAO.get(id);
		assertSame(validationComment, reloadedComment);

		flushAndClearSession();

		reloadedComment = commentDAO.get(id);
	
		assertNotSame(validationComment, reloadedComment);
		assertEquals(validationComment, reloadedComment);
		assertEquals(user, reloadedComment.getUser());
		assertEquals(CommentType.REVIEW_EVALUATION, reloadedComment.getType());
		assertEquals(validationComment.getComment(), reloadedComment.getComment());
		
		
		assertTrue(reloadedComment instanceof StateChangeComment);
	}
	
	@Test
	public void shouldSaveAndLoadReviewComment() {
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		ReviewComment reviewComment = new ReviewCommentBuilder().application(application).adminsNotified(CheckedStatus.NO).comment("comment").user(user).commentType(CommentType.REVIEW).toReviewComment();
		
		assertNull(reviewComment.getId());
		
		commentDAO.save(reviewComment);
		
		assertNotNull(reviewComment.getId());
		Integer id = reviewComment.getId();
		Comment reloadedComment = commentDAO.get(id);
		assertSame(reviewComment, reloadedComment);
		
		flushAndClearSession();
		
		reloadedComment = commentDAO.get(id);
		
		assertNotSame(reviewComment, reloadedComment);
		assertEquals(reviewComment, reloadedComment);
		assertEquals(user, reloadedComment.getUser());
		assertEquals(CommentType.REVIEW, reloadedComment.getType());
		assertEquals(reviewComment.getComment(), reloadedComment.getComment());
		
		
		assertTrue(reloadedComment instanceof ReviewComment);
	}
	
	@Test
	public void shouldGetAllComments() {
		
		int noOfCommentsBefore = commentDAO.getAllComments().size(); 
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		Comment comment = new CommentBuilder().user(user).createdTimeStamp(new Date()).comment("comment").application(application).toComment();
		ReviewComment reviewComment = new ReviewCommentBuilder().application(application).adminsNotified(CheckedStatus.NO).comment("comment").user(user).commentType(CommentType.REVIEW).toReviewComment();
		
		assertNull(reviewComment.getId());
		
		commentDAO.save(comment);
		commentDAO.save(reviewComment);
		
		List<Comment> reloadedComments = commentDAO.getAllComments();
		
		flushAndClearSession();
		
		reloadedComments = commentDAO.getAllComments();
		
		
		assertEquals(noOfCommentsBefore+2, reloadedComments.size());
		assertTrue(reloadedComments.contains(comment));
		assertTrue(reloadedComments.contains(reviewComment));
	}
	
	@Test
	public void shouldGetAllCommentsDueAdminEmailNotification() {
		
		int noOfReviewCommentsBefore = commentDAO.getReviewCommentsDueNotification().size(); 
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		Comment comment = new CommentBuilder().user(user).comment("comment").application(application).toComment();
		ReviewComment reviewComment = new ReviewCommentBuilder().application(application).adminsNotified(CheckedStatus.NO).comment("comment").user(user).commentType(CommentType.REVIEW).toReviewComment();
		ReviewComment reviewComment1 = new ReviewCommentBuilder().application(application).adminsNotified(CheckedStatus.YES).comment("comment").user(user).commentType(CommentType.REVIEW).toReviewComment();
		ReviewComment reviewComment2 = new ReviewCommentBuilder().application(application).adminsNotified(CheckedStatus.YES).comment("comment").user(user).commentType(CommentType.GENERIC).toReviewComment();
		
		assertNull(reviewComment.getId());
		
		commentDAO.save(comment);
		commentDAO.save(reviewComment);
		commentDAO.save(reviewComment1);
		commentDAO.save(reviewComment2);
		
		List<ReviewComment> reloadedComments = commentDAO.getReviewCommentsDueNotification();
		
		flushAndClearSession();
		
		reloadedComments = commentDAO.getReviewCommentsDueNotification();
		
		
		assertEquals(noOfReviewCommentsBefore+1, reloadedComments.size());
		assertFalse(reloadedComments.contains(comment));
		assertTrue(reloadedComments.contains(reviewComment));
	}


}
