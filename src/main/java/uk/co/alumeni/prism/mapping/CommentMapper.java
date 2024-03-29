package uk.co.alumeni.prism.mapping;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.domain.Competence;
import uk.co.alumeni.prism.domain.comment.*;
import uk.co.alumeni.prism.domain.definitions.workflow.*;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.*;
import uk.co.alumeni.prism.rest.representation.comment.CommentTimelineRepresentation.CommentGroupRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newTreeSet;
import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static uk.co.alumeni.prism.PrismConstants.RATING_PRECISION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_PARTNER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionRedactionType.ALL_ASSESSMENT_CONTENT;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.setProperty;

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
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    public CommentTimelineRepresentation getCommentTimelineRepresentation(Resource resource) {
        User currentUser = userService.getCurrentUser();
        resourceService.validateViewResource(resource, currentUser);

        List<Comment> comments = commentService.getTimelineComments(resource);
        List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(resource, currentUser);

        CommentTimelineRepresentation timelineRepresentation = new CommentTimelineRepresentation();
        if (!comments.isEmpty()) {
            CommentGroupRepresentation groupRepresentation = null;

            List<PrismRole> creatableRoles = roleService.getCreatableRoles(resource.getResourceScope());
            List<PrismActionEnhancement> actionEnhancements = actionService.getPermittedActionEnhancements(currentUser, resource);
            HashMultimap<PrismAction, PrismActionRedactionType> redactions = actionService.getRedactions(resource, currentUser, overridingRoles);
            for (Comment comment : commentService.getTimelineComments(resource)) {
                Set<PrismActionRedactionType> actionRedactions = redactions.get(comment.getAction().getId());
                if (groupRepresentation == null) {
                    groupRepresentation = new CommentGroupRepresentation().withStateGroup(comment.getTransitionState().getStateGroup().getId());
                    groupRepresentation.addComment(getCommentRepresentation(currentUser, comment, creatableRoles, actionEnhancements, overridingRoles,
                            actionRedactions));
                    timelineRepresentation.addCommentGroup(groupRepresentation);
                } else if (comment.isStateGroupTransitionComment() && !comment.isSecondaryStateGroupTransitionComment()) {
                    groupRepresentation.addComment(getCommentRepresentation(currentUser, comment, creatableRoles, actionEnhancements, overridingRoles,
                            actionRedactions));
                    groupRepresentation = new CommentGroupRepresentation().withStateGroup(comment.getTransitionState().getStateGroup().getId());
                    timelineRepresentation.addCommentGroup(groupRepresentation);
                } else {
                    groupRepresentation.addComment(getCommentRepresentation(currentUser, comment, creatableRoles, actionEnhancements, overridingRoles,
                            actionRedactions));
                }
            }
        }

        return timelineRepresentation;
    }

    public List<CommentAppointmentTimeslotRepresentation> getCommentAppointmentTimeslotRepresentations(Set<CommentAppointmentTimeslot> timeslots) {
        return timeslots.stream()
                .map(timeslot -> new CommentAppointmentTimeslotRepresentation().withId(timeslot.getId()).withDateTime(timeslot.getDateTime()))
                .collect(Collectors.toList());
    }

    public List<CommentAppointmentPreferenceRepresentation> getCommentAppointmentPreferenceRepresentations(Comment schedulingComment,
            Set<CommentAppointmentTimeslot> timeslots) {
        List<CommentAppointmentPreferenceRepresentation> representations = Lists.newLinkedList();

        User currentUser = userService.getCurrentUser();
        for (User user : commentService.getAppointmentInvitees(schedulingComment)) {
            CommentAppointmentPreferenceRepresentation representation = new CommentAppointmentPreferenceRepresentation()
                    .withUser(userMapper.getUserRepresentationSimple(user, currentUser));

            Comment preferenceComment = commentService.getLatestAppointmentPreferenceComment(schedulingComment.getApplication(), schedulingComment, user);
            if (preferenceComment != null) {
                List<LocalDateTime> preferences = commentService.getAppointmentPreferences(preferenceComment);

                List<Integer> inviteePreferences = timeslots.stream()
                        .filter(timeslot -> preferences.contains(timeslot.getDateTime()))
                        .map(CommentAppointmentTimeslot::getId).collect(Collectors.toList());
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

    public CommentRepresentation getCommentRepresentation(User user, Comment comment, List<PrismRole> creatableRoles, List<PrismRole> overridingRoles) {
        Resource resource = comment.getResource();
        List<PrismActionEnhancement> actionEnhancements = actionService.getPermittedActionEnhancements(user, resource);
        Set<PrismActionRedactionType> redactions = actionService.getRedactions(resource, user, overridingRoles).get(comment.getAction().getId());
        return getCommentRepresentation(user, comment, creatableRoles, actionEnhancements, overridingRoles, redactions);
    }

    public CommentRepresentation getCommentRepresentationExtended(Comment comment, List<PrismRole> creatableRoles) {
        State state = comment.getState();
        State transitionState = comment.getTransitionState();

        CommentRepresentation representation = getCommentRepresentationSimple(comment).withContent(comment.getContent())
                .withState(state == null ? null : state.getId()).withTransitionState(transitionState == null ? null : transitionState.getId())
                .withEligible(comment.getEligible()).withApplicantKnown(comment.getApplicantKnown())
                .withApplicantKnownDuration(comment.getApplicantKnownDuration()).withApplicantKnownCapacity(comment.getApplicantKnownCapacity())
                .withRating(comment.getRating()).withInterested(comment.getInterested()).withInterviewState(comment.getInterviewState())
                .withInterviewAppointment(getCommentInterviewAppointmentRepresentation(comment))
                .withInterviewInstruction(getCommentInterviewInstructionRepresentation(comment, true)).withInterviewAvailable(comment.getInterviewAvailable())
                .withPositionDetail(getCommentPositionDetailRepresentation(comment)).withOfferDetail(getCommentOfferDetailRepresentation(comment))
                .withRecruiterAcceptAppointment(comment.getRecruiterAcceptAppointment()).withPartnerAcceptAppointment(comment.getPartnerAcceptAppointment())
                .withApplicantAcceptAppointment(comment.getApplicantAcceptAppointment()).withRejectionReason(comment.getRejectionReason())
                .withCompetenceGroups(getCommentCompetenceRepresentations(comment.getCompetences()))
                .withAppointmentTimeslots(getCommentAppointmentTimeslotRepresentations(comment.getAppointmentTimeslots()))
                .withAppointmentPreferences(getCommentAppointmentPreferenceRepresentations(comment)).withDocuments(getCommentDocumentRepresentations(comment));

        representation.setAssignedUsers(comment.getAssignedUsers().stream()
                .filter(commentAssignedUser -> creatableRoles.contains(commentAssignedUser.getRole().getId()))
                .map(this::getCommentAssignedUserRepresentation)
                .collect(Collectors.toList()));

        return representation;
    }

    public List<CommentRepresentationRatingSummary> getRatingCommentSummaryRepresentations(User user, PrismScope scope,
            List<CommentRepresentation> commentList) {
        Map<PrismAction, CommentRepresentationRatingSummary> representations = newLinkedHashMap();

        LinkedHashMultimap<PrismAction, CommentRepresentation> commentIndex = LinkedHashMultimap.create();
        if (isNotEmpty(commentList)) {
            commentList.stream().forEach(comment -> {
                PrismAction delegatedAction = comment.getDelegatedAction();
                commentIndex.put(delegatedAction == null ? comment.getAction() : delegatedAction, comment);
            });
        }

        actionService.getRatingActions(scope).stream().forEach(action -> {
            Action delegatedAction = action.getDelegatedAction();
            PrismAction prismAction = delegatedAction == null ? action.getId() : delegatedAction.getId();

            CommentRepresentationRatingSummary representation = representations.get(prismAction);
            if (representation == null) {
                representation = new CommentRepresentationRatingSummary().withId(prismAction);
                representations.put(prismAction, representation);
            }

            Set<CommentRepresentation> ratingComments = commentIndex.get(prismAction);
            if (isNotEmpty(ratingComments)) {
                for (CommentRepresentation ratingComment : ratingComments) {
                    if (isTrue(ratingComment.getDeclinedResponse())) {
                        setRatingCommentSummaryCount(representation, "declinedCount");
                    } else {
                        setRatingCommentSummaryCount(representation, "providedCount");

                        BigDecimal rating = ratingComment.getRating();
                        if (rating != null) {
                            BigDecimal ratingAverage = representation.getRatingAverage();
                            representation.setRatingAverage(ratingAverage.add(rating)
                                    .divide(new BigDecimal(representation.getProvidedCount()), RATING_PRECISION, HALF_UP));
                        }
                    }
                }
            }
        });

        return newLinkedList(newTreeSet(representations.values()));
    }

    private void setRatingCommentSummaryCount(CommentRepresentationRatingSummary commentRepresentation, String countProperty) {
        Integer count = (Integer) getProperty(commentRepresentation, countProperty);
        setProperty(commentRepresentation, countProperty, count == null ? 1 : (count + 1));
    }

    private CommentRepresentation getCommentRepresentation(User user, Comment comment, List<PrismRole> creatableRoles,
            List<PrismActionEnhancement> actionEnhancements, List<PrismRole> overridingRoles, Set<PrismActionRedactionType> redactions) {
        boolean onlyAsPartner = actionEnhancements.size() == 1 && actionEnhancements.contains(APPLICATION_VIEW_AS_PARTNER);
        if (!onlyAsPartner && (!overridingRoles.isEmpty() || redactions.isEmpty() || commentService.isCommentOwner(comment, user))) {
            return getCommentRepresentationExtended(comment, creatableRoles);
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
        Action action = comment.getAction();
        Action delegatedAction = action.getDelegatedAction();

        CommentRepresentation representation = new CommentRepresentation().withId(comment.getId())
                .withUser(userMapper.getUserRepresentationSimple(comment.getUser(), userService.getCurrentUser()))
                .withDelegateUser(getCommentDelegateUserRepresentation(comment)).withAction(action.getId())
                .withDelegatedAction(delegatedAction == null ? null : delegatedAction.getId())
                .withDeclinedResponse(comment.getDeclinedResponse()).withCreatedTimestamp(comment.getCreatedTimestamp())
                .withSubmittedTimestamp(comment.getSubmittedTimestamp());

        if (comment.isApplicationCompleteComment()) {
            representation.setShared(comment.getShared());
            representation.setOnCourse(comment.getOnCourse());
        }

        return representation;
    }

    private CommentAssignedUserRepresentation getCommentAssignedUserRepresentation(CommentAssignedUser commentAssignedUser) {
        return new CommentAssignedUserRepresentation()
                .withUser(userMapper.getUserRepresentationSimple(commentAssignedUser.getUser(), userService.getCurrentUser()))
                .withRole(commentAssignedUser.getRole().getId()).withRoleTransitionType(commentAssignedUser.getRoleTransitionType());
    }

    private UserRepresentationSimple getCommentDelegateUserRepresentation(Comment comment) {
        User delegate = comment.getDelegateUser();
        return delegate == null ? null : userMapper.getUserRepresentationSimple(delegate, userService.getCurrentUser());
    }

    private List<CommentCompetenceGroupRepresentation> getCommentCompetenceRepresentations(Set<CommentCompetence> commentCompetences) {
        Map<Integer, CommentCompetenceGroupRepresentation> groups = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            groups.put(i, new CommentCompetenceGroupRepresentation().withImportance(i).withCompetences(new LinkedList<>()));
        }

        for (CommentCompetence commentCompetence : commentCompetences) {
            Competence competence = commentCompetence.getCompetence();
            Integer rating = null;
            Boolean fulfil = commentCompetence.getFulfil();
            if (fulfil == null) {
                rating = commentCompetence.getRating();
            }

            CommentCompetenceGroupRepresentation group = groups.get(commentCompetence.getImportance());
            group.getCompetences().add(new CommentCompetenceRepresentation().withCompetenceId(competence.getId()).withName(competence.getName())
                    .withDescription(competence.getDescription()).withFulfil(fulfil).withRating(rating).withRemark(commentCompetence.getRemark()));
        }
        return Stream.of(3, 2, 1)
                .map(groups::get)
                .filter(group -> !group.getCompetences().isEmpty())
                .collect(Collectors.toList());
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
        return comment.getAppointmentPreferences().stream()
                .map(CommentAppointmentPreference::getDateTime)
                .collect(Collectors.toList());
    }

    private List<DocumentRepresentation> getCommentDocumentRepresentations(Comment comment) {
        return comment.getDocuments().stream()
                .map(documentMapper::getDocumentRepresentation)
                .collect(Collectors.toList());
    }

}
