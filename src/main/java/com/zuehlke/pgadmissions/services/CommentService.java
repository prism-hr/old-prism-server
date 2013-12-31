package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Service
@Transactional
public class CommentService {

    private final CommentDAO commentDAO;
    private final ReviewerDAO reviewerDAO;

    public CommentService() {
        this(null, null);
    }

    @Autowired
    public CommentService(CommentDAO commentDAO, ReviewerDAO reviewerDAO) {
        this.commentDAO = commentDAO;
        this.reviewerDAO = reviewerDAO;
    }

    public void save(Comment comment) {
        commentDAO.save(comment);
    }

    public Comment getReviewById(int id) {
        return commentDAO.get(id);
    }

    public List<ReviewComment> getReviewCommentsDueNotification() {
        return commentDAO.getReviewCommentsDueNotification();
    }

    public List<InterviewComment> getInterviewCommentsDueNotification() {
        return commentDAO.getInterviewCommentsDueNotification();
    }

    public List<Comment> getAllComments() {
        return commentDAO.getAllComments();
    }

    public void declineReview(RegisteredUser user, ApplicationForm application) {
        Reviewer currentReviewer = reviewerDAO.getReviewerByUserAndReviewRound(user, application.getLatestReviewRound());
        if (!commentDAO.getReviewCommentsForReviewerAndApplication(currentReviewer, application).isEmpty()) {
            return;
        }

        ReviewComment reviewComment = getNewReviewComment();
        reviewComment.setApplication(application);
        reviewComment.setUser(user);
        reviewComment.setDecline(true);
        reviewComment.setType(CommentType.REVIEW);
        reviewComment.setComment(StringUtils.EMPTY);
        reviewComment.setReviewer(currentReviewer);
        
        save(reviewComment);
    }

    public Comment getNewGenericComment() {
        return new Comment();
    }

    public ReviewComment getNewReviewComment() {
        return new ReviewComment();
    }
    
}