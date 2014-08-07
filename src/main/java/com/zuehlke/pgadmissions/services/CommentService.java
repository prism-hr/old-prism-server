package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.application.ApplicationAssignedSupervisorRepresentation;
import com.zuehlke.pgadmissions.rest.representation.application.UserAppointmentPreferencesRepresentation;

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
        for (CommentAppointmentTimeslot schedulingOption : commentDAO.getAppointmentTimeslots(schedulingComment)) {
            schedulingOptions.add(new AppointmentTimeslotRepresentation().withId(schedulingOption.getId()).withDateTime(schedulingOption.getDateTime()));
        }

        return schedulingOptions;
    }

    public List<UserAppointmentPreferencesRepresentation> getAppointmentPreferences(Application application) {
        Action schedulingAction = actionService.getById(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS);
        Comment schedulingComment = commentDAO.getLatestComment(application, schedulingAction);

        List<UserAppointmentPreferencesRepresentation> schedulingPreferences = Lists.newLinkedList();

        for (User invitee : commentDAO.getAppointmentInvitees(schedulingComment)) {
            UserExtendedRepresentation inviteeRepresentation = userService.getUserRepresentation(invitee);
            UserAppointmentPreferencesRepresentation preferenceRepresentation = new UserAppointmentPreferencesRepresentation().withUser(inviteeRepresentation);

            List<Boolean> inviteePreferences = Lists.newLinkedList();

            Comment preferenceComment = getLatestAppointmentPreferenceComment(application, invitee);
            List<CommentAppointmentTimeslot> inviteeResponses = commentDAO.getAppointmentPreferences(schedulingComment, preferenceComment);
            for (CommentAppointmentTimeslot timeslot : commentDAO.getAppointmentTimeslots(schedulingComment)) {
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

    public List<ApplicationAssignedSupervisorRepresentation> getApplicationSupervisors(Application application) {
        Comment assignmentComment = getLatestComment(application, PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);
        
        if (assignmentComment != null) {
            return Lists.newArrayList(buildApplicationSupervisorRepresentation(assignmentComment));
        } else {
            assignmentComment = getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS);
            Set<ApplicationAssignedSupervisorRepresentation> assignedSupervisors = buildApplicationSupervisorRepresentation(assignmentComment);
            
            List<String> declinedSupervisors = commentDAO.getDeclinedSupervisors(assignmentComment);
            
            for (ApplicationAssignedSupervisorRepresentation assignedSupervisor : assignedSupervisors) {
                if (declinedSupervisors.contains(assignedSupervisor.getUser().getEmail())) {
                    assignedSupervisors.remove(assignedSupervisor);
                }
            }
            
            return Lists.newArrayList(assignedSupervisors);
        }

    }

    private Set<ApplicationAssignedSupervisorRepresentation> buildApplicationSupervisorRepresentation(Comment assignmentComment) {
        Set<ApplicationAssignedSupervisorRepresentation> supervisors = Sets.newLinkedHashSet();
        
        for (CommentAssignedUser assignee : commentDAO.getAssignedSupervisors(assignmentComment)) {
            User user = assignee.getUser();
            UserRepresentation userRepresentation = new UserRepresentation().withFirstName(user.getFirstName()).withLastName(user.getLastName())
                    .withEmail(user.getEmail());
            ApplicationAssignedSupervisorRepresentation assignedSupervisorRepresentation = new ApplicationAssignedSupervisorRepresentation().withUser(
                    userRepresentation).withRole(assignee.getRole().getId()).withAcceptedSupervision(true);
            supervisors.add(assignedSupervisorRepresentation);
        }
        
        return supervisors;
    }
    
    private Comment getLatestAppointmentPreferenceComment(Application application, User user) {
        Comment preferenceComment = getLatestComment(application, PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY, user);
        return preferenceComment == null ? getLatestComment(application, PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY, user) : preferenceComment;
    }
    
}
