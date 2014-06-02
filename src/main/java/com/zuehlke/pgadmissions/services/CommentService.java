package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.User;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ManageUsersService manageUsersService;

    @Autowired
    private StateDAO stateDAO;
    
    @Autowired
    private SystemService systemService;
    
    @Autowired
    private EntityDAO entityDAO;
    
    public Comment getById(int id) {
        return entityDAO.getById(Comment.class, id);
    }
    
    public void save(Comment comment) {
        entityDAO.save(comment);
    }
    
    public Comment getLastComment(PrismResourceDynamic resource) {
        return commentDAO.getLastComment(resource);
    }
    
    public <T extends Comment> T getLastCommentOfType(PrismResourceDynamic resource, Class<T> clazz) {
        return getLastCommentOfType(resource, clazz);
    }
    
    public <T extends Comment> T getLastCommentOfType(PrismResourceDynamic resource, Class<T> clazz, User author) {
        return commentDAO.getLastCommentOfType(resource, clazz, author);
    }
    
    public List<User> getAssignedUsers(Comment comment, Role role, User invoker) {
        return commentDAO.getAssignedUsers(comment, role, invoker);
    }
    
    // TODO: rewrite below

    public List<Comment> getVisibleComments(User user, Application applicationForm) {
        return commentDAO.getVisibleComments(user, applicationForm);
    }

    public void declineReview(User user, Application application) {
        // check if user has already responded
        // if (!commentDAO.getReviewCommentsForReviewerAndApplication(user, application).isEmpty()) {
        // return;
        // }

        Comment reviewComment = new Comment();
        reviewComment.setApplication(application);
        reviewComment.setUser(user);
        reviewComment.setDeclinedResponse(true);
        reviewComment.setContent(StringUtils.EMPTY);

        save(reviewComment);
    }

    public List<CommentAssignedUser> getNotDecliningSupervisorsFromLatestApprovalStage(Application application) {
        return commentDAO.getNotDecliningSupervisorsFromLatestApprovalStage(application);
    }

    public CommentAssignedUser assignUser(Comment approvalComment, User user, boolean isPrimary) {
        CommentAssignedUser assignedUser = new CommentAssignedUser();
        assignedUser.setUser(user);
        approvalComment.getCommentAssignedUsers().add(assignedUser);
        return assignedUser;
    }

}
