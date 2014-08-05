package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private EntityService entityService;

    public Comment getById(int id) {
        return entityService.getById(Comment.class, id);
    }

    public void save(Comment comment) {
        List<CommentAssignedUser> assignedUsers = Lists.newArrayList(comment.getAssignedUsers());
        comment.getAssignedUsers().clear();
        
        entityService.save(comment);
        
        for (CommentAssignedUser assignedUser : assignedUsers) {
            assignedUser.setComment(comment);
            entityService.save(assignedUser);
            comment.getAssignedUsers().add(assignedUser);
        }
    }

    public Comment getLatestComment(Resource resource) {
        return commentDAO.getLatestComment(resource);
    }
    
    public Comment getLatestComment(Resource resource, Action action) {
        return commentDAO.getLatestComment(resource, action);
    }
    
    public Comment getLatestComment(Resource resource, PrismAction actionId) {
        Action action = actionService.getById(actionId);
        return getLatestComment(resource, action);
    }
    
    public List<Comment> getVisibleComments(Resource resource, User user) {
        List<Comment> comments = Lists.newArrayList();
        if (!actionService.getPermittedActions(resource, user).isEmpty()) {
            comments = commentDAO.getComments(resource);
        }
        return comments;
    }
    
    public List<Comment> getApplicationAssessmentComments(Application application) {
        return commentDAO.getApplicationAssessmentComments(application);
    }
    
    public List<DateTime> getAppointmentTimeslots(Application application) {
        Action schedulingAction = actionService.getById(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS);
        Comment schedulingComment = commentDAO.getLatestComment(application, schedulingAction);
        
        List<DateTime> schedulingOptions = Lists.newLinkedList();
        for (CommentAppointmentTimeslot schedulingOption : schedulingComment.getAppointmentTimeslots()) {
            schedulingOptions.add(schedulingOption.getId(), schedulingOption.getDateTime());
        }
        
        return schedulingOptions;
    }
    
    
}
