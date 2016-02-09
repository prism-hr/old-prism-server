package uk.co.alumeni.prism.services;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang.BooleanUtils.isFalse;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.joda.time.DateTime.now;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_CONTENT_BULK_PROCESSED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_PARTNER_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.document.PrismFileCategory.DOCUMENT;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.CommentDAO;
import uk.co.alumeni.prism.domain.Competence;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.application.ApplicationReferee;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentAppointmentPreference;
import uk.co.alumeni.prism.domain.comment.CommentAppointmentTimeslot;
import uk.co.alumeni.prism.domain.comment.CommentAssignedUser;
import uk.co.alumeni.prism.domain.comment.CommentCompetence;
import uk.co.alumeni.prism.domain.comment.CommentInterviewAppointment;
import uk.co.alumeni.prism.domain.comment.CommentInterviewInstruction;
import uk.co.alumeni.prism.domain.comment.CommentOfferDetail;
import uk.co.alumeni.prism.domain.comment.CommentPositionDetail;
import uk.co.alumeni.prism.domain.comment.CommentTransitionState;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.resource.ResourceState;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.exceptions.DeduplicationException;
import uk.co.alumeni.prism.rest.dto.DocumentDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentAssignedUserDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentCompetenceDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentInterviewAppointmentDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentInterviewInstructionDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentOfferDetailDTO;
import uk.co.alumeni.prism.rest.dto.comment.CommentPositionDetailDTO;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

import com.google.common.collect.Lists;

@Service
@Transactional
public class CommentService {

    @Inject
    private CommentDAO commentDAO;

    @Inject
    private ActivityService activityService;

    @Inject
    private DocumentService documentService;

    @Inject
    private EntityService entityService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Inject
    private ApplicationContext applicationContext;

    public Comment getById(int id) {
        return entityService.getById(Comment.class, id);
    }

    public Comment getLatestComment(Resource resource, PrismAction... prismActions) {
        return prismActions.length > 0 ? commentDAO.getLatestComment(resource, prismActions) : null;
    }

    public Comment getLatestComment(Resource resource, User user, PrismAction... prismActions) {
        return prismActions.length > 0 ? commentDAO.getLatestComment(resource, user, prismActions) : null;
    }

    public Comment getLatestComment(Resource resource, PrismAction actionId, User user, DateTime baseline) {
        return commentDAO.getLatestComment(resource, actionId, user, baseline);
    }

    public boolean isCommentOwner(Comment comment, User user) {
        Integer userId = user.getId();
        User ownerDelegate = comment.getDelegateUser();
        return (Objects.equals(comment.getUser().getId(), userId) || (ownerDelegate != null && Objects.equals(ownerDelegate.getId(), userId)));
    }

    public void createOrUpdateComment(Resource resource, Comment comment) {
        Set<CommentAssignedUser> transientAssignees = comment.getAssignedUsers();
        Set<CommentAssignedUser> persistentAssignees = newHashSet(transientAssignees);
        transientAssignees.clear();

        Set<CommentTransitionState> transientTransitionStates = comment.getCommentTransitionStates();
        Set<CommentTransitionState> persistentTransitionStates = newHashSet(transientTransitionStates);
        transientTransitionStates.clear();

        Set<CommentAppointmentTimeslot> transientTimeslots = comment.getAppointmentTimeslots();
        Set<CommentAppointmentTimeslot> persistentTimeslots = newHashSet(transientTimeslots);
        transientTimeslots.clear();

        Set<CommentAppointmentPreference> transientPreferences = comment.getAppointmentPreferences();
        Set<CommentAppointmentPreference> persistentPreferences = newHashSet(transientPreferences);
        transientPreferences.clear();

        Set<CommentCompetence> transientCompetences = comment.getCompetences();
        Set<CommentCompetence> persistentCompetences = newHashSet(transientCompetences);
        transientCompetences.clear();

        if (comment.getId() != null) {
            entityService.save(comment);
            setSequenceIdentifier(comment, comment.getCreatedTimestamp());
        }

        comment.getAssignedUsers().addAll(persistentAssignees.stream().map(assignee -> assignee.withRoleTransitionType( //
                assignee.getRoleTransitionType() == null ? CREATE : assignee.getRoleTransitionType())).collect(toSet()));

        comment.getCommentTransitionStates().addAll(persistentTransitionStates);
        comment.getAppointmentTimeslots().addAll(persistentTimeslots);
        comment.getAppointmentPreferences().addAll(persistentPreferences);
        comment.getCompetences().addAll(persistentCompetences);
        resource.addComment(comment);

        entityService.flush();
    }

    public void persistComment(Resource resource, Comment comment) {
        createOrUpdateComment(resource, comment);
        DateTime baseline = comment.getCreatedTimestamp();
        comment.setSubmittedTimestamp(baseline);
        setSequenceIdentifier(comment, baseline);
    }

    public Comment replicateComment(Resource resource, Comment templateComment) {
        String content = templateComment.getContent();
        if (content != null) {
            content = applicationContext.getBean(PropertyLoader.class).localizeLazy(resource.getSystem()).loadLazy(SYSTEM_COMMENT_CONTENT_BULK_PROCESSED);
        }

        Action action = templateComment.getAction();
        Comment replicateComment = new Comment().withResource(resource).withUser(templateComment.getUser()).withDelegateUser(templateComment.getDelegateUser())
                .withAction(action).withContent(content).withDeclinedResponse(false).withState(templateComment.getState())
                .withTransitionState(templateComment.getTransitionState()).withRejectionReason(templateComment.getRejectionReason())
                .withCreatedTimestamp(now());

        CommentInterviewAppointment templateInterviewAppointment = templateComment.getInterviewAppointment();
        if (templateInterviewAppointment != null) {
            replicateComment.setInterviewAppointment(new CommentInterviewAppointment()
                    .withInterviewDateTime(templateInterviewAppointment.getInterviewDateTime())
                    .withInterviewTimezone(templateInterviewAppointment.getInterviewTimeZone())
                    .withInterviewDuration(templateInterviewAppointment.getInterviewDuration()));
        }

        CommentInterviewInstruction templateInterviewInstruction = templateComment.getInterviewInstruction();
        if (templateInterviewInstruction != null) {
            replicateComment.setInterviewInstruction(new CommentInterviewInstruction()
                    .withIntervieweeInstructions(templateInterviewInstruction.getIntervieweeInstructions())
                    .withInterviewerInstructions(templateInterviewInstruction.getInterviewerInstructions())
                    .withInterviewLocation(templateInterviewInstruction.getInterviewLocation()));
        }

        CommentPositionDetail templatePositionDetail = templateComment.getPositionDetail();
        if (templatePositionDetail != null) {
            replicateComment.setPositionDetail(new CommentPositionDetail().withPositionName(templatePositionDetail.getPositionName())
                    .withPositionDescription(templatePositionDetail.getPositionDescription()));
        }

        CommentOfferDetail templateOfferDetail = templateComment.getOfferDetail();
        if (templateOfferDetail != null) {
            replicateComment.setOfferDetail(new CommentOfferDetail().withPositionProvisionStartDate(templateOfferDetail.getPositionProvisionalStartDate())
                    .withAppointmentConditions(templateOfferDetail.getAppointmentConditions()));
        }

        if (isTrue(action.getReplicableUserAssignmentAction())) {
            templateComment.getAssignedUsers().forEach(assignedUser -> {
                if (assignedUser.getRoleTransitionType().equals(CREATE)) {
                    replicateComment.addAssignedUser(assignedUser.getUser(), assignedUser.getRole(), CREATE);
                }
            });
        }

        Set<CommentAppointmentTimeslot> replicateAppointmentTimelots = replicateComment.getAppointmentTimeslots();
        templateComment.getAppointmentTimeslots().stream().forEach(appointmentTimeslot -> {
            replicateAppointmentTimelots.add(new CommentAppointmentTimeslot().withDateTime(appointmentTimeslot.getDateTime()));
        });

        templateComment.getCommentTransitionStates().stream().forEach(commentTransitionState -> {
            if (isFalse(commentTransitionState.getPrimaryState())) {
                replicateComment.addSecondaryTransitionState(commentTransitionState.getState());
            }
        });

        return replicateComment;
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

    public Comment createInterviewPreferenceComment(Resource resource, Action action, User invoker, User user, LocalDateTime interviewDateTime,
            DateTime baseline) {
        Comment preferenceComment = new Comment().withResource(resource).withAction(action).withUser(invoker).withDelegateUser(user)
                .withDeclinedResponse(false).withState(resource.getState()).withCreatedTimestamp(baseline);
        preferenceComment.getAppointmentPreferences().add(new CommentAppointmentPreference().withDateTime(interviewDateTime));
        return preferenceComment;
    }

    public List<LocalDateTime> getAppointmentPreferences(Comment comment) {
        return commentDAO.getAppointmentPreferences(comment);
    }

    public List<LocalDateTime> getAppointmentPreferences(Application application, User user) {
        Comment preferenceComment = getLatestComment(application, user, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY,
                APPLICATION_UPDATE_INTERVIEW_AVAILABILITY);
        return getAppointmentPreferences(preferenceComment);
    }

    public List<User> getAssignedUsers(Comment comment, PrismRole... roles) {
        return commentDAO.getAssignedUsers(comment, roles);
    }

    public List<CommentAssignedUser> getAssignedUsers(List<Integer> commentIds, List<PrismRole> roleIds) {
        return commentDAO.getAssignedUsers(commentIds, roleIds);
    }

    public List<Comment> getTimelineComments(Resource resource) {
        return commentDAO.getTimelineComments(resource);
    }

    public List<User> getAppointmentInvitees(Comment comment) {
        return commentDAO.getAppointmentInvitees(comment);
    }

    public Comment getLatestAppointmentPreferenceComment(Application application, Comment schedulingComment, User user) {
        DateTime baseline = schedulingComment.getSubmittedTimestamp();
        Comment preferenceComment = getLatestComment(application, APPLICATION_UPDATE_INTERVIEW_AVAILABILITY, user, baseline);
        return preferenceComment == null ? getLatestComment(application, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY, user, baseline)
                : preferenceComment;
    }

    public List<CommentAssignedUser> getAssignedHiringManagers(Comment comment) {
        return commentDAO.getAssignedHiringManagers(comment);
    }

    public List<String> getDeclinedHiringManagers(Comment comment) {
        return commentDAO.getDeclinedHiringManagers(comment);
    }

    public List<Comment> getResourceOwnerComments(Resource resource) {
        return commentDAO.getResourceOwnerComments(resource);
    }

    public List<CommentAssignedUser> getResourceOwnerCommentAssignedUsers(Resource resource) {
        return commentDAO.getResourceOwnerCommentAssignedUsers(resource);
    }

    public void preprocessClaimComment(User user, CommentDTO commentDTO) {
        commentDTO.setAssignedUsers(newArrayList(new CommentAssignedUserDTO()
                .withUser(new UserDTO().withId(user.getId()).withFirstName(user.getFirstName()).withLastName(user.getLastName()).withEmail(user.getEmail()))
                .withRole(PrismRole.valueOf(commentDTO.getResource().getScope().name() + "_ADMINISTRATOR"))));
    }

    public List<Comment> getTransitionCommentHistory(Resource resource) {
        return commentDAO.getTransitionCommentHistory(resource);
    }

    public List<Comment> getComments(List<Integer> commentIds) {
        return commentDAO.getComments(commentIds);
    }

    public Comment prepareProcessResourceComment(Resource resource, User user, Action action, CommentDTO commentDTO) {
        Integer commentId = commentDTO.getId();
        Comment comment = commentId == null ? new Comment() : getById(commentId);

        comment.setUser(user);
        comment.setDelegateUser(userService.getById(commentDTO.getDelegateUser()));
        comment.setResource(resource);
        comment.setAction(action);
        comment.setRating(commentDTO.getRating());
        comment.setTransitionState(entityService.getById(State.class, commentDTO.getTransitionState()));
        comment.setCreatedTimestamp(now());
        comment.setDeclinedResponse(toBoolean(commentDTO.getDeclinedResponse()));

        if (resource.getResourceScope().equals(APPLICATION)) {
            prepareProcessApplicationComment((Application) resource, user, action, comment, commentDTO);
        } else {
            prepareProcessResourceParentComment((ResourceParent) resource, user, action, comment, commentDTO);
        }

        comment.setSubmit(commentDTO.getSubmit());
        return comment;
    }

    private void prepareProcessApplicationComment(Application application, User user, Action action, Comment comment, CommentDTO commentDTO) {
        PrismAction actionId = action.getId();
        boolean isCompleteAction = actionId.equals(APPLICATION_COMPLETE);

        comment.setApplicantKnown(commentDTO.getApplicantKnown());
        comment.setApplicantKnownDuration(commentDTO.getApplicantKnownDuration());
        comment.setApplicantKnownCapacity(commentDTO.getApplicantKnownCapacity());
        comment.setEligible(commentDTO.getEligible());
        comment.setInterested(commentDTO.getInterested());
        comment.setInterviewAvailable(commentDTO.getInterviewAvailable());
        comment.setRecruiterAcceptAppointment(commentDTO.getRecruiterAcceptAppointment());
        comment.setPartnerAcceptAppointment(commentDTO.getPartnerAcceptAppointment());
        comment.setPartnerAcceptAppointment(commentDTO.getPartnerAcceptAppointment());
        comment.setRejectionReason(commentDTO.getRejectionReason());

        if (isCompleteAction) {
            comment.setShared(commentDTO.getShared());
            comment.setOnCourse(commentDTO.getOnCourse());
        }

        CommentPositionDetailDTO positionDetailDTO = commentDTO.getPositionDetail();
        if (positionDetailDTO != null) {
            comment.setPositionDetail(new CommentPositionDetail().withPositionName(positionDetailDTO.getPositionName()).withPositionDescription(
                    positionDetailDTO.getPositionDescription()));
        }

        CommentOfferDetailDTO offerDetailDTO = commentDTO.getOfferDetail();
        if (offerDetailDTO != null) {
            comment.setOfferDetail(new CommentOfferDetail().withPositionProvisionStartDate(offerDetailDTO.getPositionProvisionalStartDate())
                    .withAppointmentConditions(offerDetailDTO.getAppointmentConditions()));
        }

        appendCommentProperties(comment, commentDTO);
        appendCommentApplicationProperties(comment, commentDTO);

        if (isCompleteAction) {
            Role refereeRole = entityService.getById(Role.class, APPLICATION_REFEREE);
            for (ApplicationReferee referee : application.getReferees()) {
                comment.getAssignedUsers().add(new CommentAssignedUser().withUser(referee.getUser()).withRole(refereeRole));
            }
        }

        if (commentDTO.getAppointmentTimeslots() != null) {
            appendAppointmentTimeslots(comment, commentDTO);
        }

        if (commentDTO.getAppointmentPreferences() != null) {
            appendAppointmentPreferences(comment, commentDTO);
        }

        if (actionId.equals(APPLICATION_PROVIDE_PARTNER_APPROVAL)) {
            Boolean onCourse = commentDTO.getOnCourse();
            if (onCourse != null) {
                comment.setOnCourse(onCourse);
            }
        }
    }

    private <T extends ResourceParent> void prepareProcessResourceParentComment(T resource, User user, Action action, Comment comment, CommentDTO commentDTO) {
        String content;
        String resourceScopeReference = resource.getResourceScope().name();
        if (action.getId().equals(PrismAction.valueOf(resourceScopeReference + "_VIEW_EDIT"))) {
            content = applicationContext.getBean(PropertyLoader.class).localizeLazy(resource)
                    .loadLazy(PrismDisplayPropertyDefinition.valueOf(resourceScopeReference + "_COMMENT_UPDATED"));
        } else {
            content = commentDTO.getContent();
        }

        comment.setContent(content);
        appendCommentProperties(comment, commentDTO);
    }

    private void appendAssignedUsers(Comment comment, CommentDTO commentDTO) throws DeduplicationException {
        if (commentDTO.getAssignedUsers() != null) {
            for (CommentAssignedUserDTO assignedUserDTO : commentDTO.getAssignedUsers()) {
                UserDTO commentUserDTO = assignedUserDTO.getUser();
                User commentUser = userService.getOrCreateUser(commentUserDTO.getFirstName(), commentUserDTO.getLastName(), commentUserDTO.getEmail());
                comment.getAssignedUsers().add(
                        new CommentAssignedUser().withUser(commentUser).withRole(entityService.getById(Role.class, assignedUserDTO.getRole())));
            }
        }
    }

    private void appendTransitionStates(Comment comment, CommentDTO commentDTO) {
        comment.setTransitionState(stateService.getById(commentDTO.getTransitionState()));
        List<PrismState> secondaryTransitionStates = commentDTO.getSecondaryTransitionStates();
        if (secondaryTransitionStates != null) {
            for (PrismState secondaryTransitionState : secondaryTransitionStates) {
                comment.addSecondaryTransitionState(stateService.getById(secondaryTransitionState));
            }
        }
    }

    private void appendAppointmentTimeslots(Comment comment, CommentDTO commentDTO) {
        Set<CommentAppointmentTimeslot> appointmentTimeslots = comment.getAppointmentTimeslots();
        for (LocalDateTime dateTime : commentDTO.getAppointmentTimeslots()) {
            CommentAppointmentTimeslot timeslot = new CommentAppointmentTimeslot().withDateTime(dateTime);
            appointmentTimeslots.add(timeslot);
        }
    }

    private void appendAppointmentPreferences(Comment comment, CommentDTO commentDTO) {
        Set<CommentAppointmentPreference> appointmentPreferences = comment.getAppointmentPreferences();
        for (Integer timeslotId : commentDTO.getAppointmentPreferences()) {
            CommentAppointmentTimeslot timeslot = entityService.getById(CommentAppointmentTimeslot.class, timeslotId);
            appointmentPreferences.add(new CommentAppointmentPreference().withDateTime(timeslot.getDateTime()));
        }
    }

    private void appendCommentProperties(Comment comment, CommentDTO commentDTO) {
        appendAssignedUsers(comment, commentDTO);
        appendTransitionStates(comment, commentDTO);

        if (commentDTO.getDocuments() != null) {
            appendDocuments(comment, commentDTO);
        }
    }

    private void appendCommentApplicationProperties(Comment comment, CommentDTO commentDTO) {
        if (commentDTO.getCompetences() != null) {
            appendCompetences(comment, commentDTO);
        }

        if (commentDTO.getInterviewAppointment() != null) {
            appendInterviewAppointment(comment, commentDTO);
        }

        if (commentDTO.getInterviewInstruction() != null) {
            appendInterviewInstruction(comment, commentDTO);
        }
    }

    private void updateCommentStates(Comment comment) {
        for (ResourceState resourceState : comment.getResource().getResourceStates()) {
            if (isFalse(resourceState.getPrimaryState())) {
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
            if (!stateTerminations.contains(state) && isFalse(resourceState.getPrimaryState())) {
                comment.addCommentTransitionState(state, false);
            }
        }
    }

    private void appendDocuments(Comment comment, CommentDTO commentDTO) {
        List<Integer> documentIds = Lists.newArrayList();
        for (DocumentDTO fileDTO : commentDTO.getDocuments()) {
            Integer documentId = fileDTO.getId();
            if (!documentIds.contains(documentId)) {
                comment.addDocument(documentService.getById(fileDTO.getId(), DOCUMENT));
            }
            documentIds.add(documentId);
        }
    }

    private void appendCompetences(Comment comment, CommentDTO commentDTO) {
        for (CommentCompetenceDTO commentCompetenceDTO : commentDTO.getCompetences()) {
            comment.addCompetence(entityService.getById(Competence.class, commentCompetenceDTO.getCompetenceId()), commentCompetenceDTO.getImportance(),
                    commentCompetenceDTO.getRating(), commentCompetenceDTO.getRemark());
        }
    }

    private void appendInterviewAppointment(Comment comment, CommentDTO commentDTO) {
        CommentInterviewAppointmentDTO interviewAppointmentDTO = commentDTO.getInterviewAppointment();
        comment.setInterviewAppointment(new CommentInterviewAppointment().withInterviewDateTime(interviewAppointmentDTO.getInterviewDateTime())
                .withInterviewTimezone(interviewAppointmentDTO.getInterviewTimeZone()).withInterviewDuration(interviewAppointmentDTO.getInterviewDuration()));
    }

    private void appendInterviewInstruction(Comment comment, CommentDTO commentDTO) {
        CommentInterviewInstructionDTO interviewInstructionDTO = commentDTO.getInterviewInstruction();
        comment.setInterviewInstruction(new CommentInterviewInstruction()
                .withIntervieweeInstructions(interviewInstructionDTO.getIntervieweeInstructions())
                .withInterviewerInstructions(interviewInstructionDTO.getInterviewerInstructions())
                .withInterviewLocation(interviewInstructionDTO.getInterviewLocation()));
    }

    private void setSequenceIdentifier(Comment comment, DateTime baseline) {
        activityService.setSequenceIdentifier(comment, baseline);
    }

}
