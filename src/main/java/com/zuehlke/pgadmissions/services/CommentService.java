package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_REJECTION_SYSTEM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.EXHUME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup.APPLICATION_REFERENCE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationInterviewAppointment;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationInterviewInstruction;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationPositionDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentPreference;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentCustomResponse;
import com.zuehlke.pgadmissions.domain.comment.CommentTransitionState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.document.PrismFileCategory;
import com.zuehlke.pgadmissions.domain.imported.RejectionReason;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.rest.dto.AssignedUserDTO;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentApplicationInterviewAppointmentDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentApplicationInterviewInstructionDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentAssignedUserDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentCustomResponseDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.TimelineRepresentation;
import com.zuehlke.pgadmissions.rest.representation.TimelineRepresentation.TimelineCommentGroupRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.AppointmentTimeslotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentApplicationInterviewAppointmentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentApplicationInterviewInstructionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentAssignedUserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAssignedSupervisorRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.InterviewRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.OfferRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.UserAppointmentPreferencesRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.CommentValidator;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismConstants;

@Service
@Transactional
public class CommentService {

    @Inject
    private CommentDAO commentDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private EntityService entityService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Inject
    private DocumentService documentService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private Mapper mapper;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private CommentValidator commentValidator;

    public Comment getById(int id) {
        return entityService.getById(Comment.class, id);
    }

    public Comment getLatestComment(Resource resource) {
        return commentDAO.getLatestComment(resource);
    }

    public Comment getLatestComment(Resource resource, PrismAction prismAction) {
        return commentDAO.getLatestComment(resource, prismAction);
    }

    public Comment getLatestComment(Resource resource, User user, PrismAction... prismActions) {
        return commentDAO.getLatestComment(resource, user, prismActions);
    }

    public Comment getLatestComment(Resource resource, PrismAction actionId, User user, DateTime baseline) {
        return commentDAO.getLatestComment(resource, actionId, user, baseline);
    }

    public <T extends Resource> Comment getEarliestComment(ResourceParent parentResource, Class<T> resourceClass, PrismAction actionId) {
        return commentDAO.getEarliestComment(parentResource, resourceClass, actionId);
    }

    public boolean isCommentOwner(Comment comment, User user) {
        Integer userId = user.getId();
        User ownerDelegate = comment.getDelegateUser();
        return (comment.getUser().getId() == userId || (ownerDelegate != null && ownerDelegate.getId() == userId));
    }

    public TimelineRepresentation getComments(Resource resource, User user) {
        TimelineRepresentation timeline = new TimelineRepresentation();
        List<Comment> transitionComments = commentDAO.getStateGroupTransitionComments(resource);

        int transitionCommentCount = transitionComments.size();
        if (transitionCommentCount > 0) {
            PrismStateGroup stateGroupId = null;
            List<Comment> previousStateComments = Lists.newArrayList();

            List<PrismRole> rolesOverridingRedactions = roleService.getRolesOverridingRedactions(resource, user);
            List<PrismRole> creatableRoles = roleService.getCreatableRoles(resource.getResourceScope());
            HashMultimap<PrismAction, PrismActionRedactionType> redactions = actionService.getRedactions(resource, user);

            for (int i = 0; i < transitionCommentCount; i++) {
                Comment start = transitionComments.get(i);
                Comment close = i == (transitionCommentCount - 1) ? null : transitionComments.get(i + 1);

                stateGroupId = stateGroupId == null ? start.getState().getStateGroup().getId() : stateGroupId;
                List<Comment> stateComments = commentDAO.getStateComments(resource, start, close, stateGroupId, previousStateComments);

                List<Integer> batchedViewEditCommentIds = null;
                CommentRepresentation lastViewEditComment = null;
                TimelineCommentGroupRepresentation commentGroup = new TimelineCommentGroupRepresentation().withStateGroup(stateGroupId);

                for (Comment comment : stateComments) {
                    Set<PrismActionRedactionType> commentRedactions = redactions.get(comment.getAction().getId());
                    if (comment.isViewEditComment()) {
                        if (lastViewEditComment == null || lastViewEditComment.getCreatedTimestamp().plusHours(1).isBefore(comment.getCreatedTimestamp())) {
                            CommentRepresentation representation = getCommentRepresentation(user, comment, rolesOverridingRedactions, commentRedactions,
                                    creatableRoles);
                            commentGroup.addComment(representation);
                            batchedViewEditCommentIds = Lists.newArrayList(comment.getId());
                            lastViewEditComment = representation;
                        } else {
                            String contentNew = comment.getContent();
                            String contentExisting = lastViewEditComment.getContent();
                            if (contentExisting == null) {
                                lastViewEditComment.setContent(contentNew);
                            } else if (!contentExisting.contains(contentNew)) {
                                contentExisting = contentExisting + "<br/>" + contentNew;
                                lastViewEditComment.setContent(contentExisting);
                            }
                            batchedViewEditCommentIds.add(comment.getId());
                            lastViewEditComment.setAssignedUsers(getAssignedUsers(batchedViewEditCommentIds, creatableRoles));
                        }
                    } else {
                        CommentRepresentation representation = getCommentRepresentation(user, comment, rolesOverridingRedactions, commentRedactions,
                                creatableRoles);
                        commentGroup.addComment(representation);
                    }
                }

                timeline.addCommentGroup(commentGroup);
                stateGroupId = close == null ? null : close.getTransitionState().getStateGroup().getId();
                previousStateComments = stateComments;
            }
        }

        return timeline;
    }

    public InterviewRepresentation getInterview(Application application) {
        Comment schedulingComment = commentDAO.getLatestComment(application, PrismAction.APPLICATION_ASSIGN_INTERVIEWERS);
        if (schedulingComment == null) {
            return null;
        }
        InterviewRepresentation interview = new InterviewRepresentation();

        interview.setAppointmentTimeslots(Lists.<AppointmentTimeslotRepresentation> newLinkedList());
        for (CommentAppointmentTimeslot schedulingOption : commentDAO.getAppointmentTimeslots(schedulingComment)) {
            interview.getAppointmentTimeslots().add(
                    new AppointmentTimeslotRepresentation().withId(schedulingOption.getId()).withDateTime(schedulingOption.getDateTime()));
        }

        interview.setAppointmentPreferences(Lists.<UserAppointmentPreferencesRepresentation> newLinkedList());
        for (User invitee : commentDAO.getAppointmentInvitees(schedulingComment)) {
            UserRepresentation inviteeRepresentation = userService.getUserRepresentation(invitee);
            UserAppointmentPreferencesRepresentation preferenceRepresentation = new UserAppointmentPreferencesRepresentation().withUser(inviteeRepresentation);

            List<Integer> inviteePreferences = Lists.newLinkedList();

            Comment preferenceComment = getLatestAppointmentPreferenceComment(application, schedulingComment, invitee);
            if (preferenceComment != null) {
                List<LocalDateTime> inviteeResponses = commentDAO.getAppointmentPreferences(preferenceComment);
                for (CommentAppointmentTimeslot timeslot : commentDAO.getAppointmentTimeslots(schedulingComment)) {
                    if (inviteeResponses.contains(timeslot.getDateTime())) {
                        inviteePreferences.add(timeslot.getId());
                    }
                }
                preferenceRepresentation.setPreferences(inviteePreferences);
            }

            interview.getAppointmentPreferences().add(preferenceRepresentation);
        }

        CommentApplicationInterviewAppointment interviewAppointment = schedulingComment.getInterviewAppointment();
        if (interviewAppointment != null) {
            mapper.map(interviewAppointment, interview);
        }

        CommentApplicationInterviewInstruction interviewInstruction = schedulingComment.getInterviewInstruction();
        if (interviewInstruction != null) {
            mapper.map(interviewInstruction, interview);
        }

        return interview;
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
        }

        sourceComment = getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS);
        if (sourceComment != null) {
            OfferRepresentation offerRepresentation = buildOfferRepresentation(sourceComment);

            User primarySupervisor = Iterables.getFirst(commentDAO.getAssignedUsers(sourceComment, PrismRole.APPLICATION_PRIMARY_SUPERVISOR), null);
            if (primarySupervisor != null) {
                sourceComment = getLatestComment(application, PrismAction.APPLICATION_CONFIRM_PRIMARY_SUPERVISION, primarySupervisor,
                        sourceComment.getCreatedTimestamp());
            }

            if (sourceComment != null) {
                String positionTitle = null;
                String positionDescription = null;

                CommentApplicationPositionDetail positionDetail = sourceComment.getPositionDetail();
                if (positionDetail != null) {
                    positionTitle = positionDetail.getPositionTitle();
                    positionDescription = positionDetail.getPositionDescription();
                }

                LocalDate positionProvisionalStartDate = null;
                String appointmentConditions = null;

                CommentApplicationOfferDetail offerDetail = sourceComment.getOfferDetail();
                if (offerDetail != null) {
                    positionProvisionalStartDate = offerDetail.getPositionProvisionalStartDate();
                    appointmentConditions = offerDetail.getAppointmentConditions();
                }

                offerRepresentation.setPositionTitle(positionTitle);
                offerRepresentation.setPositionDescription(positionDescription);
                offerRepresentation.setPositionProvisionalStartDate(positionProvisionalStartDate);
                offerRepresentation.setAppointmentConditions(appointmentConditions);
            }

            return offerRepresentation;
        }

        return new OfferRepresentation();
    }

    public void persistComment(Resource resource, Comment comment) {
        Set<CommentAssignedUser> transientAssignees = comment.getAssignedUsers();
        Set<CommentAssignedUser> persistentAssignees = Sets.newHashSet(transientAssignees);
        transientAssignees.clear();

        Set<CommentTransitionState> transientTransitionStates = comment.getCommentTransitionStates();
        Set<CommentTransitionState> persistentTransitionStates = Sets.newHashSet(transientTransitionStates);
        transientTransitionStates.clear();

        Set<CommentAppointmentTimeslot> transientTimeslots = comment.getAppointmentTimeslots();
        Set<CommentAppointmentTimeslot> persistentTimeslots = Sets.newHashSet(transientTimeslots);
        transientTimeslots.clear();

        Set<CommentAppointmentPreference> transientPreferences = comment.getAppointmentPreferences();
        Set<CommentAppointmentPreference> persistentPreferences = Sets.newHashSet(transientPreferences);
        transientPreferences.clear();

        Set<CommentCustomResponse> transientResponses = comment.getCustomResponses();
        Set<CommentCustomResponse> persistentResponses = Sets.newHashSet(transientResponses);
        transientResponses.clear();

        entityService.save(comment);

        assignUsers(comment, persistentAssignees);
        comment.getCommentTransitionStates().addAll(persistentTransitionStates);
        comment.getAppointmentTimeslots().addAll(persistentTimeslots);
        comment.getAppointmentPreferences().addAll(persistentPreferences);
        comment.getCustomResponses().addAll(persistentResponses);
        resource.addComment(comment);

        validateComment(comment);
        entityService.flush();
    }

    public void update(Integer commentId, CommentDTO commentDTO) {
        Comment comment = getById(commentId);
        actionService.validateUpdateAction(comment);

        comment.setDeclinedResponse(commentDTO.getDeclinedResponse());
        comment.setContent(commentDTO.getContent());
        comment.getDocuments().clear();

        for (FileDTO fileDTO : commentDTO.getDocuments()) {
            Document document = documentService.getById(fileDTO.getId(), PrismFileCategory.DOCUMENT);
            comment.getDocuments().add(document);
        }
    }

    public List<Comment> getRecentComments(PrismScope resourceScope, Integer resourceId, DateTime rangeStart, DateTime rangeClose) {
        return commentDAO.getRecentComments(resourceScope, resourceId, rangeStart, rangeClose);
    }

    public void recordStateTransition(Comment comment, State state, State transitionState, Set<State> stateTerminations) {
        comment.setState(state);
        comment.setTransitionState(transitionState);

        comment.addCommentState(state, true);
        comment.addCommentTransitionState(transitionState, true);

        updateCommentStates(comment);

        if (comment.isSecondaryStateGroupTransitionComment() || comment.isStateGroupTransitionComment()) {
            createCommentTransitionStates(comment, transitionState, stateTerminations);
        } else {
            updateCommentTransitionStates(comment, stateTerminations);
        }

        entityService.flush();
    }

    public void delete(Application application, Comment exclusion) {
        for (Comment comment : application.getComments()) {
            if (comment != exclusion) {
                entityService.delete(comment);
            }
        }
    }

    public void preProcessComment(Resource resource, Comment comment) throws Exception {
        if (comment.isApplicationAssignRefereesComment()) {
            appendApplicationReferees(resource, comment);
        }

        if (comment.isInterviewScheduledConfirmedComment()) {
            appendInterviewScheduledConfirmedComments(comment);
        }

        if (comment.isApplicationReverseRejectionComment()) {
            exhumeApplicationReferees(resource, comment);
        }
    }

    public void processComment(Resource resource, Comment comment) throws Exception {
        if (comment.isApplicationAutomatedRejectionComment()) {
            PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize((Application) resource);
            comment.setRejectionReasonSystem(propertyLoader.load(APPLICATION_COMMENT_REJECTION_SYSTEM));
        }
        entityService.flush();
    }

    public void postProcessComment(Resource resource, Comment comment) throws Exception {
        if (comment.isApplicationViewEditComment() && !resourceService.getResourceStatesByStateGroup(resource, APPLICATION_REFERENCE).isEmpty()) {
            appendApplicationReferees(resource, comment);
        }

        if (comment.isApplicationRatingComment() && comment.getApplicationRating() == null) {
            buildAggregatedRating(comment);
            if (comment.getApplicationRating() == null) {
                comment.setApplicationRating(new BigDecimal(PrismConstants.DEFAULT_RATING));
            }
        }

        if (comment.isInterviewScheduledExpeditedComment()) {
            appendInterviewScheduledExpeditedComments(comment);
        }
    }

    public void appendAssignedUsers(Comment comment, CommentDTO commentDTO) throws DeduplicationException {
        if (commentDTO.getAssignedUsers() != null) {
            for (CommentAssignedUserDTO assignedUserDTO : commentDTO.getAssignedUsers()) {
                AssignedUserDTO commentUserDTO = assignedUserDTO.getUser();
                User commentUser = userService.getOrCreateUser(commentUserDTO.getFirstName(), commentUserDTO.getLastName(), commentUserDTO.getEmail());
                comment.getAssignedUsers().add(
                        new CommentAssignedUser().withUser(commentUser).withRole(entityService.getById(Role.class, assignedUserDTO.getRole())));
            }
        }
    }

    public void appendTransitionStates(Comment comment, CommentDTO commentDTO) {
        comment.setTransitionState(stateService.getById(commentDTO.getTransitionState()));
        List<PrismState> secondaryTransitionStates = commentDTO.getSecondaryTransitionStates();
        if (secondaryTransitionStates != null) {
            for (PrismState secondaryTransitionState : secondaryTransitionStates) {
                comment.addSecondaryTransitionState(stateService.getById(secondaryTransitionState));
            }
        }
    }

    public void appendAppointmentTimeslots(Comment comment, CommentDTO commentDTO) {
        for (LocalDateTime dateTime : commentDTO.getAppointmentTimeslots()) {
            CommentAppointmentTimeslot timeslot = new CommentAppointmentTimeslot().withDateTime(dateTime);
            comment.getAppointmentTimeslots().add(timeslot);
        }
    }

    public void appendAppointmentPreferences(Comment comment, CommentDTO commentDTO) {
        for (Integer timeslotId : commentDTO.getAppointmentPreferences()) {
            CommentAppointmentTimeslot timeslot = entityService.getById(CommentAppointmentTimeslot.class, timeslotId);
            comment.getAppointmentPreferences().add(new CommentAppointmentPreference().withDateTime(timeslot.getDateTime()));
        }
    }

    public void appendRejectionReason(Comment comment, CommentDTO commentDTO) {
        RejectionReason rejectionReason = entityService.getById(RejectionReason.class, commentDTO.getRejectionReason());
        comment.setRejectionReason(rejectionReason);
    }

    public void appendCommentProperties(Comment comment, CommentDTO commentDTO) {
        appendAssignedUsers(comment, commentDTO);
        appendTransitionStates(comment, commentDTO);

        if (commentDTO.getInterviewAppointment() != null) {
            appendInterviewAppointment(comment, commentDTO);
        }

        if (commentDTO.getInterviewInstruction() != null) {
            appendInterviewInstruction(comment, commentDTO);
        }

        if (commentDTO.getDocuments() != null) {
            appendDocuments(comment, commentDTO);
        }

        if (commentDTO.getCustomResponses() != null) {
            appendCustomResponses(comment, commentDTO);
        }
    }

    public void validateComment(Comment comment) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(comment, "comment");
        ValidationUtils.invokeValidator(commentValidator, comment, errors);
        if (errors.hasErrors()) {
            throw new PrismValidationException("Comment not completed", errors);
        }
    }

    public void reassignComments(User oldUser, User newUser) {
        commentDAO.reassignComments(oldUser, newUser);
        commentDAO.reassignDelegateComments(oldUser, newUser);
        reassignCommentAssignedUsers(oldUser, newUser);
    }

    private void reassignCommentAssignedUsers(User oldUser, User newUser) {
        List<CommentAssignedUser> commentAssignedUsers = commentDAO.getCommentAssignedUsers(oldUser);
        for (CommentAssignedUser commentAssignedUser : commentAssignedUsers) {
            commentAssignedUser.setUser(newUser);
            CommentAssignedUser duplicateCommentAssignedUser = entityService.getDuplicateEntity(commentAssignedUser);
            if (duplicateCommentAssignedUser != null) {
                commentAssignedUser.setUser(oldUser);
                entityService.delete(commentAssignedUser);
            }
        }
    }

    private Comment getLatestAppointmentPreferenceComment(Application application, Comment schedulingComment, User user) {
        DateTime baseline = schedulingComment.getCreatedTimestamp();
        Comment preferenceComment = getLatestComment(application, PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY, user, baseline);
        return preferenceComment == null ? getLatestComment(application, PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY, user, baseline)
                : preferenceComment;
    }

    private OfferRepresentation buildOfferRepresentation(Comment sourceComment) {
        CommentApplicationPositionDetail positionDetail = sourceComment.getPositionDetail();
        CommentApplicationOfferDetail offerDetail = sourceComment.getOfferDetail();

        boolean positionDetailNull = positionDetail == null;
        boolean offerDetailNull = offerDetail == null;

        return new OfferRepresentation().withPositionTitle(positionDetailNull ? null : positionDetail.getPositionTitle())
                .withPositionDescription(positionDetailNull ? null : positionDetail.getPositionDescription())
                .withPositionProvisionalStartDate(offerDetailNull ? null : offerDetail.getPositionProvisionalStartDate())
                .withAppointmentConditions(offerDetailNull ? null : offerDetail.getAppointmentConditions());
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

    private void assignUsers(Comment comment, Set<CommentAssignedUser> assignees) {
        for (CommentAssignedUser assignee : assignees) {
            PrismRoleTransitionType transitionType = assignee.getRoleTransitionType();
            comment.addAssignedUser(assignee.getUser().getParentUser(), assignee.getRole(), transitionType == null ? CREATE : transitionType);
        }
    }

    private CommentRepresentation getCommentRepresentation(User user, Comment comment, List<PrismRole> rolesOverridingRedactions,
            Set<PrismActionRedactionType> redactions, List<PrismRole> creatableRoles) {
        User author = comment.getUser();
        User authorDelegate = comment.getDelegateUser();

        CommentRepresentation representation;
        if (!rolesOverridingRedactions.isEmpty() || redactions.isEmpty() || isCommentOwner(comment, user)) {
            representation = mapper.map(comment, CommentRepresentation.class);
            appendCommentAssignedUsers(comment, representation, creatableRoles);
        } else {
            UserRepresentation authorRepresentation = new UserRepresentation().withFirstName(author.getFirstName()).withLastName(author.getLastName())
                    .withEmail(author.getEmail());
            UserRepresentation authorDelegateRepresenation = authorDelegate == null ? null : new UserRepresentation()
                    .withFirstName(authorDelegate.getFirstName()).withLastName(authorDelegate.getLastName()).withEmail(authorDelegate.getEmail());

            representation = new CommentRepresentation().addId(comment.getId()).addUser(authorRepresentation).addDelegateUser(authorDelegateRepresenation)
                    .addAction(comment.getAction().getId()).addDeclinedResponse(comment.getDeclinedResponse())
                    .addCreatedTimestamp(comment.getCreatedTimestamp());

            if (redactions.contains(PrismActionRedactionType.ALL_ASSESSMENT_CONTENT)) {
                CommentApplicationInterviewAppointment interviewAppointment = comment.getInterviewAppointment();
                if (interviewAppointment != null) {
                    representation.setInterviewAppointment(new CommentApplicationInterviewAppointmentRepresentation()
                            .withInterviewDateTime(interviewAppointment.getInterviewDateTime())
                            .withInterviewTimeZone(interviewAppointment.getInterviewTimeZone())
                            .withInterviewDuration(interviewAppointment.getInterviewDuration()));
                }

                CommentApplicationInterviewInstruction interviewInstruction = comment.getInterviewInstruction();
                if (interviewInstruction != null) {
                    representation.setInterviewInstruction(new CommentApplicationInterviewInstructionRepresentation().withIntervieweeInstructions(
                            interviewInstruction.getIntervieweeInstructions()).withInterviewLocation(interviewInstruction.getInterviewLocation()));
                }

                Set<AppointmentTimeslotRepresentation> timeslots = Sets.newLinkedHashSet();
                for (CommentAppointmentTimeslot timeslot : comment.getAppointmentTimeslots()) {
                    timeslots.add(new AppointmentTimeslotRepresentation().withId(timeslot.getId()).withDateTime(timeslot.getDateTime()));
                }
                representation.setAppointmentTimeslots(Lists.newLinkedList(timeslots));
            }
        }

        return representation;
    }

    private void appendCommentAssignedUsers(Comment comment, CommentRepresentation representation, List<PrismRole> creatableRoles) {
        Set<CommentAssignedUser> assignees = comment.getAssignedUsers();
        if (!assignees.isEmpty()) {
            List<CommentAssignedUserRepresentation> representations = Lists.newLinkedList();
            for (CommentAssignedUser assignee : assignees) {
                if (creatableRoles.contains(assignee.getRole().getId())) {
                    representations.add(mapper.map(assignee, CommentAssignedUserRepresentation.class));
                }
            }
            representation.setAssignedUsers(representations);
        }
    }

    private void buildAggregatedRating(Comment comment) {
        if (!comment.getCustomResponses().isEmpty()) {
            BigDecimal aggregatedRating = new BigDecimal(0.00);
            for (CommentCustomResponse customResponse : comment.getCustomResponses()) {
                switch (customResponse.getActionCustomQuestionConfiguration().getCustomQuestionType()) {
                case RATING_NORMAL:
                    aggregatedRating = aggregatedRating.add(getWeightedRatingComponent(customResponse, 5));
                    break;
                case RATING_WEIGHTED:
                    aggregatedRating = aggregatedRating.add(getWeightedRatingComponent(customResponse, 8));
                    break;
                default:
                    break;
                }
            }
            comment.setApplicationRating(aggregatedRating);
        }
    }

    private BigDecimal getWeightedRatingComponent(CommentCustomResponse customResponse, Integer denominator) {
        String propertyValue = customResponse.getPropertyValue();
        return new BigDecimal(propertyValue == null ? PrismConstants.DEFAULT_RATING.toString() : propertyValue).divide(new BigDecimal(denominator))
                .multiply(new BigDecimal(5)).multiply(customResponse.getActionCustomQuestionConfiguration().getWeighting()).setScale(2, RoundingMode.HALF_UP);
    }

    private void appendInterviewScheduledExpeditedComments(Comment comment) throws Exception {
        LocalDateTime interviewDateTime = comment.getInterviewAppointment().getInterviewDateTime();
        comment.getAppointmentTimeslots().add(new CommentAppointmentTimeslot().withDateTime(interviewDateTime));

        Resource resource = comment.getResource();
        PrismAction prismAction = APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
        Action action = actionService.getById(prismAction);
        DateTime baseline = comment.getCreatedTimestamp();

        User invoker = comment.getUser();
        List<User> users = commentDAO.getAssignedUsers(comment, APPLICATION_INTERVIEWER, APPLICATION_INTERVIEWEE);
        for (User user : users) {
            Comment preferenceComment = createAutomatedInterviewPreferenceComment(resource, action, invoker, user, interviewDateTime, baseline);
            persistComment(resource, preferenceComment);
            resource.addComment(preferenceComment);
        }
    }

    private void appendInterviewScheduledConfirmedComments(Comment comment) throws Exception {
        Resource resource = comment.getResource();
        PrismAction prismAction = APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
        Action action = actionService.getById(prismAction);
        DateTime baseline = comment.getCreatedTimestamp().minusSeconds(1);

        User invoker = comment.getUser();
        List<User> users = userService.getUsersWithAction(resource, prismAction, APPLICATION_UPDATE_INTERVIEW_AVAILABILITY);
        LocalDateTime interviewDateTime = comment.getInterviewAppointment().getInterviewDateTime();
        for (User user : users) {
            Comment oldPreferenceComment = getLatestComment(resource, user, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY,
                    APPLICATION_UPDATE_INTERVIEW_AVAILABILITY);

            List<LocalDateTime> oldPreferences = commentDAO.getAppointmentPreferences(oldPreferenceComment);
            if (!oldPreferences.contains(interviewDateTime)) {
                Comment newPreferenceComment = createAutomatedInterviewPreferenceComment(resource, action, invoker, user, interviewDateTime, baseline);
                actionService.executeActionSilent(resource, action, newPreferenceComment);
            }
        }
    }

    private Comment createAutomatedInterviewPreferenceComment(Resource resource, Action action, User invoker, User user, LocalDateTime interviewDateTime,
            DateTime baseline) {
        Comment preferenceComment = new Comment().withResource(resource).withAction(action).withUser(invoker).withDelegateUser(user)
                .withDeclinedResponse(false).withState(resource.getState()).withCreatedTimestamp(baseline);
        preferenceComment.getAppointmentPreferences().add(new CommentAppointmentPreference().withDateTime(interviewDateTime));
        return preferenceComment;
    }

    private void appendApplicationReferees(Resource resource, Comment comment) {
        Role refereeRole = roleService.getById(APPLICATION_REFEREE);
        for (User referee : applicationService.getUnassignedApplicationReferees((Application) resource)) {
            comment.addAssignedUser(referee, refereeRole, CREATE);
        }
    }

    private void exhumeApplicationReferees(Resource resource, Comment comment) {
        Role refereeRole = roleService.getById(APPLICATION_REFEREE);
        for (User referee : applicationService.getUnassignedApplicationReferees((Application) resource)) {
            comment.addAssignedUser(referee, refereeRole, EXHUME);
        }
    }

    private void updateCommentStates(Comment comment) {
        for (ResourceState resourceState : comment.getResource().getResourceStates()) {
            if (BooleanUtils.isFalse(resourceState.getPrimaryState())) {
                comment.addCommentState(resourceState.getState(), false);
            }
        }
    }

    private void createCommentTransitionStates(Comment comment, State transitionState, Set<State> stateTerminations) {
        for (State secondaryTransitionState : comment.getSecondaryTransitionStates()) {
            if (!stateTerminations.contains(secondaryTransitionState)) {
                comment.addCommentTransitionState(secondaryTransitionState, false);
            }
        }
    }

    private void updateCommentTransitionStates(Comment comment, Set<State> stateTerminations) {
        for (ResourceState resourceState : comment.getResource().getResourceStates()) {
            State state = resourceState.getState();
            if (!stateTerminations.contains(state) && BooleanUtils.isFalse(resourceState.getPrimaryState())) {
                comment.addCommentTransitionState(state, false);
            }
        }
    }

    private void appendCustomResponses(Comment comment, CommentDTO commentDTO) {
        for (CommentCustomResponseDTO response : commentDTO.getCustomResponses()) {
            if (response.getPropertyValue() != null) {
                ActionCustomQuestionConfiguration configuration = entityService.getById(ActionCustomQuestionConfiguration.class, response.getId());
                comment.getCustomResponses().add(
                        new CommentCustomResponse().withActionCustomQuestionConfiguration(configuration).withPropertyValue(response.getPropertyValue()));
            }
        }
    }

    private void appendDocuments(Comment comment, CommentDTO commentDTO) {
        for (FileDTO fileDTO : commentDTO.getDocuments()) {
            Document document = documentService.getById(fileDTO.getId(), PrismFileCategory.DOCUMENT);
            comment.getDocuments().add(document);
        }
    }

    private void appendInterviewAppointment(Comment comment, CommentDTO commentDTO) {
        CommentApplicationInterviewAppointmentDTO interviewAppointmentDTO = commentDTO.getInterviewAppointment();
        comment.setInterviewAppointment(new CommentApplicationInterviewAppointment().withInterviewDateTime(interviewAppointmentDTO.getInterviewDateTime())
                .withInterviewTimezone(interviewAppointmentDTO.getInterviewTimeZone()).withInterviewDuration(interviewAppointmentDTO.getInterviewDuration()));
    }

    private void appendInterviewInstruction(Comment comment, CommentDTO commentDTO) {
        CommentApplicationInterviewInstructionDTO interviewInstructionDTO = commentDTO.getInterviewInstruction();
        comment.setInterviewInstruction(new CommentApplicationInterviewInstruction()
                .withIntervieweeInstructions(interviewInstructionDTO.getIntervieweeInstructions())
                .withInterviewerInstructions(interviewInstructionDTO.getInterviewerInstructions())
                .withInterviewLocation(interviewInstructionDTO.getInterviewLocation()));
    }

    private List<CommentAssignedUserRepresentation> getAssignedUsers(List<Integer> commentIds, List<PrismRole> roleIds) {
        List<CommentAssignedUserRepresentation> representations = Lists.newLinkedList();
        for (CommentAssignedUser assignedUser : commentDAO.getAssignedUsers(commentIds, roleIds)) {
            representations.add(mapper.map(assignedUser, CommentAssignedUserRepresentation.class));
        }
        return representations;
    }

}
