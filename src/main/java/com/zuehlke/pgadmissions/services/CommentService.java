package com.zuehlke.pgadmissions.services;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.*;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.*;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.AppointmentTimeslotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAssignedSupervisorRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.OfferRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.UserAppointmentPreferencesRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private ApplicationContext applicationContext;

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

    public <T extends Resource> Comment getEarliestComment(ResourceParent parentResource, Class<T> resourceClass, PrismAction actionId) {
        return commentDAO.getEarliestComment(parentResource, resourceClass, actionId);
    }

    public List<Comment> getVisibleComments(Resource resource, User user) {
        List<Comment> comments = Lists.newLinkedList();
        if (!actionService.getPermittedActions(resource, user).isEmpty()) {
            LinkedList<Comment> transitionComments = Lists.newLinkedList(commentDAO.getStateGroupTransitionComments(resource));
            transitionComments.add(0, null); // add null as first element
            for (int i = 1; i < transitionComments.size(); i++) {
                comments.addAll(commentDAO.getStateComments(resource, transitionComments.get(i), transitionComments.get(i - 1)));
                comments.add(transitionComments.get(i));
            }
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
                UserRepresentation inviteeRepresentation = userService.getUserRepresentation(invitee);
                UserAppointmentPreferencesRepresentation preferenceRepresentation = new UserAppointmentPreferencesRepresentation()
                        .withUser(inviteeRepresentation);

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
                sourceComment = getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS, primarySupervisor,
                        sourceComment.getCreatedTimestamp());

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

    public Comment getRejectionComment(Application application) {
        Comment comment = getLatestComment(application, PrismAction.APPLICATION_CONFIRM_REJECTION);
        return comment == null ? getLatestComment(application, PrismAction.APPLICATION_TERMINATE) : comment;
    }

    public void create(Comment comment) {
        Resource resource = comment.getResource();
        Action action = comment.getAction();

        setCommentAuthorRoles(comment, resource, action);

        Set<CommentAssignedUser> transientAssignees = comment.getAssignedUsers();
        Set<CommentAssignedUser> persistentAssignees = Sets.newHashSet(transientAssignees);
        transientAssignees.clear();

        Set<CommentAppointmentTimeslot> transientTimeslots = comment.getAppointmentTimeslots();
        Set<CommentAppointmentTimeslot> persistentTimeslots = Sets.newHashSet(transientTimeslots);
        transientTimeslots.clear();

        Set<CommentAppointmentPreference> transientPreferences = comment.getAppointmentPreferences();
        Set<CommentAppointmentPreference> persistentPreferences = Sets.newHashSet(transientPreferences);
        transientPreferences.clear();

        entityService.save(comment);

        addAssignedUsers(comment, persistentAssignees);
        comment.getAppointmentTimeslots().addAll(persistentTimeslots);
        comment.getAppointmentPreferences().addAll(persistentPreferences);
    }

    public void update(Integer commentId, CommentDTO commentDTO) {
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

    public <T extends Resource> List<Comment> getTransitionComments(Class<T> resourceClass, Integer resourceId, DateTime rangeStart, DateTime rangeClose) {
        return commentDAO.getStateTransitionComments(resourceClass, resourceId, rangeStart, rangeClose);
    }

    public void recordStateTransition(Comment comment, State state, State transitionState) {
        comment.setState(state);
        comment.setTransitionState(transitionState);
    }

    public void delete(Application application, Comment exclusion) {
        for (Comment comment : application.getComments()) {
            if (comment != exclusion) {
                entityService.delete(comment);
            }
        }
    }

    public void processComment(Comment comment) {
        if (comment.isApplicationAutomatedRejectionComment()) {
            PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).withResource(comment.getApplication());
            comment.setRejectionReasonSystem(propertyLoader.load(PrismDisplayProperty.APPLICATION_COMMENT_REJECTION_SYSTEM));
        }

    }

    private Comment getLatestAppointmentPreferenceComment(Application application, Comment schedulingComment, User user) {
        DateTime baseline = schedulingComment.getCreatedTimestamp();
        Comment preferenceComment = getLatestComment(application, PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY, user, baseline);
        return preferenceComment == null ? getLatestComment(application, PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY, user, baseline)
                : preferenceComment;
    }

    private OfferRepresentation buildOfferRepresentation(Comment sourceComment) {
        return new OfferRepresentation().withPositionTitle(sourceComment.getPositionTitle()).withPositionDescription(sourceComment.getPositionDescription())
                .withPositionProvisionalStartDate(sourceComment.getPositionProvisionalStartDate())
                .withAppointmentConditions(sourceComment.getAppointmentConditions());
    }

    private void setCommentAuthorRoles(Comment comment, Resource resource, Action action) {
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

    private void addAssignedUsers(Comment comment, Set<CommentAssignedUser> assignees) {
        for (CommentAssignedUser assignee : assignees) {
            PrismRoleTransitionType transitionType = assignee.getRoleTransitionType();
            comment.addAssignedUser(assignee.getUser(), assignee.getRole(), transitionType == null ? PrismRoleTransitionType.CREATE : transitionType);
        }
    }

}
