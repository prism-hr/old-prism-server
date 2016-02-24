package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isFalse;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.MESSAGE_RESOURCE;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.ActionDTO;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.dto.ResourceListRowDTO;
import uk.co.alumeni.prism.dto.UserRoleDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.rest.representation.action.ActionOutcomeReplicableRepresentation;
import uk.co.alumeni.prism.rest.representation.action.ActionOutcomeRepresentation;
import uk.co.alumeni.prism.rest.representation.action.ActionRecipientRepresentation;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentation;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.comment.CommentRepresentation;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.ResourceListFilterService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.StateService;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;

@Service
@Transactional
public class ActionMapper {

    @Inject
    private CommentMapper commentMapper;

    @Inject
    private CommentService commentService;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private StateMapper stateMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceListFilterService resourceListFilterService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    public ActionRepresentation getActionRepresentation(PrismAction action) {
        return getActionRepresentation(action, ActionRepresentation.class);
    }

    public List<ActionRepresentationSimple> getActionRepresentations(ResourceListRowDTO resourceRow) {
        List<ActionRepresentationSimple> representations = Lists.newLinkedList();
        for (ActionDTO actionDTO : resourceRow.getActions()) {
            ActionRepresentationSimple action = getActionRepresentationSimple(actionDTO, ActionRepresentationSimple.class);

            String applyHomepage = resourceRow.getApplyHomepage();
            if (applyHomepage != null && actionDTO.getActionId().name().endsWith("_CREATE_APPLICATION")) {
                action.setRedirectLink(applyHomepage);
            }

            representations.add(action);
        }
        return representations;
    }

    public List<ActionRepresentationExtended> getActionRepresentations(Resource resource, User user) {
        PrismScope scope = resource.getResourceScope();
        Map<PrismAction, ActionRepresentationExtended> representations = newLinkedHashMap();

        boolean onlyAsPartner = true;
        List<ActionDTO> actions = actionService.getPermittedActions(user, resource);
        for (ActionDTO action : actions) {
            onlyAsPartner = onlyAsPartner && isTrue(action.getOnlyAsPartner());
            representations.put(action.getActionId(), getActionRepresentationExtended(resource, user, action));
        }

        List<PrismRole> creatableRoles = roleService.getCreatableRoles(resource.getResourceScope());

        if (isNotEmpty(actions)) {
            Map<PrismAction, Comment> unsubmittedComments = commentService.getUnsubmittedComments(resource, representations.keySet(), user);
            representations.keySet().stream().forEach(prismAction -> {
                if (unsubmittedComments.containsKey(prismAction)) {
                    Comment comment = unsubmittedComments.get(prismAction);
                    CommentRepresentation commentRepresentation = commentMapper.getCommentRepresentationExtended(comment);
                    commentRepresentation.setAssignedUsers(commentMapper.getCommentAssignedUserRepresentations(comment, creatableRoles));
                    representations.get(prismAction).setComment(commentRepresentation);
                }
            });
        }

        actionService.getPermittedActionEnhancements(user, resource, actions.stream().map(ActionDTO::getActionId).collect(toList()))
                .forEach(actionEnancement -> representations.get(actionEnancement.getAction()).addActionEnhancement(actionEnancement.getActionEnhancement()));

        List<ActionDTO> publicActions = actionService.getPermittedUnsecuredActions(scope, Collections.singletonList(resource.getId()));
        for (ActionDTO publicAction : publicActions) {
            boolean applicationAction = publicAction.getActionId().name().endsWith("_CREATE_APPLICATION");
            if (!onlyAsPartner || applicationAction) {
                Advert advert = resource.getAdvert();
                ActionRepresentationExtended actionRepresentation = getActionRepresentationExtended(resource, user, publicAction);

                if (advert != null) {
                    String applyHomepage = advert.getApplyHomepage();
                    if (applyHomepage != null && applicationAction) {
                        actionRepresentation.setRedirectLink(applyHomepage);
                    }
                }

                representations.put(publicAction.getActionId(), actionRepresentation);
            }
        }

        return Lists.newLinkedList(representations.values());
    }

    public ActionOutcomeRepresentation getActionOutcomeRepresentation(ActionOutcomeDTO actionOutcomeDTO) {
        ActionOutcomeRepresentation representation = new ActionOutcomeRepresentation()
                .withResource(resourceMapper.getResourceRepresentationSimple(actionOutcomeDTO.getResource()))
                .withTransitionResource(resourceMapper.getResourceRepresentationSimple(actionOutcomeDTO.getTransitionResource()))
                .withTransitionAction(actionOutcomeDTO.getTransitionAction().getId());

        List<Comment> replicableSequenceComments = actionOutcomeDTO.getReplicableSequenceComments();
        if (isNotEmpty(replicableSequenceComments)) {
            representation.setReplicable(new ActionOutcomeReplicableRepresentation().withFilter( //
                    resourceListFilterService.getReplicableActionFilter(actionOutcomeDTO.getTransitionResource(),
                            actionOutcomeDTO.getStateTransition(),
                            replicableSequenceComments.stream().map(comment -> comment.getAction().getId()).collect(toList()), true))
                    .withSequenceComments(replicableSequenceComments.stream().map(commentMapper::getCommentRepresentationExtended).collect(toList())));
        }

        return representation;
    }

    private ActionRepresentationExtended getActionRepresentationExtended(Resource resource, User user, ActionDTO actionDTO) {
        ActionRepresentationExtended representation = getActionRepresentationSimple(actionDTO, ActionRepresentationExtended.class) //
                .addNextStates(stateMapper.getStateRepresentations(resource, actionDTO.getActionId())) //
                .addRecommendedNextStates(stateMapper.getRecommendedNextStateRepresentations(resource));

        if (actionDTO.getActionId().getActionCategory().equals(MESSAGE_RESOURCE)) {
            List<PrismRole> recipientRoles = newLinkedList();
            List<PrismRole> partnerRecipientRoles = newLinkedList();
            
            Action action = actionService.getById(actionDTO.getActionId());
            List<Integer> stateActionAssignments = stateService.getStateActionAssignments(user, resource, action);
            if (!stateActionAssignments.isEmpty()) {
                stateService.getStateActionRecipients(stateActionAssignments).stream().forEach(stateActionRecipient -> {
                    if (isFalse(stateActionRecipient.getExternalMode())) {
                        recipientRoles.add(stateActionRecipient.getRole());
                    } else {
                        partnerRecipientRoles.add(stateActionRecipient.getRole());
                    }
                });

                if (!recipientRoles.isEmpty()) {
                    List<UserRoleDTO> recipientUserRoles = roleService.getUserRoles(resource, recipientRoles);
                    representation.addRecipients(getActionRecipientRepresentations(user, recipientUserRoles));
                }

                if (!partnerRecipientRoles.isEmpty()) {
                    Map<Integer, Advert> resourceAdverts = newHashMap();
                    resource.getAdvert().getEnclosingResources().stream().forEach(enclosingResource -> {
                        Advert enclosingAdvert = enclosingResource.getAdvert();
                        resourceAdverts.put(enclosingAdvert.getId(), enclosingAdvert);
                    });

                    Map<ResourceDTO, Resource> targetingResources = newHashMap();
                    advertService.getTargetingAdverts(resourceAdverts.values()).stream().forEach(targetingAdvert -> {
                        targetingAdvert.getEnclosingResources().stream().forEach(targetingResource -> {
                            ResourceDTO targetingResourceDTO = new ResourceDTO().withScope(targetingResource.getResourceScope()).withId(targetingResource.getId());
                            targetingResources.put(targetingResourceDTO, targetingResource);
                        });
                    });

                    if (!targetingResources.isEmpty()) {
                        List<UserRoleDTO> recipientPartnerUserRoles = roleService.getUserRoles(targetingResources.values(), partnerRecipientRoles);
                        representation.addPartnerRecipients(getActionRecipientRepresentations(user, recipientPartnerUserRoles));
                    }
                }
            }
            }

        return representation;
    }

    private List<ActionRecipientRepresentation> getActionRecipientRepresentations(User user, List<UserRoleDTO> recipientUserRoles) {
        LinkedHashMultimap<PrismRole, User> index = LinkedHashMultimap.create();
        recipientUserRoles.stream().forEach(userRole -> index.put(userRole.getRole(), userRole.getUser()));

        List<ActionRecipientRepresentation> recipients = newLinkedList();
        index.keySet().stream().forEach(key -> {
            recipients.add(new ActionRecipientRepresentation().withRole(key).withUsers(
                    index.get(key).stream().map(value -> userMapper.getUserRepresentationSimple(value, user)).collect(toList())));
        });
        return recipients;
    }

    private <T extends ActionRepresentationSimple> T getActionRepresentationSimple(ActionDTO action, Class<T> returnType) {
        PrismAction prismAction = action.getActionId();
        T representation = getActionRepresentation(prismAction, returnType);

        representation.setId(action.getActionId());
        representation.setRaisesUrgentFlag(action.getRaisesUrgentFlag());
        representation.setPrimaryState(action.getPrimaryState());
        representation.setDeclinable(action.getDeclinable());

        return representation;
    }

    private <T extends ActionRepresentation> T getActionRepresentation(PrismAction action, Class<T> returnType) {
        T representation = BeanUtils.instantiate(returnType);
        representation.setId(action);
        representation.setCategory(action.getActionCategory());
        representation.setDeclinable(action.isDeclinableAction());
        return representation;
    }

}
