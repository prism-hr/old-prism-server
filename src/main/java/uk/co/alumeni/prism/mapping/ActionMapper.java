package uk.co.alumeni.prism.mapping;

import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.ActionDTO;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.dto.ResourceListRowDTO;
import uk.co.alumeni.prism.rest.representation.action.*;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.ResourceListFilterService;
import uk.co.alumeni.prism.services.RoleService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;

@Service
@Transactional
public class ActionMapper {

    @Inject
    private ActionService actionService;

    @Inject
    private CommentMapper commentMapper;

    @Inject
    private CommentService commentService;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private ResourceListFilterService resourceListFilterService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateMapper stateMapper;

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

        if (representations.size() > 0) {
            List<PrismRole> creatableRoles = roleService.getCreatableRoles(resource.getResourceScope());
            Map<PrismAction, Comment> unsubmittedComments = commentService.getUnsubmittedComments(resource, representations.keySet(), user);
            representations.keySet().stream().forEach(
                    prismAction -> {
                        if (unsubmittedComments.containsKey(prismAction)) {
                            representations.get(prismAction).setComment(
                                    commentMapper.getCommentRepresentationExtended(unsubmittedComments.get(prismAction), creatableRoles));
                        }
                    });
        }

        return newLinkedList(representations.values());
    }

    public ActionOutcomeRepresentation getActionOutcomeRepresentation(ActionOutcomeDTO actionOutcomeDTO) {
        ActionOutcomeRepresentation representation = new ActionOutcomeRepresentation()
                .withResource(resourceMapper.getResourceRepresentationSimple(actionOutcomeDTO.getResource()))
                .withTransitionResource(resourceMapper.getResourceRepresentationSimple(actionOutcomeDTO.getTransitionResource()))
                .withAction(actionOutcomeDTO.getAction().getId()).withTransitionAction(actionOutcomeDTO.getTransitionAction().getId());

        List<Comment> replicableSequenceComments = actionOutcomeDTO.getReplicableSequenceComments();
        if (isNotEmpty(replicableSequenceComments)) {
            Resource resource = replicableSequenceComments.get(0).getResource();
            List<PrismRole> creatableRoles = roleService.getCreatableRoles(resource.getResourceScope());
            representation.setReplicable(new ActionOutcomeReplicableRepresentation().withFilter( //
                    resourceListFilterService.getReplicableActionFilter(resource, actionOutcomeDTO.getStateTransition(),
                            replicableSequenceComments.stream().map(comment -> comment.getAction().getId()).collect(toList()), true))
                    .withSequenceComments(replicableSequenceComments.stream()
                            .map(comment -> commentMapper.getCommentRepresentationExtended(comment, creatableRoles)).collect(toList())));
        }

        return representation;
    }

    private ActionRepresentationExtended getActionRepresentationExtended(Resource resource, User user, ActionDTO actionDTO) {
        return getActionRepresentationSimple(actionDTO, ActionRepresentationExtended.class) //
                .addNextStates(stateMapper.getStateRepresentations(resource, actionDTO.getActionId())) //
                .addRecommendedNextStates(stateMapper.getRecommendedNextStateRepresentations(resource));
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
