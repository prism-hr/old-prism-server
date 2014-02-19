package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;

@Service
@Transactional
public class CommentService {

    private final CommentDAO commentDAO;

    public CommentService() {
        this(null);
    }

    @Autowired
    public CommentService(CommentDAO commentDAO) {
        this.commentDAO = commentDAO;
    }

    public void save(Comment comment) {
        commentDAO.save(comment);
    }
    
    public List<Comment> getVisibleComments(RegisteredUser user, ApplicationForm applicationForm) {
        return commentDAO.getVisibleComments(user, applicationForm);
    }
    
    public void declineReview(RegisteredUser user, ApplicationForm application) {
        Reviewer currentReviewer = user.getReviewerForCurrentUserFromLatestReviewRound(application);
        if (!commentDAO.getReviewCommentsForReviewerAndApplication(currentReviewer, application).isEmpty()) {
            return;
        }

        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setApplication(application);
        reviewComment.setUser(user);
        reviewComment.setDeclined(true);
        reviewComment.setContent(StringUtils.EMPTY);
        
        save(reviewComment);
    }

    public List<CommentAssignedUser> getNotDecliningSupervisorsFromLatestApprovalStage(ApplicationForm application) {
        return commentDAO.getNotDecliningSupervisorsFromLatestApprovalStage(application);
    }

    public CommentAssignedUser assignUser(ApprovalComment approvalComment, RegisteredUser user, boolean isPrimary) {
        CommentAssignedUser assignedUser = new CommentAssignedUser();
        assignedUser.setUser(user);
        assignedUser.setPrimary(isPrimary);
        approvalComment.getAssignedUsers().add(assignedUser);
        return assignedUser;
    }

}