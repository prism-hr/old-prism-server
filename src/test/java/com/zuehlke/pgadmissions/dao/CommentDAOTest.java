package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
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
		
		ReviewComment reviewComment = new ReviewCommentBuilder().application(application).adminsNotified(false).comment("comment").user(user).commentType(CommentType.REVIEW).toReviewComment();
		
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
	public void shouldGetAllReviewCommentsDueAdminEmailNotification() {
		
		
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		Comment comment = new CommentBuilder().user(user).comment("comment").application(application).toComment();
		ReviewComment reviewComment = new ReviewCommentBuilder().application(application).adminsNotified(false).comment("comment").user(user).commentType(CommentType.REVIEW).toReviewComment();
		ReviewComment reviewComment1 = new ReviewCommentBuilder().application(application).adminsNotified(true).comment("comment").user(user).commentType(CommentType.REVIEW).toReviewComment();
		ReviewComment reviewComment2 = new ReviewCommentBuilder().application(application).adminsNotified(false).comment("comment").user(user).commentType(CommentType.GENERIC).toReviewComment();
		
		
		save(comment, reviewComment, reviewComment1,reviewComment2);
				
		flushAndClearSession();
		
		List<ReviewComment> reloadedComments = commentDAO.getReviewCommentsDueNotification();
		
		assertFalse(reloadedComments.contains(comment));
		assertFalse(reloadedComments.contains(reviewComment2));
		assertFalse(reloadedComments.contains(reviewComment1));
		assertTrue(reloadedComments.contains(reviewComment));
		
	}

	@Test
	public void shouldGetAllInterviewCommentsDueAdminEmailNotification() {
		
		
		
		ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).toApplicationForm();
		save(application);
		flushAndClearSession();
		
		Comment comment = new CommentBuilder().user(user).comment("comment").application(application).toComment();
		InterviewComment interviewComment1 = new InterviewCommentBuilder().user(user).application(application).adminsNotified(CheckedStatus.NO).comment("comment").commentType(CommentType.INTERVIEW).toInterviewComment();
		InterviewComment interviewComment2 = new InterviewCommentBuilder().user(user).application(application).adminsNotified(CheckedStatus.NO).comment("comment").commentType(CommentType.REVIEW).toInterviewComment();
		InterviewComment interviewComment3 = new InterviewCommentBuilder().user(user).application(application).adminsNotified(null).comment("comment").commentType(CommentType.INTERVIEW).toInterviewComment();
		InterviewComment interviewComment4 = new InterviewCommentBuilder().user(user).application(application).adminsNotified(CheckedStatus.YES).comment("comment").commentType(CommentType.INTERVIEW).toInterviewComment();
		
		save(comment, interviewComment1, interviewComment2, interviewComment3, interviewComment4);
		
		flushAndClearSession();
		
		List<InterviewComment> reloadedComments = commentDAO.getInterviewCommentsDueNotification();
		
		assertFalse(reloadedComments.contains(comment));
		assertTrue(reloadedComments.contains(interviewComment1));
		assertFalse(reloadedComments.contains(interviewComment2));
		assertTrue(reloadedComments.contains(interviewComment3));
		assertFalse(reloadedComments.contains(interviewComment4));
	}
	

}
