package com.zuehlke.pgadmissions.services;

import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.application.UserAppointmentPreferencesRepresentation;
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
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.representation.AppointmentTimeslotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

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

    public Comment getLatestComment(Resource resource, Action action, User user) {
        return commentDAO.getLatestComment(resource, action, user);
    }

    public Comment getLatestComment(Resource resource, PrismAction actionId) {
        Action action = actionService.getById(actionId);
        return getLatestComment(resource, action);
    }

    public Comment getLatestComment(Resource resource, PrismAction actionId, User user) {
        Action action = actionService.getById(actionId);
        return commentDAO.getLatestComment(resource, action, user);
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

    public List<AppointmentTimeslotRepresentation> getAppointmentTimeslots(Application application) {
        Action schedulingAction = actionService.getById(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS);
        Comment schedulingComment = commentDAO.getLatestComment(application, schedulingAction);

        List<AppointmentTimeslotRepresentation> schedulingOptions = Lists.newLinkedList();
        for (CommentAppointmentTimeslot schedulingOption : schedulingComment.getAppointmentTimeslots()) {
            schedulingOptions.add(new AppointmentTimeslotRepresentation().withId(schedulingOption.getId()).withDateTime(schedulingOption.getDateTime()));
        }

        return schedulingOptions;
    }

    public List<UserAppointmentPreferencesRepresentation> getAppointmentPreferences(Application application) {
        Action schedulingAction = actionService.getById(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS);
        Comment schedulingComment = commentDAO.getLatestComment(application, schedulingAction);

        List<UserAppointmentPreferencesRepresentation> schedulingPreferences = Lists.newLinkedList();

        for (User invitee : commentDAO.getAppointmentInvitees(schedulingComment)) {
            UserRepresentation inviteeRepresentation = userService.getUserRepresentation(invitee);
            UserAppointmentPreferencesRepresentation preferenceRepresentation = new UserAppointmentPreferencesRepresentation().withUser(inviteeRepresentation);

            List<Boolean> inviteePreferences = Lists.newLinkedList();

            Comment preferenceComment = getLatestAppointmentPreferenceComment(application, invitee);
            List<CommentAppointmentTimeslot> inviteeResponses = commentDAO.getAppointmentPreferences(schedulingComment, preferenceComment);
            for (CommentAppointmentTimeslot timeslot : schedulingComment.getAppointmentTimeslots()) {
                inviteePreferences.add(inviteeResponses.contains(timeslot));
            }

            preferenceRepresentation.withPreferences(inviteePreferences);
            schedulingPreferences.add(preferenceRepresentation);
        }

        return schedulingPreferences;
    }

    public void addAssignedUser(Comment comment, UserRole userRole) {
        CommentAssignedUser transientAssignedUser = new CommentAssignedUser().withComment(comment).withUser(userRole.getUser()).withRole(userRole.getRole());
        CommentAssignedUser persistentAssignedUser = entityService.getDuplicateEntity(transientAssignedUser);
        if (persistentAssignedUser == null) {
            entityService.save(transientAssignedUser);
            comment.getAssignedUsers().add(transientAssignedUser);
        }
    }

    private Comment getLatestAppointmentPreferenceComment(Application application, User user) {
        Comment preferenceComment = getLatestComment(application, PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY, user);
        return preferenceComment == null ? getLatestComment(application, PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY, user) : preferenceComment;
    }

}
