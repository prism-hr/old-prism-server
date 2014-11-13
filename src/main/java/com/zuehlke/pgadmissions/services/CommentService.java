package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_ASSESSMENT_CONTENT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentPreference;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentCustomResponse;
import com.zuehlke.pgadmissions.domain.comment.CommentTransitionState;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.representation.TimelineRepresentation;
import com.zuehlke.pgadmissions.rest.representation.TimelineRepresentation.TimelineCommentGroupRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.AppointmentTimeslotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAssignedSupervisorRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.OfferRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.UserAppointmentPreferencesRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

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
    private Mapper dozerBeanMapper;

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

    public TimelineRepresentation getComments(Resource resource, User user) {
        TimelineRepresentation timeline = new TimelineRepresentation();
        List<Comment> transitionComments = commentDAO.getStateGroupTransitionComments(resource);

        int transitionCommentCount = transitionComments.size();
        if (transitionCommentCount > 0) {
            PrismStateGroup stateGroupId = null;
            List<Comment> previousStateComments = Lists.newArrayList();

            HashMultimap<PrismAction, PrismActionRedactionType> redactions = actionService.getRedactions(resource, user);

            for (int i = 0; i < transitionCommentCount; i++) {
                Comment start = transitionComments.get(i);
                Comment close = i == (transitionCommentCount - 1) ? null : transitionComments.get(i + 1);

                stateGroupId = stateGroupId == null ? start.getState().getStateGroup().getId() : stateGroupId;
                List<Comment> stateComments = commentDAO.getStateComments(resource, start, close, stateGroupId, previousStateComments);

                TimelineCommentGroupRepresentation commentGroup = new TimelineCommentGroupRepresentation().withStateGroup(stateGroupId);

                for (Comment comment : stateComments) {
                    CommentRepresentation representation = getCommentRepresentation(user, comment, redactions.get(comment.getAction().getId()));
                    commentGroup.addComment(representation);
                }

                timeline.addCommentGroup(commentGroup);

                stateGroupId = close == null ? null : close.getTransitionState().getStateGroup().getId();
                previousStateComments = stateComments;
            }
        }

        return timeline;
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
                List<LocalDateTime> inviteeResponses = commentDAO.getAppointmentPreferences(preferenceComment);
                for (CommentAppointmentTimeslot timeslot : commentDAO.getAppointmentTimeslots(schedulingComment)) {
                    inviteePreferences.add(inviteeResponses.contains(timeslot.getDateTime()));
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

        Set<CommentTransitionState> transientTransitionStates = comment.getTransitionStates();
        Set<CommentTransitionState> persistentTransitionStates = Sets.newHashSet(transientTransitionStates);
        transientTransitionStates.clear();

        Set<CommentAppointmentTimeslot> transientTimeslots = comment.getAppointmentTimeslots();
        Set<CommentAppointmentTimeslot> persistentTimeslots = Sets.newHashSet(transientTimeslots);
        transientTimeslots.clear();

        Set<CommentAppointmentPreference> transientPreferences = comment.getAppointmentPreferences();
        Set<CommentAppointmentPreference> persistentPreferences = Sets.newHashSet(transientPreferences);
        transientPreferences.clear();

        Set<CommentCustomResponse> transientProperties = comment.getPropertyAnswers();
        Set<CommentCustomResponse> persistentProperties = Sets.newHashSet(transientProperties);
        transientProperties.clear();

        entityService.save(comment);

        addAssignedUsers(comment, persistentAssignees);
        comment.getTransitionStates().addAll(persistentTransitionStates);
        comment.getAppointmentTimeslots().addAll(persistentTimeslots);
        comment.getAppointmentPreferences().addAll(persistentPreferences);
        addPropertyAnswers(comment, persistentProperties);
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

    public <T extends Resource> List<Comment> getRecentComments(Class<T> resourceClass, Integer resourceId, DateTime rangeStart, DateTime rangeClose) {
        return commentDAO.getRecentComments(resourceClass, resourceId, rangeStart, rangeClose);
    }

    public void recordStateTransition(Comment comment, State state, State transitionState) {
        comment.setState(state);
        comment.setTransitionState(transitionState);
        comment.addTransitionState(transitionState, true);
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
            PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize(comment.getApplication(), comment.getUser());
            comment.setRejectionReasonSystem(propertyLoader.load(PrismDisplayProperty.APPLICATION_COMMENT_REJECTION_SYSTEM));
        }

    }

    public void postProcessComment(Comment comment) {
        if (comment.isApplicationRatingComment() && comment.getApplicationRating() == null) {
            buildAggregatedRating(comment);
        }

        if (comment.isInterviewScheduledExpeditedComment()) {
            appendInterviewPreferenceComments(comment);
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

    private CommentRepresentation getCommentRepresentation(User user, Comment comment, Set<PrismActionRedactionType> redactions) {
        Action action = comment.getAction();
        Integer userId = user.getId();

        User author = comment.getUser();
        User authorDelegate = comment.getDelegateUser();

        CommentRepresentation representation;
        if (redactions.isEmpty() || userId.equals(author.getId()) || (authorDelegate != null && userId.equals(authorDelegate.getId()))) {
            representation = dozerBeanMapper.map(comment, CommentRepresentation.class);
        } else {

            UserRepresentation authorRepresentation = new UserRepresentation().withFirstName(author.getFirstName()).withLastName(author.getLastName())
                    .withEmail(author.getEmail());
            UserRepresentation authorDelegateRepresenation = authorDelegate == null ? null : new UserRepresentation()
                    .withFirstName(authorDelegate.getFirstName()).withLastName(authorDelegate.getLastName()).withEmail(authorDelegate.getEmail());

            representation = new CommentRepresentation().addId(comment.getId()).addUser(authorRepresentation).addDelegateUser(authorDelegateRepresenation)
                    .addAction(comment.getAction().getId()).addDeclinedResponse(comment.getDeclinedResponse())
                    .addCreatedTimestamp(comment.getCreatedTimestamp());

            if (redactions.contains(ALL_ASSESSMENT_CONTENT)) {
                representation.addInterviewTimeZone(comment.getInterviewTimeZone()).addInterviewDateTime(comment.getInterviewDateTime())
                        .addInterviewDuration(comment.getInterviewDuration()).addIntervieweeInstructions(comment.getIntervieweeInstructions())
                        .addInterviewLocation(comment.getInterviewLocation());

                for (CommentAppointmentTimeslot timeslot : comment.getAppointmentTimeslots()) {
                    representation
                            .addAppointmentTimeslot(new AppointmentTimeslotRepresentation().withId(timeslot.getId()).withDateTime(timeslot.getDateTime()));
                }
            }
        }

        representation.setEmphasizedAction(action.getEmphasizedAction());
        return representation;
    }

    private void buildAggregatedRating(Comment comment) {
        BigDecimal aggregatedRating = new BigDecimal(0.00);
        for (CommentCustomResponse property : comment.getPropertyAnswers()) {
            switch (property.getPropertyType()) {
            case RATING_NORMAL:
                aggregatedRating = aggregatedRating.add(getWeightedRatingComponent(property, 5));
                break;
            case RATING_WEIGHTED:
                aggregatedRating = aggregatedRating.add(getWeightedRatingComponent(property, 8));
                break;
            default:
                continue;
            }
        }
        comment.setApplicationRating(aggregatedRating);
    }

    private BigDecimal getWeightedRatingComponent(CommentCustomResponse property, Integer denominator) {
        return new BigDecimal(property.getPropertyValue()).divide(new BigDecimal(denominator)).multiply(new BigDecimal(5))
                .multiply(property.getPropertyWeight()).setScale(2, RoundingMode.HALF_UP);
    }

    private void appendInterviewPreferenceComments(Comment comment) {
        LocalDateTime interviewDateTime = comment.getInterviewDateTime();
        comment.getAppointmentTimeslots().add(new CommentAppointmentTimeslot().withDateTime(interviewDateTime));

        Resource resource = comment.getResource();
        Action action = actionService.getById(PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY);

        DateTime baseline = new DateTime();
        for (CommentAssignedUser assignee : comment.getAssignedUsers()) {
            PrismRole roleId = assignee.getRole().getId();
            if (Arrays.asList(PrismRole.APPLICATION_INTERVIEWEE, PrismRole.APPLICATION_INTERVIEWER).contains(roleId)) {
                Comment preference = new Comment().withResource(resource).withAction(action).withUser(assignee.getUser()).withRole(roleId.name())
                        .withDeclinedResponse(false).withState(resource.getState()).withTransitionState(resource.getState()).withCreatedTimestamp(baseline);
                preference.getAppointmentPreferences().add(new CommentAppointmentPreference().withDateTime(interviewDateTime));
                create(preference);
                resource.addComment(preference);
            }
        }
    }

    private void addPropertyAnswers(Comment comment, Set<CommentCustomResponse> persistentProperties) {
        comment.getPropertyAnswers().addAll(persistentProperties);
    }

}
