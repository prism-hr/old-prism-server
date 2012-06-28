package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class CommentServiceTest {

	private CommentDAO commentDAOMock;
	private CommentService service;

	@Test
	public void shouldGetReviewById() {
		Comment comment = EasyMock.createMock(Comment.class);
		EasyMock.expect(commentDAOMock.get(23)).andReturn(comment);
		EasyMock.replay(comment, commentDAOMock);

		Assert.assertEquals(comment, service.getReviewById(23));
	}

	@Test
	public void shouldDelegateCommentSaveToDAO() {
		Comment appReview = EasyMock.createMock(Comment.class);
		commentDAOMock.save(appReview);
		EasyMock.replay(appReview, commentDAOMock);
		service.save(appReview);
		EasyMock.verify(commentDAOMock);
	}

	@Test
	public void shouldGetAllReviewCommentsDueNotificationToAdmin() {
		ReviewComment reviewComment = new ReviewCommentBuilder().id(1).toReviewComment();
		ReviewComment reviewComment2 = new ReviewCommentBuilder().id(2)
				.commentType(CommentType.REVIEW).adminsNotified(true).toReviewComment();
		EasyMock.expect(commentDAOMock.getReviewCommentsDueNotification()).andReturn(Arrays.asList(reviewComment2));
		EasyMock.replay(commentDAOMock);
		List<ReviewComment> commentsDueNotification = service.getReviewCommentsDueNotification();
		assertEquals(1, commentsDueNotification.size());
		assertFalse(commentsDueNotification.contains(reviewComment));
	}
	
	@Test
	public void shouldGetAllInterviewCommentsDueNotificationToAdmin() {
		InterviewComment interviewComment = new InterviewCommentBuilder().id(1).toInterviewComment();
		InterviewComment interviewComment2 = new InterviewCommentBuilder().id(2)
				.commentType(CommentType.INTERVIEW).adminsNotified(true).toInterviewComment();
		EasyMock.expect(commentDAOMock.getInterviewCommentsDueNotification()).andReturn(Arrays.asList(interviewComment2));
		EasyMock.replay(commentDAOMock);
		List<InterviewComment> commentsDueNotification = service.getInterviewCommentsDueNotification();
		assertEquals(1, commentsDueNotification.size());
		assertFalse(commentsDueNotification.contains(interviewComment));
	}
	
	@Test
	public void shouldDeclineReview(){
		final ReviewComment reviewComment = new ReviewCommentBuilder().id(1).toReviewComment();
		service = new CommentService(commentDAOMock){
			@Override
			public ReviewComment getNewReviewComment(){
				return reviewComment;
			}
		};
		RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).toUser();
		Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).toReviewer();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer).toReviewRound();
		ApplicationForm application = new ApplicationFormBuilder().latestReviewRound(reviewRound).reviewRounds(reviewRound).id(1).toApplicationForm();
		service.save(reviewComment);
		service.declineReview(reviewerUser, application);
		Assert.assertTrue(reviewComment.isDecline());
		Assert.assertEquals(reviewerUser, reviewComment.getUser());
		Assert.assertEquals(application, reviewComment.getApplication());
	}
	
	@Test
	public void shouldcreateDelegateComment(){
		final Comment comment = new CommentBuilder().id(1).toComment();
		service = new CommentService(commentDAOMock){
			@Override
			public Comment getNewGenericComment(){
				return comment;
			}
		};
		RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("joan").lastName("kyp").id(1).toUser();
		Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).toReviewer();
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer).toReviewRound();
		ApplicationForm application = new ApplicationFormBuilder().applicationAdministrator(reviewerUser).latestReviewRound(reviewRound).reviewRounds(reviewRound).id(1).toApplicationForm();
		service.save(comment);
		service.createDelegateComment(reviewerUser, application);
		Assert.assertEquals("Delegated Application for processing to joan kyp", comment.getComment());
		Assert.assertEquals(reviewerUser, comment.getUser());
		Assert.assertEquals(application, comment.getApplication());
	}
	
	@Before
	public void setUp() {
		commentDAOMock = EasyMock.createMock(CommentDAO.class);
		service = new CommentService(commentDAOMock);
	}
}
