package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private UserService userService;

    @Autowired
    private StateDAO stateDAO;

    @Autowired
    private SystemService systemService;

    @Autowired
    private EntityService entityService;

    public Comment getById(int id) {
        return entityService.getById(Comment.class, id);
    }

    public void save(Comment comment) {
        List<CommentAssignedUser> assignedUsers = Lists.newArrayList(comment.getCommentAssignedUsers());
        comment.getCommentAssignedUsers().clear();
        
        entityService.save(comment);
        
        for (CommentAssignedUser assignedUser : assignedUsers) {
            assignedUser.setComment(comment);
            entityService.save(assignedUser);
            comment.getCommentAssignedUsers().add(assignedUser);
        }
    }

    public Comment getLastComment(Resource resource) {
        return commentDAO.getLastComment(resource);
    }

    public <T extends Comment> T getLastCommentOfType(Resource resource, Class<T> clazz) {
        return getLastCommentOfType(resource, clazz);
    }

    public <T extends Comment> T getLastCommentOfType(Resource resource, Class<T> clazz, User author) {
        return commentDAO.getLastCommentOfType(resource, clazz, author);
    }

    public List<Comment> getVisibleComments(Resource resource, User user) {
        List<Comment> comments = Lists.newArrayList();
        if (!actionService.getPermittedActions(resource, user).isEmpty()) {
            comments = commentDAO.getComments(resource);
        }
        return comments;
    }

    public CommentAssignedUser assignUser(Comment approvalComment, User user) {
        CommentAssignedUser assignedUser = new CommentAssignedUser();
        assignedUser.setUser(user);
        approvalComment.getCommentAssignedUsers().add(assignedUser);
        return assignedUser;
    }

    public List<CommentAssignedUser> getNotDecliningSupervisorsFromLatestApprovalStage(Application application) {
        // TODO implement
        return null;
    }

}
