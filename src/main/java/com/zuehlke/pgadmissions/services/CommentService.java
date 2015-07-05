package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.document.PrismFileCategory.DOCUMENT;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentPreference;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentCustomResponse;
import com.zuehlke.pgadmissions.domain.comment.CommentInterviewAppointment;
import com.zuehlke.pgadmissions.domain.comment.CommentInterviewInstruction;
import com.zuehlke.pgadmissions.domain.comment.CommentTransitionState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.rest.dto.AssignedUserDTO;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentAssignedUserDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentCustomResponseDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentInterviewAppointmentDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentInterviewInstructionDTO;
import com.zuehlke.pgadmissions.rest.validation.CommentValidator;

@Service
@Transactional
public class CommentService {

    @Inject
    private CommentDAO commentDAO;

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
    private CommentValidator commentValidator;

    public Comment getById(int id) {
        return entityService.getById(Comment.class, id);
    }

    public Comment getLatestComment(Resource resource) {
        return commentDAO.getLatestComment(resource);
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

    public <T extends Resource> Comment getEarliestComment(ResourceParent parentResource, Class<T> resourceClass, PrismAction actionId) {
        return commentDAO.getEarliestComment(parentResource, resourceClass, actionId);
    }

    public boolean isCommentOwner(Comment comment, User user) {
        Integer userId = user.getId();
        User ownerDelegate = comment.getDelegateUser();
        return (comment.getUser().getId() == userId || (ownerDelegate != null && ownerDelegate.getId() == userId));
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
        ImportedEntitySimple rejectionReason = entityService.getById(ImportedEntitySimple.class, commentDTO.getRejectionReason());
        comment.setRejectionReason(rejectionReason);
    }

    public void appendCommentProperties(Comment comment, CommentDTO commentDTO) throws Exception {
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

    public Comment prepareResourceParentComment(ResourceParent resource, User user, Action action, CommentDTO commentDTO, PrismRole... roleAssignments)
            throws Exception {
        Comment comment = new Comment().withUser(user).withResource(resource).withContent(commentDTO.getContent()).withAction(action)
                .withCreatedTimestamp(new DateTime()).withDeclinedResponse(false);
        appendCommentProperties(comment, commentDTO);
        for (PrismRole roleAssignment : roleAssignments) {
            Role role = roleService.getById(roleAssignment);
            comment.addAssignedUser(user, role, CREATE);
        }
        return comment;
    }

    public List<Comment> getStateGroupTransitionComments(Resource resource) {
        return commentDAO.getStateGroupTransitionComments(resource);
    }

    public List<Comment> getStateComments(Resource resource, Comment start, Comment close, StateGroup stateGroup, List<Comment> exclusions) {
        return commentDAO.getStateComments(resource, start, close, stateGroup, exclusions);
    }

    public List<User> getAppointmentInvitees(Comment comment) {
        return commentDAO.getAppointmentInvitees(comment);
    }

    public Comment getLatestAppointmentPreferenceComment(Application application, Comment schedulingComment, User user) {
        DateTime baseline = schedulingComment.getCreatedTimestamp();
        Comment preferenceComment = getLatestComment(application, APPLICATION_UPDATE_INTERVIEW_AVAILABILITY, user, baseline);
        return preferenceComment == null ? getLatestComment(application, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY, user, baseline)
                : preferenceComment;
    }

    public List<CommentAssignedUser> getAssignedSupervisors(Comment comment) {
        return commentDAO.getAssignedSupervisors(comment);
    }

    public List<String> getDeclinedSupervisors(Comment comment) {
        return commentDAO.getDeclinedSupervisors(comment);
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

    private void assignUsers(Comment comment, Set<CommentAssignedUser> assignees) {
        for (CommentAssignedUser assignee : assignees) {
            PrismRoleTransitionType transitionType = assignee.getRoleTransitionType();
            comment.addAssignedUser(assignee.getUser(), assignee.getRole(), transitionType == null ? CREATE : transitionType);
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
            Document document = documentService.getById(fileDTO.getId(), DOCUMENT);
            comment.getDocuments().add(document);
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

}
