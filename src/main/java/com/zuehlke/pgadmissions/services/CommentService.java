package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Service
public class CommentService {

	private final CommentDAO commentDAO;

	CommentService() {
		this(null);
	}

	@Autowired
	public CommentService(CommentDAO commentDAO) {
		this.commentDAO = commentDAO;
	}

	@Transactional
	public void save(Comment comment) {
		commentDAO.save(comment);
	}

	public Comment getReviewById(int id) {
		return commentDAO.get(id);
	}

	@Transactional
	public List<ReviewComment> getReviewCommentsDueNotification() {
		return commentDAO.getReviewCommentsDueNotification();
	}
	
	@Transactional
	public List<InterviewComment> getInterviewCommentsDueNotification() {
		return commentDAO.getInterviewCommentsDueNotification();
	}
	
	@Transactional
	public List<Comment> getAllComments() {
		return commentDAO.getAllComments();
	}

	@Transactional
	public void declineReview(RegisteredUser reviewer, ApplicationForm application) {
		ReviewComment reviewComment = getNewReviewComment();
		reviewComment.setApplication(application);
		reviewComment.setUser(reviewer);
		reviewComment.setDecline(true);
		reviewComment.setType(CommentType.REVIEW);
		reviewComment.setReviewer(reviewer.getReviewersForApplicationForm(application).get(0));
		save(reviewComment);
	}

	public ReviewComment getNewReviewComment() {
		return new ReviewComment();
	}

}
