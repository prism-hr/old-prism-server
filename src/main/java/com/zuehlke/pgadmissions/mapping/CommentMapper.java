package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_ASSESSMENT_CONTENT;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentPreference;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentCustomResponse;
import com.zuehlke.pgadmissions.domain.comment.CommentExport;
import com.zuehlke.pgadmissions.domain.comment.CommentInterviewAppointment;
import com.zuehlke.pgadmissions.domain.comment.CommentInterviewInstruction;
import com.zuehlke.pgadmissions.domain.comment.CommentOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentPositionDetail;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.rest.representation.FileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentAppointmentPreferenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentAppointmentTimeslotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentAssignedUserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentCustomResponseRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentExportRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewInstructionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentOfferDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentPositionDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.TimelineRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.TimelineRepresentation.TimelineCommentGroupRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.RoleService;

@Service
@Transactional
public class CommentMapper {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private RoleService roleService;

    public TimelineRepresentation getTimelineRepresentation(Resource resource, User user) {
        TimelineRepresentation timeline = new TimelineRepresentation();
        List<Comment> transitionComments = commentService.getStateGroupTransitionComments(resource);

        int transitionCommentCount = transitionComments.size();
        if (transitionCommentCount > 0) {
            StateGroup stateGroup = null;
            List<Comment> previousStateComments = Lists.newArrayList();

            List<PrismRole> rolesOverridingRedactions = roleService.getRolesOverridingRedactions(resource, user);
            List<PrismRole> creatableRoles = roleService.getCreatableRoles(resource.getResourceScope());
            HashMultimap<PrismAction, PrismActionRedactionType> redactions = actionService.getRedactions(resource, user);

            for (int i = 0; i < transitionCommentCount; i++) {
                Comment start = transitionComments.get(i);
                Comment close = i == (transitionCommentCount - 1) ? null : transitionComments.get(i + 1);

                stateGroup = stateGroup == null ? start.getState().getStateGroup() : stateGroup;
                List<Comment> stateComments = commentService.getStateComments(resource, start, close, stateGroup, previousStateComments);

                List<Integer> batchedViewEditCommentIds = null;
                CommentRepresentation lastViewEditComment = null;
                TimelineCommentGroupRepresentation commentGroup = new TimelineCommentGroupRepresentation().withStateGroup(stateGroup.getId());

                for (Comment comment : stateComments) {
                    Set<PrismActionRedactionType> commentRedactions = redactions.get(comment.getAction().getId());
                    if (comment.isViewEditComment()) {
                        if (lastViewEditComment == null || lastViewEditComment.getCreatedTimestamp().plusHours(1).isBefore(comment.getCreatedTimestamp())) {
                            CommentRepresentation representation = getCommentRepresentationSecured(user, comment, rolesOverridingRedactions, commentRedactions,
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
                            lastViewEditComment.setAssignedUsers(getCommentAssignedUserRepresentations(batchedViewEditCommentIds, creatableRoles));
                        }
                    } else {
                        CommentRepresentation representation = getCommentRepresentationSecured(user, comment, rolesOverridingRedactions, commentRedactions,
                                creatableRoles);
                        commentGroup.addComment(representation);
                    }
                }

                timeline.addCommentGroup(commentGroup);
                stateGroup = close == null ? null : close.getTransitionState().getStateGroup();
                previousStateComments = stateComments;
            }
        }

        return timeline;
    }

    public List<CommentAppointmentTimeslotRepresentation> getCommentAppointmentTimeslotRepresentations(Set<CommentAppointmentTimeslot> timeslots) {
        List<CommentAppointmentTimeslotRepresentation> representations = Lists.newLinkedList();
        for (CommentAppointmentTimeslot timeslot : timeslots) {
            representations.add(new CommentAppointmentTimeslotRepresentation().withId(timeslot.getId()).withDateTime(timeslot.getDateTime()));
        }
        return representations;
    }

    public List<CommentAppointmentPreferenceRepresentation> getCommentAppointmentPreferenceRepresentations(Comment schedulingComment,
            Set<CommentAppointmentTimeslot> timeslots) {
        List<CommentAppointmentPreferenceRepresentation> representations = Lists.newLinkedList();

        for (User user : commentService.getAppointmentInvitees(schedulingComment)) {
            CommentAppointmentPreferenceRepresentation representation = new CommentAppointmentPreferenceRepresentation()
                    .withUser(userMapper.getUserRepresentationSimple(user));

            List<Integer> inviteePreferences = Lists.newLinkedList();
            Comment preferenceComment = commentService.getLatestAppointmentPreferenceComment(schedulingComment.getApplication(), schedulingComment, user);
            if (preferenceComment != null) {
                List<LocalDateTime> preferences = commentService.getAppointmentPreferences(preferenceComment);
                for (CommentAppointmentTimeslot timeslot : timeslots) {
                    if (preferences.contains(timeslot.getDateTime())) {
                        inviteePreferences.add(timeslot.getId());
                    }
                }
                representation.setPreferences(inviteePreferences);
            }

            representations.add(representation);
        }

        return representations;
    }

    public CommentInterviewAppointmentRepresentation getCommentInterviewAppointmentRepresentation(Comment comment) {
        CommentInterviewAppointment appointment = comment.getInterviewAppointment();
        if (appointment != null) {
            return new CommentInterviewAppointmentRepresentation().withInterviewDateTime(appointment.getInterviewDateTime())
                    .withInterviewTimeZone(appointment.getInterviewTimeZone()).withInterviewDuration(appointment.getInterviewDuration());
        }
        return null;
    }

    public CommentInterviewInstructionRepresentation getCommentInterviewInstructionRepresentation(Comment comment, boolean interviewer) {
        CommentInterviewInstruction instruction = comment.getInterviewInstruction();
        if (instruction != null) {
            CommentInterviewInstructionRepresentation representation = new CommentInterviewInstructionRepresentation().withIntervieweeInstructions(
                    instruction.getIntervieweeInstructions()).withInterviewLocation(instruction.getInterviewLocation());
            if (interviewer) {
                representation.setInterviewerInstructions(instruction.getInterviewerInstructions());
            }
            return representation;
        }
        return null;
    }

    private CommentRepresentation getCommentRepresentationSecured(User user, Comment comment, List<PrismRole> rolesOverridingRedactions,
            Set<PrismActionRedactionType> redactions, List<PrismRole> creatableRoles) {
        if (!rolesOverridingRedactions.isEmpty() || redactions.isEmpty() || commentService.isCommentOwner(comment, user)) {
            CommentRepresentation representation = getCommentRepresentationExtended(comment);
            representation.setAssignedUsers(getCommentAssignedUserRepresentations(comment, creatableRoles));
            return representation;
        } else {
            CommentRepresentation representation = getCommentRepresentationSimple(comment);

            if (redactions.contains(ALL_ASSESSMENT_CONTENT)) {
                representation.setInterviewAppointment(getCommentInterviewAppointmentRepresentation(comment));
                representation.setInterviewInstruction(getCommentInterviewInstructionRepresentation(comment, false));
                representation.setAppointmentTimeslots(getCommentAppointmentTimeslotRepresentations(comment.getAppointmentTimeslots()));
            }

            return representation;
        }
    }

    private CommentRepresentation getCommentRepresentationSimple(Comment comment) {
        return new CommentRepresentation().withId(comment.getId()).withUser(userMapper.getUserRepresentationSimple(comment.getUser()))
                .withDelegateUser(getCommentDelegateUserRepresentation(comment)).withAction(comment.getAction().getId())
                .withDeclinedResponse(comment.getDeclinedResponse()).withCreatedTimestamp(comment.getCreatedTimestamp());
    }

    private CommentRepresentation getCommentRepresentationExtended(Comment comment) {
        ImportedEntitySimple rejectionReason = comment.getRejectionReason();
        return getCommentRepresentationSimple(comment).withContent(comment.getContent()).withState(comment.getState().getId())
                .withTransitionState(comment.getTransitionState().getId()).withApplicationEligible(comment.getApplicationEligible())
                .withApplicationInterested(comment.getApplicationInterested()).withInterviewAppointment(getCommentInterviewAppointmentRepresentation(comment))
                .withInterviewInstruction(getCommentInterviewInstructionRepresentation(comment, true))
                .withPositionDetail(getCommentPositionDetailRepresentation(comment)).withOfferDetail(getCommentOfferDetailRepresentation(comment))
                .withRecruiterAcceptAppointment(comment.getRecruiterAcceptAppointment()).withApplicationReserveStatus(comment.getApplicationReserveStatus())
                .withRejectionReason(rejectionReason == null ? null : rejectionReason.getName()).withRejectionReasonSystem(comment.getRejectionReasonSystem())
                .withApplicationRating(comment.getApplicationRating()).withExport(getCommentExportRepresentation(comment))
                .withAppointmentTimeslots(getCommentAppointmentTimeslotRepresentations(comment.getAppointmentTimeslots()))
                .withAppointmentPreferences(getCommentAppointmentPreferenceRepresentations(comment)).withDocuments(getCommentDocumentRepresentations(comment))
                .withCustomResponses(getCommentCustomResponseRepresentations(comment));
    }

    private CommentAssignedUserRepresentation getCommentAssignedUserRepresentation(CommentAssignedUser commentAssignedUser) {
        return new CommentAssignedUserRepresentation().withUser(userMapper.getUserRepresentationSimple(commentAssignedUser.getUser()))
                .withRole(commentAssignedUser.getRole().getId()).withRoleTransitionType(commentAssignedUser.getRoleTransitionType());
    }

    private List<CommentAssignedUserRepresentation> getCommentAssignedUserRepresentations(Comment comment, List<PrismRole> creatableRoles) {
        List<CommentAssignedUserRepresentation> representations = Lists.newLinkedList();
        for (CommentAssignedUser commentAssignedUser : comment.getAssignedUsers()) {
            if (creatableRoles.contains(commentAssignedUser.getRole().getId())) {
                representations.add(getCommentAssignedUserRepresentation(commentAssignedUser));
            }
        }
        return representations;
    }

    private List<CommentAssignedUserRepresentation> getCommentAssignedUserRepresentations(List<Integer> comments, List<PrismRole> creatableRoles) {
        List<CommentAssignedUserRepresentation> representations = Lists.newLinkedList();
        for (CommentAssignedUser commentAssignedUser : commentService.getAssignedUsers(comments, creatableRoles)) {
            representations.add(getCommentAssignedUserRepresentation(commentAssignedUser));
        }
        return representations;
    }

    private UserRepresentationSimple getCommentDelegateUserRepresentation(Comment comment) {
        User delegate = comment.getDelegateUser();
        return delegate == null ? null : userMapper.getUserRepresentationSimple(delegate);
    }

    private CommentPositionDetailRepresentation getCommentPositionDetailRepresentation(Comment comment) {
        CommentPositionDetail position = comment.getPositionDetail();
        return position == null ? null : new CommentPositionDetailRepresentation().withPositionTitle(position.getPositionTitle()).withPositionDescription(
                position.getPositionDescription());
    }

    private CommentOfferDetailRepresentation getCommentOfferDetailRepresentation(Comment comment) {
        CommentOfferDetail offer = comment.getOfferDetail();
        return offer == null ? null : new CommentOfferDetailRepresentation().withPositionProvisionalStartDate(offer.getPositionProvisionalStartDate())
                .withAppointmentConditions(offer.getAppointmentConditions());
    }

    private CommentExportRepresentation getCommentExportRepresentation(Comment comment) {
        CommentExport export = comment.getExport();
        return export == null ? null : new CommentExportRepresentation().withExportSucceeded(export.getExportSucceeded())
                .withExportRequest(export.getExportRequest()).withExportException(export.getExportException()).withExportReference(export.getExportReference());
    }

    private List<LocalDateTime> getCommentAppointmentPreferenceRepresentations(Comment comment) {
        List<LocalDateTime> representations = Lists.newLinkedList();
        for (CommentAppointmentPreference preference : comment.getAppointmentPreferences()) {
            representations.add(preference.getDateTime());
        }
        return representations;
    }

    private List<FileRepresentation> getCommentDocumentRepresentations(Comment comment) {
        List<FileRepresentation> representations = Lists.newLinkedList();
        for (Document document : comment.getDocuments()) {
            representations.add(documentMapper.getDocumentRepresentation(document));
        }
        return representations;
    }

    private List<CommentCustomResponseRepresentation> getCommentCustomResponseRepresentations(Comment comment) {
        List<CommentCustomResponseRepresentation> representations = Lists.newLinkedList();
        for (CommentCustomResponse response : comment.getCustomResponses()) {
            representations.add(getCommentCustomResponseRepresentation(response));
        }
        return representations;
    }

    private CommentCustomResponseRepresentation getCommentCustomResponseRepresentation(CommentCustomResponse response) {
        CommentCustomResponseRepresentation representation = new CommentCustomResponseRepresentation().withLabel(response
                .getActionCustomQuestionConfiguration().getLabel());
        if (response.getActionCustomQuestionConfiguration().getCustomQuestionType() == PrismCustomQuestionType.RATING_WEIGHTED) {
            String[] options = response.getActionCustomQuestionConfiguration().getOptions().split("\\|");
            Map<Integer, Integer> index = ImmutableMap.of(1, 0, 2, 1, 3, 2, 5, 3, 8, 4);
            representation.setPropertyValue(options[index.get(Integer.parseInt(response.getPropertyValue()))]);
        } else {
            representation.setPropertyValue(response.getPropertyValue());
        }
        return representation;
    }

}
