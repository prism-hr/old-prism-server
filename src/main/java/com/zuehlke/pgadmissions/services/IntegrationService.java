package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType.ALL_ASSESSMENT_CONTENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.LocalDateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetence;
import com.zuehlke.pgadmissions.domain.advert.AdvertDepartment;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.advert.AdvertInstitution;
import com.zuehlke.pgadmissions.domain.advert.AdvertProgram;
import com.zuehlke.pgadmissions.domain.advert.AdvertSubjectArea;
import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.advert.AdvertTargets;
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
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionRedactionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedRejectionReason;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceCondition;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccountExternal;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.rest.representation.InstitutionDomicileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.StateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.TimelineRepresentation;
import com.zuehlke.pgadmissions.rest.representation.TimelineRepresentation.TimelineCommentGroupRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentAppointmentTimeslotRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentAssignedUserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentCustomResponseRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentExportRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewInstructionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentOfferDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentPositionDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation.SelectableStateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionAddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceConditionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceParentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceUserRolesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertCategoriesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertClosingDateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertCompetenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertFinancialDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertFinancialDetailsRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertTargetRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertTargetsRepresentation;
import com.zuehlke.pgadmissions.workflow.resource.representation.ResourceRepresentationEnricher;

@Service
@Transactional
public class IntegrationService {

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private CommentService commentService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Inject
    private ApplicationContext applicationContext;

    public <T extends Resource> ResourceRepresentationSimple getResourceRepresentationSimple(T resource) {
        ResourceRepresentationSimple representation = new ResourceRepresentationSimple().withId(resource.getId()).withCode(resource.getCode())
                .withImportedCode(resource.getCode()).withTitle(resource.getTitle());

        if (Institution.class.isAssignableFrom(resource.getClass())) {
            representation.setLogoImage(resource.getInstitution().getLogoImage().getId());
        }

        return representation;
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> ResourceRepresentationExtended getResourceRepresentationExtended(T resource) throws Exception {
        User currentUser = userService.getCurrentUser();

        ResourceRepresentationExtended representation = (ResourceRepresentationExtended) getResourceRepresentationSimple(resource);
        representation.setUser(getUserRepresentation(resource.getUser()));

        for (PrismScope parentScope : scopeService.getParentScopesDescending(resource.getResourceScope())) {
            String parentScopeReference = parentScope.getLowerCamelName();
            Resource parentResource = resource.getEnclosingResource(parentScope);
            if (parentResource != null) {
                setProperty(representation, parentScopeReference, getResourceRepresentationSimple(parentResource));
            }
        }

        representation.setState(getStateRepresentation(resource.getState()));
        representation.setPreviousState(getStateRepresentation(resource.getPreviousState()));
        representation.setSecondaryStates(getSecondaryStateRepresentations(resource));

        representation.setCreatedTimestamp(resource.getCreatedTimestamp());
        representation.setUpdatedTimestamp(resource.getUpdatedTimestamp());

        representation.setTimeline(getTimelineRepresentation(resource, currentUser));
        representation.setActions(getActionRepresentations(resource, currentUser));
        representation.setUserRoles(getResourceUserRoleRepresentations(resource));

        representation.setWorkflowConfigurations(resourceService.getWorkflowPropertyConfigurations(resource));
        representation.setConditions(getResourceConditionRepresentations(resource));

        Class<T> resourceClass = (Class<T>) resource.getClass();
        if (ResourceParent.class.isAssignableFrom(resourceClass)) {
            ResourceParent resourceParent = (ResourceParent) resource;
            ResourceParentRepresentation representationParent = (ResourceParentRepresentation) representation;
            representationParent.setAdvert(getAdvertRepresentation(resourceParent.getAdvert()));
            representationParent.setBackgroundImage(resourceService.getBackgroundImage(resourceParent));
            representationParent.setPartnerActions(actionService.getPartnerActions(resourceParent));

            if (ResourceOpportunity.class.isAssignableFrom(resourceClass)) {
                ResourceOpportunity resourceOpportunity = (ResourceOpportunity) resourceParent;
                ResourceOpportunityRepresentation representationOpportunity = (ResourceOpportunityRepresentation) representationParent;
                representationOpportunity.setStudyOptions(resourceService.getStudyOptions(resourceOpportunity));
                representationOpportunity.setStudyLocations(resourceService.getStudyLocations(resourceOpportunity));
            }
        }

        return representation;
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource, V extends ResourceRepresentationExtended> V getResourceClientRepresentation(T resource)
            throws Exception {
        V representation = (V) getResourceRepresentationExtended(resource);

        Class<? extends ResourceRepresentationEnricher<T, V>> enricher = (Class<? extends ResourceRepresentationEnricher<T, V>>) resource.getResourceScope()
                .getResourceRepresentationEnricher();
        if (enricher != null) {
            applicationContext.getBean(enricher).enrich(resource, (V) representation);
        }

        return representation;
    }

    public UserRepresentation getUserRepresentation(User user) {
        UserRepresentation representation = new UserRepresentation().withId(user.getId()).withFirstName(user.getFirstName())
                .withFirstName2(user.getFirstName2()).withFirstName3(user.getEmail());

        UserAccountExternal userAccountExternal = user.getUserAccount().getPrimaryExternalAccount();
        if (userAccountExternal != null) {
            representation.setAccountProfileUrl(userAccountExternal.getAccountProfileUrl());
            representation.setAccountImageUrl(userAccountExternal.getAccountImageUrl());
        }

        return representation;
    }

    public StateRepresentation getStateRepresentation(State state) {
        return new StateRepresentation().withState(state.getId()).withStateGroup(state.getStateGroup().getId());
    }

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

    public List<ActionRepresentation> getActionRepresentations(Resource resource, User user) {
        PrismScope scope = resource.getResourceScope();
        Integer resourceId = resource.getId();
        Integer systemId = resource.getSystem().getId();
        Integer institutionId = resourceService.getResourceId(resource.getInstitution());
        Integer programId = resourceService.getResourceId(resource.getProgram());
        Integer projectId = resourceService.getResourceId(resource.getProject());
        Integer applicationId = resourceService.getResourceId(resource.getApplication());

        Set<ActionRepresentation> representations = Sets.newLinkedHashSet();
        List<ActionDTO> actions = actionService.getPermittedActions(scope, resourceId, systemId, institutionId, programId, projectId,
                applicationId, user);
        for (ActionDTO action : actions) {
            representations.add(getResourceActionRepresentation(resource, action, user));
        }

        List<ActionDTO> publicActions = actionService.getPermittedUnsecuredActions(scope, Sets.newHashSet(resource.getId()), APPLICATION);
        for (ActionDTO publicAction : publicActions) {
            representations.add(getResourceActionRepresentation(resource, publicAction, user));
        }

        return Lists.newLinkedList(representations);
    }

    public ActionRepresentation getResourceActionRepresentation(Resource resource, ActionDTO action, User user) {
        PrismAction prismAction = action.getActionId();
        boolean primaryState = BooleanUtils.toBoolean(action.getPrimaryState());
        return new ActionRepresentation()
                .withId(prismAction)
                .withRaisesUrgentFlag(action.getRaisesUrgentFlag())
                .withPrimaryState(primaryState)
                .addActionEnhancements(actionService.getGlobalActionEnhancements(resource, prismAction, user))
                .addActionEnhancements(actionService.getCustomActionEnhancements(resource, prismAction, user))
                .addNextStates(
                        primaryState ? stateService.getSelectableTransitionStates(resource.getState(), prismAction, resource.getAdvert().isImported())
                                : Collections.<SelectableStateRepresentation> emptyList())
                .addRecommendedNextStates(stateService.getRecommendedNextStates(resource));
    }

    public List<ResourceUserRolesRepresentation> getResourceUserRoleRepresentations(Resource resource) {
        List<User> users = userService.getResourceUsers(resource);
        List<ResourceUserRolesRepresentation> resourceUserRolesRepresentations = Lists.newArrayListWithCapacity(users.size());
        for (User user : users) {
            resourceUserRolesRepresentations.add(getResourceUserRolesRepresentation(resource, user));
        }
        return resourceUserRolesRepresentations;
    }

    public ResourceUserRolesRepresentation getResourceUserRolesRepresentation(Resource resource, User user) {
        UserRepresentation userRepresentation = getUserRepresentation(user);
        ResourceUserRolesRepresentation resourceUserRolesRepresentation = new ResourceUserRolesRepresentation().withUser(userRepresentation).withRoles(
                roleService.getRolesForResource(resource, user));
        return resourceUserRolesRepresentation;
    }

    public AdvertRepresentation getAdvertRepresentation(Advert advert) {
        ResourceParent resource = advert.getResource();
        return new AdvertRepresentation().withId(advert.getId()).withUser(getUserRepresentation(resource.getUser()))
                .withResource(getResourceRepresentationSimple(resource)).withInstitution(getResourceRepresentationSimple(resource.getInstitution()))
                .withDepartment(getResourceDepartmentRepresentation(resource)).withOpportunityType(advert.getOpportunityType()).withTitle(advert.getTitle())
                .withSummary(advert.getSummary()).withDescription(advert.getDescription()).withHomepage(advert.getHomepage())
                .withApplyHomepage(advert.getApplyHomepage()).withTelephone(advert.getTelephone())
                .withAddress(getInstitutionAddressRepresentation(advert)).withFinancialDetails(getAdvertFinancialDetailsRepresentation(advert))
                .withClosingDate(getAdvertClosingDateReprentation(advert)).withClosingDates(getAdvertClosingDateReprentations(advert))
                .withCategories(getAdvertCategoriesRepresentation(advert)).withTargets(getAdvertTargetsRepresentation(advert))
                .withSequenceIdentifier(advert.getSequenceIdentifier());
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
                representation.setAppointmentTimeslots(getCommentAppointmentTimeslotRepresentations(comment));
            }

            return representation;
        }
    }

    private CommentRepresentation getCommentRepresentationSimple(Comment comment) {
        return new CommentRepresentation().withId(comment.getId()).withUser(getUserRepresentation(comment.getUser()))
                .withDelegateUser(getCommentDelegateUserRepresentation(comment)).withAction(comment.getAction().getId())
                .withDeclinedResponse(comment.getDeclinedResponse()).withCreatedTimestamp(comment.getCreatedTimestamp());
    }

    private CommentRepresentation getCommentRepresentationExtended(Comment comment) {
        return getCommentRepresentationSimple(comment).withContent(comment.getContent()).withState(comment.getState().getId())
                .withTransitionState(comment.getTransitionState().getId()).withApplicationEligible(comment.getApplicationEligible())
                .withApplicationInterested(comment.getApplicationInterested()).withInterviewAppointment(getCommentInterviewAppointmentRepresentation(comment))
                .withInterviewInstruction(getCommentInterviewInstructionRepresentation(comment, true))
                .withPositionDetail(getCommentPositionDetailRepresentation(comment)).withOfferDetail(getCommentOfferDetailRepresentation(comment))
                .withRecruiterAcceptAppointment(comment.getRecruiterAcceptAppointment()).withApplicationReserveStatus(comment.getApplicationReserveStatus())
                .withRejectionReason(getCommentRejectionReasonRepresentation(comment)).withRejectionReasonSystem(comment.getRejectionReasonSystem())
                .withApplicationRating(comment.getApplicationRating()).withExport(getCommentExportRepresentation(comment))
                .withAppointmentTimeslots(getCommentAppointmentTimeslotRepresentations(comment))
                .withAppointmentPreferences(getCommentAppointmentPreferenceRepresentations(comment)).withDocuments(getCommentDocumentRepresentations(comment))
                .withCustomResponses(getCommentCustomResponseRepresentations(comment));
    }

    private CommentAssignedUserRepresentation getCommentAssignedUserRepresentation(CommentAssignedUser commentAssignedUser) {
        return new CommentAssignedUserRepresentation().withUser(getUserRepresentation(commentAssignedUser.getUser()))
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

    private UserRepresentation getCommentDelegateUserRepresentation(Comment comment) {
        User delegate = comment.getDelegateUser();
        return delegate == null ? null : getUserRepresentation(delegate);
    }

    private CommentInterviewAppointmentRepresentation getCommentInterviewAppointmentRepresentation(Comment comment) {
        CommentInterviewAppointment appointment = comment.getInterviewAppointment();
        if (appointment != null) {
            return new CommentInterviewAppointmentRepresentation().withInterviewDateTime(appointment.getInterviewDateTime())
                    .withInterviewTimeZone(appointment.getInterviewTimeZone()).withInterviewDuration(appointment.getInterviewDuration());
        }
        return null;
    }

    private CommentInterviewInstructionRepresentation getCommentInterviewInstructionRepresentation(Comment comment, boolean interviewer) {
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

    private List<CommentAppointmentTimeslotRepresentation> getCommentAppointmentTimeslotRepresentations(Comment comment) {
        List<CommentAppointmentTimeslotRepresentation> timeslots = Lists.newLinkedList();
        for (CommentAppointmentTimeslot timeslot : comment.getAppointmentTimeslots()) {
            timeslots.add(new CommentAppointmentTimeslotRepresentation().withId(timeslot.getId()).withDateTime(timeslot.getDateTime()));
        }
        return timeslots;
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

    private String getCommentRejectionReasonRepresentation(Comment comment) {
        ImportedRejectionReason rejection = comment.getRejectionReason();
        return rejection == null ? null : rejection.getName();
    }

    private CommentExportRepresentation getCommentExportRepresentation(Comment comment) {
        CommentExport export = comment.getApplicationExport();
        return export == null ? null : new CommentExportRepresentation().withExportRequest(export.getExportRequest())
                .withExportException(export.getExportException()).withExportReference(export.getExportReference());
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
            representations.add(getDocumentRepresentation(document));
        }
        return representations;
    }

    private DocumentRepresentation getDocumentRepresentation(Document document) {
        return new DocumentRepresentation().withId(document.getId()).withFileName(document.getFileName());
    }

    private List<CommentCustomResponseRepresentation> getCommentCustomResponseRepresentations(Comment comment) {
        List<CommentCustomResponseRepresentation> representations = Lists.newLinkedList();
        for (CommentCustomResponse response : comment.getCustomResponses()) {
            representations.add(new CommentCustomResponseRepresentation().withLabel(response.getActionCustomQuestionConfiguration().getLabel())
                    .withPropertyValue(response.getPropertyValue()));
        }
        return representations;
    }

    private List<ResourceConditionRepresentation> getResourceConditionRepresentations(Resource resource) {
        List<ResourceConditionRepresentation> representations = Lists.newLinkedList();
        for (ResourceCondition condition : resource.getResourceConditions()) {
            representations.add(new ResourceConditionRepresentation().withActionCondition(condition.getActionCondition()).withPartnerMode(
                    condition.getPartnerMode()));
        }
        return representations;
    }

    private List<StateRepresentation> getSecondaryStateRepresentations(Resource resource) {
        List<StateRepresentation> secondaryStates = Lists.newLinkedList();
        for (State state : stateService.getSecondaryResourceStates(resource)) {
            secondaryStates.add(getStateRepresentation(state));
        }
        return secondaryStates;
    }

    private ResourceRepresentationSimple getResourceDepartmentRepresentation(Resource resource) {
        Department department = resource.getDepartment();
        return (department == null || department.sameAs(resource)) ? null : getResourceRepresentationSimple(department);
    }

    private InstitutionAddressRepresentation getInstitutionAddressRepresentation(Advert advert) {
        InstitutionAddress address = advert.getAddress();
        if (address != null) {
            InstitutionAddressRepresentation representation = new InstitutionAddressRepresentation()
                    .withDomicile(getInstitutionDomicileRepresentation(address.getDomicile())).withAddressLine1(address.getAddressLine1())
                    .withAddressLine2(address.getAddressLine2()).withAddressTown(address.getAddressTown()).withAddressRegion(address.getAddressRegion())
                    .withAddressCode(address.getAddressCode()).withGoogleId(address.getGoogleId());

            GeographicLocation location = address.getLocation();
            if (location != null) {
                representation.setLocationX(location.getLocationX());
                representation.setLocationY(location.getLocationY());
            }

            representation.setLocationString(address.getLocationString());
            return representation;
        }

        return null;
    }

    private InstitutionDomicileRepresentation getInstitutionDomicileRepresentation(InstitutionDomicile domicile) {
        return new InstitutionDomicileRepresentation().withId(domicile.getId()).withName(domicile.getName()).withCurrency(domicile.getCurrency());
    }

    private AdvertFinancialDetailsRepresentation getAdvertFinancialDetailsRepresentation(Advert advert) {
        AdvertFinancialDetail fee = advert.getFee();
        AdvertFinancialDetail pay = advert.getPay();
        if (!(fee == null && pay == null)) {
            return new AdvertFinancialDetailsRepresentation().withFee(getAdvertFinancialDetailRepresentation(fee)).withPay(
                    getAdvertFinancialDetailRepresentation(pay));
        }
        return null;
    }

    private AdvertFinancialDetailRepresentation getAdvertFinancialDetailRepresentation(AdvertFinancialDetail detail) {
        if (detail != null) {
            PrismDurationUnit durationUnit = detail.getInterval();
            AdvertFinancialDetailRepresentation representation = new AdvertFinancialDetailRepresentation().withCurrency(detail.getCurrencySpecified())
                    .withInterval(detail.getInterval());
            if (durationUnit.equals(YEAR)) {
                representation.setMinimum(detail.getYearMinimumSpecified());
                representation.setMaximum(detail.getYearMaximumSpecified());
            } else {
                representation.setMinimum(detail.getMonthMinimumSpecified());
                representation.setMaximum(detail.getMonthMaximumSpecified());
            }
            return representation;
        }
        return null;
    }

    private AdvertClosingDateRepresentation getAdvertClosingDateReprentation(Advert advert) {
        AdvertClosingDate closingDate = advert.getClosingDate();
        return closingDate == null ? null : getAdvertClosingDateRepresentation(closingDate);
    }

    private List<AdvertClosingDateRepresentation> getAdvertClosingDateReprentations(Advert advert) {
        List<AdvertClosingDateRepresentation> representations = Lists.newLinkedList();
        for (AdvertClosingDate closingDate : advert.getClosingDates()) {
            representations.add(getAdvertClosingDateRepresentation(closingDate));
        }
        return representations;
    }

    private AdvertClosingDateRepresentation getAdvertClosingDateRepresentation(AdvertClosingDate closingDate) {
        return new AdvertClosingDateRepresentation().withId(closingDate.getId()).withClosingDate(closingDate.getClosingDate());
    }

    private AdvertCategoriesRepresentation getAdvertCategoriesRepresentation(Advert advert) {
        AdvertCategories categories = advertService.getAdvertCategories(advert);
        return categories == null ? null : new AdvertCategoriesRepresentation().withIndustries(advertService.getAdvertIndustries(advert))
                .withFunctions(advertService.getAdvertFunctions(advert)).withThemes(advertService.getAdvertThemes(advert));
    }

    private AdvertTargetsRepresentation getAdvertTargetsRepresentation(Advert advert) {
        AdvertTargets targets = advertService.getAdvertTargets(advert);
        return targets == null ? null : new AdvertTargetsRepresentation().withCompetences(getAdvertCompetencesRepresentation(advert)).withInstitutions(
                getAdvertTargetsRepresentation(advert, AdvertInstitution.class))
                .withDepartments(getAdvertTargetsRepresentation(advert, AdvertDepartment.class))
                .withPrograms(getAdvertTargetsRepresentation(advert, AdvertProgram.class))
                .withSubjectAreas(getAdvertTargetsRepresentation(advert, AdvertSubjectArea.class));
    }

    private List<AdvertTargetRepresentation> getAdvertTargetsRepresentation(Advert advert, Class<? extends AdvertTarget<?>> targetClass) {
        List<AdvertTargetRepresentation> representations = Lists.newLinkedList();
        for (AdvertTarget<?> target : advertService.getAdvertTargets(advert, targetClass)) {
            representations.add(new AdvertTargetRepresentation().withId(target.getId()).withTitle(target.getTitle()).withImportance(target.getImportance()));
        }
        return representations;
    }

    private List<AdvertCompetenceRepresentation> getAdvertCompetencesRepresentation(Advert advert) {
        List<AdvertCompetenceRepresentation> representations = Lists.newLinkedList();
        for (AdvertTarget<?> target : advertService.getAdvertTargets(advert, AdvertCompetence.class)) {
            AdvertCompetence competence = (AdvertCompetence) target;
            representations.add(new AdvertCompetenceRepresentation().withId(competence.getId()).withTitle(competence.getTitle())
                    .withDescription(competence.getCompetence().getDescription()).withImportance(competence.getImportance()));
        }
        return representations;
    }

}
