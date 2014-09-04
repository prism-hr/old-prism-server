package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.AppointmentTimeslotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAssignedSupervisorRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.OfferRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.UserAppointmentPreferencesRepresentation;

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
    private RoleService roleService;

    @Autowired
    private UserService userService;

    public Comment getById(int id) {
        return entityService.getById(Comment.class, id);
    }

    public Comment getLatestComment(Resource resource) {
        return commentDAO.getLatestComment(resource);
    }

    public Comment getLatestComment(Resource resource, PrismAction actionId) {
        return commentDAO.getLatestComment(resource, actionId);
    }

    public Comment getLatestComment(Resource resource, PrismAction actionId, User user, DateTime baseline) {
        return commentDAO.getLatestComment(resource, actionId, user, baseline);
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
        Comment schedulingComment = commentDAO.getLatestComment(application, PrismAction.APPLICATION_ASSIGN_INTERVIEWERS);

        if (schedulingComment != null) {
            List<AppointmentTimeslotRepresentation> schedulingOptions = Lists.newLinkedList();
            for (CommentAppointmentTimeslot schedulingOption : commentDAO.getAppointmentTimeslots(schedulingComment)) {
                schedulingOptions.add(new AppointmentTimeslotRepresentation().withId(schedulingOption.getId()).withDateTime(schedulingOption.getDateTime()));
            }

            return schedulingOptions;
        }

        return Lists.newArrayList();
    }

    public List<UserAppointmentPreferencesRepresentation> getAppointmentPreferences(Application application) {
        Comment schedulingComment = commentDAO.getLatestComment(application, PrismAction.APPLICATION_ASSIGN_INTERVIEWERS);

        if (schedulingComment != null) {
            List<UserAppointmentPreferencesRepresentation> schedulingPreferences = Lists.newLinkedList();

            for (User invitee : commentDAO.getAppointmentInvitees(schedulingComment)) {
                UserExtendedRepresentation inviteeRepresentation = userService.getUserRepresentation(invitee);
                UserAppointmentPreferencesRepresentation preferenceRepresentation = new UserAppointmentPreferencesRepresentation().withUser(inviteeRepresentation);

                List<Boolean> inviteePreferences = Lists.newLinkedList();

                Comment preferenceComment = getLatestAppointmentPreferenceComment(application, schedulingComment, invitee);
                List<CommentAppointmentTimeslot> inviteeResponses = commentDAO.getAppointmentPreferences(schedulingComment, preferenceComment);
                for (CommentAppointmentTimeslot timeslot : commentDAO.getAppointmentTimeslots(schedulingComment)) {
                    inviteePreferences.add(inviteeResponses.contains(timeslot));
                }

                preferenceRepresentation.withPreferences(inviteePreferences);
                schedulingPreferences.add(preferenceRepresentation);
            }

            return schedulingPreferences;
        }

        return Lists.newArrayList();
    }

    public List<ApplicationAssignedSupervisorRepresentation> getApplicationSupervisors(Application application) {
        Comment assignmentComment = getLatestComment(application, PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);

        if (assignmentComment != null) {
            return Lists.newArrayList(buildApplicationSupervisorRepresentation(assignmentComment));
        } else {
            assignmentComment = getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS);

            if (assignmentComment != null) {
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

        return Lists.newArrayList();
    }

    public OfferRepresentation getOfferRecommendation(Application application) {
        Comment sourceComment = getLatestComment(application, PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION);

        if (sourceComment != null) {
            return buildOfferRepresentation(sourceComment);
        } else {
            sourceComment = getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS);

            if (sourceComment != null) {
                OfferRepresentation offerRepresentation = buildOfferRepresentation(sourceComment);

                User primarySupervisor = commentDAO.getAssignedUsers(sourceComment, PrismRole.APPLICATION_PRIMARY_SUPERVISOR).get(0);
                sourceComment = getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS, primarySupervisor, sourceComment.getCreatedTimestamp());

                if (sourceComment != null) {
                    String positionTitle = sourceComment.getPositionTitle();
                    String positionDescription = sourceComment.getPositionDescription();
                    LocalDate positionProvisionalStartDate = sourceComment.getPositionProvisionalStartDate();
                    String appointmentConditions = sourceComment.getAppointmentConditions();

                    offerRepresentation.setPositionTitle(positionTitle == null ? positionTitle : positionTitle);
                    offerRepresentation.setPositionDescription(positionDescription == null ? positionDescription : positionDescription);
                    offerRepresentation.setPositionProvisionalStartDate(positionProvisionalStartDate == null ? positionProvisionalStartDate
                            : positionProvisionalStartDate);
                    offerRepresentation.setAppointmentConditions(appointmentConditions == null ? appointmentConditions : appointmentConditions);
                }

                return offerRepresentation;
            }
        }

        return new OfferRepresentation();
    }

    public void save(Comment comment) {
        Resource resource = comment.getResource();
        Action action = comment.getAction();

        if (action.getActionType() == PrismActionType.SYSTEM_INVOCATION) {
            comment.setRole(PrismRole.SYSTEM_ADMINISTRATOR.toString());
        } else {
            if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
                comment.setRole(roleService.getCreatorRole(resource).getId().toString());
            } else {
                comment.setRole(Joiner.on(", ").join(roleService.getActionOwnerRoles(comment.getUser(), resource, action)));
                if (comment.getDelegateUser() != null) {
                    comment.setDelegateRole(Joiner.on(", ").join(roleService.getActionOwnerRoles(comment.getDelegateUser(), resource, action)));
                }
            }
        }
        entityService.save(comment);
        entityService.flush();
    }

    public void updateComment(Integer commentId, CommentDTO commentDTO) {
        Comment comment = getById(commentId);
        actionService.validateUpdateAction(comment);

        comment.setDeclinedResponse(commentDTO.getDeclinedResponse());
        comment.setContent(commentDTO.getContent());
        comment.getDocuments().clear();

        for (FileDTO fileDTO : commentDTO.getDocuments()) {
            Document document = entityService.getById(Document.class, fileDTO.getId());
            comment.getDocuments().add(document);
        }
    }

    private Set<ApplicationAssignedSupervisorRepresentation> buildApplicationSupervisorRepresentation(Comment assignmentComment) {
        Set<ApplicationAssignedSupervisorRepresentation> supervisors = Sets.newLinkedHashSet();

        for (CommentAssignedUser assignee : commentDAO.getAssignedSupervisors(assignmentComment)) {
            User user = assignee.getUser();
            UserRepresentation userRepresentation = new UserRepresentation().withFirstName(user.getFirstName()).withLastName(user.getLastName())
                    .withEmail(user.getEmail());
            ApplicationAssignedSupervisorRepresentation assignedSupervisorRepresentation = new ApplicationAssignedSupervisorRepresentation()
                    .withUser(userRepresentation).withRole(assignee.getRole().getId()).withAcceptedSupervision(true);
            supervisors.add(assignedSupervisorRepresentation);
        }

        return supervisors;
    }

    public <T extends Resource> List<Comment> getTransitionComments(Class<T> resourceClass, Integer resourceId, DateTime rangeStart, DateTime rangeClose) {
        return commentDAO.getTransitionComments(resourceClass, resourceId, rangeStart, rangeClose);
    }

    private Comment getLatestAppointmentPreferenceComment(Application application, Comment schedulingComment, User user) {
        DateTime baseline = schedulingComment.getCreatedTimestamp();
        Comment preferenceComment = getLatestComment(application, PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY, user, baseline);
        return preferenceComment == null ? getLatestComment(application, PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY, user, baseline) : preferenceComment;
    }

    private OfferRepresentation buildOfferRepresentation(Comment sourceComment) {
        OfferRepresentation offerRepresentation = new OfferRepresentation().withPositionTitle(sourceComment.getPositionTitle())
                .withPositionDescription(sourceComment.getPositionDescription())
                .withPositionProvisionalStartDate(sourceComment.getPositionProvisionalStartDate())
                .withAppointmentConditions(sourceComment.getAppointmentConditions());
        return offerRepresentation;
    }

}
