package com.zuehlke.pgadmissions.services;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.representation.AppointmentTimeslotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.application.AppointmentPreferenceRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<AppointmentTimeslotRepresentation> getAppointmentTimeslots(Application application) {
        Action schedulingAction = actionService.getById(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS);
        Comment schedulingComment = commentDAO.getLatestComment(application, schedulingAction);

        List<AppointmentTimeslotRepresentation> schedulingOptions = Lists.newLinkedList();
        if (schedulingComment != null) {
            for (CommentAppointmentTimeslot schedulingOption : schedulingComment.getAppointmentTimeslots()) {
                schedulingOptions.add(new AppointmentTimeslotRepresentation().withId(schedulingOption.getId()).withDateTime(schedulingOption.getDateTime()));
            }
        }

        return schedulingOptions;
    }

    public List<AppointmentPreferenceRepresentation> getAppointmentPreferences(Application application) {
        Action schedulingAction = actionService.getById(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS);
        Comment schedulingComment = commentDAO.getLatestComment(application, schedulingAction);

        List<AppointmentPreferenceRepresentation> schedulingPreferences = Lists.newLinkedList();

        if (schedulingComment != null) {
            for (User invitee : commentDAO.getAppointmentInvitees(schedulingComment)) {
                UserRepresentation inviteeRepresentation = userService.getUserRepresentation(invitee);
                AppointmentPreferenceRepresentation preferenceRepresentation = new AppointmentPreferenceRepresentation().withUser(inviteeRepresentation);

                List<Boolean> inviteePreferences = Lists.newLinkedList();

                List<CommentAppointmentTimeslot> inviteeResponses = commentDAO.getAppointmentPreferences(schedulingComment, invitee);
                for (CommentAppointmentTimeslot timeslot : schedulingComment.getAppointmentTimeslots()) {
                    inviteePreferences.add(inviteeResponses.contains(timeslot));
                }

                preferenceRepresentation.withPreferences(inviteePreferences);
                schedulingPreferences.add(preferenceRepresentation);
            }
        }

        return schedulingPreferences;
    }
}
