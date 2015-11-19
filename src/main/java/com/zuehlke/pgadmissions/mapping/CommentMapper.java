package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_PARTNER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_ASSESSMENT_CONTENT;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentPreference;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentCompetence;
import com.zuehlke.pgadmissions.domain.comment.CommentInterviewAppointment;
import com.zuehlke.pgadmissions.domain.comment.CommentInterviewInstruction;
import com.zuehlke.pgadmissions.domain.comment.CommentOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentPositionDetail;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentAppointmentPreferenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentAppointmentTimeslotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentAssignedUserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentCompetenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewInstructionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentOfferDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentPositionDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentTimelineRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentTimelineRepresentation.CommentGroupRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
@Transactional
public class CommentMapper {

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ActionService actionService;

    @Inject
    private CommentService commentService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    public CommentTimelineRepresentation getCommentTimelineRepresentation(Resource resource) {
        resourceService.validateViewResource(resource);

        List<Comment> comments = commentService.getTimelineComments(resource);
        List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(resource);
        User user = userService.getCurrentUser();

        CommentTimelineRepresentation timelineRepresentation = new CommentTimelineRepresentation();
        if (!comments.isEmpty()) {
            CommentGroupRepresentation groupRepresentation = null;

            List<PrismRole> creatableRoles = roleService.getCreatableRoles(resource.getResourceScope());
            List<PrismActionEnhancement> actionEnhancements = actionService.getPermittedActionEnhancements(user, resource);
            HashMultimap<PrismAction, PrismActionRedactionType> redactions = actionService.getRedactions(resource, user, overridingRoles);
            for (Comment comment : commentService.getTimelineComments(resource)) {
                Set<PrismActionRedactionType> actionRedactions = redactions.get(comment.getAction().getId());
                if (groupRepresentation == null) {
                    groupRepresentation = new CommentGroupRepresentation().withStateGroup(comment.getTransitionState().getStateGroup().getId());
                    groupRepresentation.addComment(getCommentRepresentation(user, comment, creatableRoles, actionEnhancements, overridingRoles, actionRedactions));
                    timelineRepresentation.addCommentGroup(groupRepresentation);
                } else if (comment.isStateGroupTransitionComment() && !comment.isSecondaryStateGroupTransitionComment()) {
                    groupRepresentation.addComment(getCommentRepresentation(user, comment, creatableRoles, actionEnhancements, overridingRoles, actionRedactions));
                    groupRepresentation = new CommentGroupRepresentation().withStateGroup(comment.getTransitionState().getStateGroup().getId());
                    timelineRepresentation.addCommentGroup(groupRepresentation);
                } else {
                    groupRepresentation.addComment(getCommentRepresentation(user, comment, creatableRoles, actionEnhancements, overridingRoles, actionRedactions));
                }
            }
        }

        return timelineRepresentation;
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

    public CommentRepresentation getCommentRepresentation(User user, Comment comment, List<PrismRole> overridingRoles) {
        Resource resource = comment.getResource();
        List<PrismRole> creatableRoles = roleService.getCreatableRoles(resource.getResourceScope());
        List<PrismActionEnhancement> actionEnhancements = actionService.getPermittedActionEnhancements(user, resource);
        Set<PrismActionRedactionType> redactions = actionService.getRedactions(resource, user, overridingRoles).get(comment.getAction().getId());
        return getCommentRepresentation(user, comment, creatableRoles, actionEnhancements, overridingRoles, redactions);
    }

    private CommentRepresentation getCommentRepresentation(User user, Comment comment, List<PrismRole> creatableRoles, List<PrismActionEnhancement> actionEnhancements,
            List<PrismRole> overridingRoles, Set<PrismActionRedactionType> redactions) {
        boolean onlyAsPartner = actionEnhancements.size() == 1 && actionEnhancements.contains(APPLICATION_VIEW_AS_PARTNER);
        if (!onlyAsPartner && (!overridingRoles.isEmpty() || redactions.isEmpty() || commentService.isCommentOwner(comment, user))) {
            CommentRepresentation representation = getCommentRepresentationExtended(comment);
            representation.setAssignedUsers(getCommentAssignedUserRepresentations(comment, creatableRoles));
            return representation;
        } else {
            CommentRepresentation representation = getCommentRepresentationSimple(comment);

            if (!onlyAsPartner && redactions.contains(ALL_ASSESSMENT_CONTENT)) {
                representation.setInterviewAppointment(getCommentInterviewAppointmentRepresentation(comment));
                representation.setInterviewInstruction(getCommentInterviewInstructionRepresentation(comment, false));
                representation.setAppointmentTimeslots(getCommentAppointmentTimeslotRepresentations(comment.getAppointmentTimeslots()));
            }

            return representation;
        }
    }

    private CommentRepresentation getCommentRepresentationSimple(Comment comment) {
        CommentRepresentation representation = new CommentRepresentation().withId(comment.getId()).withUser(userMapper.getUserRepresentationSimple(comment.getUser()))
                .withDelegateUser(getCommentDelegateUserRepresentation(comment)).withAction(comment.getAction().getId())
                .withDeclinedResponse(comment.getDeclinedResponse()).withCreatedTimestamp(comment.getCreatedTimestamp());

        if (comment.isApplicationCompleteComment()) {
            representation.setShared(comment.getShared());
            representation.setOnCourse(comment.getOnCourse());
        }

        return representation;
    }

    private CommentRepresentation getCommentRepresentationExtended(Comment comment) {
        return getCommentRepresentationSimple(comment).withContent(comment.getContent()).withState(comment.getState().getId())
                .withTransitionState(comment.getTransitionState().getId()).withRating(comment.getRating()).withEligible(comment.getEligible())
                .withInterested(comment.getInterested()).withInterviewAppointment(getCommentInterviewAppointmentRepresentation(comment))
                .withInterviewInstruction(getCommentInterviewInstructionRepresentation(comment, true)).withPositionDetail(getCommentPositionDetailRepresentation(comment))
                .withOfferDetail(getCommentOfferDetailRepresentation(comment)).withRecruiterAcceptAppointment(comment.getRecruiterAcceptAppointment())
                .withPartnerAcceptAppointment(comment.getPartnerAcceptAppointment()).withApplicantAcceptAppointment(comment.getApplicantAcceptAppointment())
                .withRejectionReason(comment.getRejectionReason()).withCompetences(getCommentCompetenceRepresentations(comment.getCompetences()))
                .withAppointmentTimeslots(getCommentAppointmentTimeslotRepresentations(comment.getAppointmentTimeslots()))
                .withAppointmentPreferences(getCommentAppointmentPreferenceRepresentations(comment)).withDocuments(getCommentDocumentRepresentations(comment));
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

    private UserRepresentationSimple getCommentDelegateUserRepresentation(Comment comment) {
        User delegate = comment.getDelegateUser();
        return delegate == null ? null : userMapper.getUserRepresentationSimple(delegate);
    }

    private List<CommentCompetenceRepresentation> getCommentCompetenceRepresentations(Set<CommentCompetence> commentCompetences) {
        List<CommentCompetenceRepresentation> representations = Lists.newLinkedList();
        for (CommentCompetence commentCompetence : commentCompetences) {
            Competence competence = commentCompetence.getCompetence();
            representations.add(new CommentCompetenceRepresentation().withName(competence.getName())
                    .withDescription(competence.getDescription()).withRating(commentCompetence.getRating())
                    .withRemark(commentCompetence.getRemark()));
        }
        return representations;
    }

    private CommentPositionDetailRepresentation getCommentPositionDetailRepresentation(Comment comment) {
        CommentPositionDetail position = comment.getPositionDetail();
        return position == null ? null : new CommentPositionDetailRepresentation().withPositionName(position.getPositionName()).withPositionDescription(
                position.getPositionDescription());
    }

    private CommentOfferDetailRepresentation getCommentOfferDetailRepresentation(Comment comment) {
        CommentOfferDetail offer = comment.getOfferDetail();
        return offer == null ? null : new CommentOfferDetailRepresentation().withPositionProvisionalStartDate(offer.getPositionProvisionalStartDate())
                .withAppointmentConditions(offer.getAppointmentConditions());
    }

    private List<LocalDateTime> getCommentAppointmentPreferenceRepresentations(Comment comment) {
        List<LocalDateTime> representations = Lists.newLinkedList();
        for (CommentAppointmentPreference preference : comment.getAppointmentPreferences()) {
            representations.add(preference.getDateTime());
        }
        return representations;
    }

    private List<DocumentRepresentation> getCommentDocumentRepresentations(Comment comment) {
        List<DocumentRepresentation> representations = Lists.newLinkedList();
        for (Document document : comment.getDocuments()) {
            representations.add(documentMapper.getDocumentRepresentation(document));
        }
        return representations;
    }

}
