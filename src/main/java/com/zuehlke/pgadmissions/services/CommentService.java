package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Role;
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
    private EntityDAO entityDAO;

    public Comment getById(int id) {
        return entityDAO.getById(Comment.class, id);
    }

    public void save(Comment comment) {
        entityDAO.save(comment);
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

    public List<User> getAssignedUsers(Comment comment, Role role, User invoker) {
        return commentDAO.getAssignedUsers(comment, role, invoker);
    }

    public List<Comment> getVisibleComments(Resource resource, User user) {
        List<Comment> comments = Lists.newArrayList();
        if (!actionService.getPermittedActions(resource, user).isEmpty()) {
            comments = commentDAO.getComments(resource);
        }
        return comments;
    }

    public CommentAssignedUser assignUser(Comment approvalComment, User user, boolean isPrimary) {
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
