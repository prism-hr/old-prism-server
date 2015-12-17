package uk.co.alumeni.prism.mapping;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.ActionDTO;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.dto.ResourceListRowDTO;
import uk.co.alumeni.prism.rest.representation.action.ActionOutcomeReplicableRepresentation;
import uk.co.alumeni.prism.rest.representation.action.ActionOutcomeRepresentation;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentation;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentationSimple;
import uk.co.alumeni.prism.services.ActionService;

@Service
@Transactional
public class ActionMapper {

    @Inject
    private CommentMapper commentMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private StateMapper stateMapper;

    @Inject
    private ActionService actionService;

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
        Map<PrismAction, ActionRepresentationExtended> representations = Maps.newLinkedHashMap();

        boolean onlyAsPartner = true;
        List<ActionDTO> actions = actionService.getPermittedActions(user, resource);
        for (ActionDTO action : actions) {
            onlyAsPartner = !onlyAsPartner ? false : isTrue(action.getOnlyAsPartner());
            representations.put(action.getActionId(), getActionRepresentationExtended(resource, action, user));
        }

        actionService.getPermittedActionEnhancements(user, resource, actions.stream().map(a -> a.getActionId()).collect(toList()))
                .forEach(ae -> representations.get(ae.getAction()).addActionEnhancement(ae.getActionEnhancement()));

        List<ActionDTO> publicActions = actionService.getPermittedUnsecuredActions(scope, asList(resource.getId()));
        for (ActionDTO publicAction : publicActions) {
            boolean applicationAction = publicAction.getActionId().name().endsWith("_CREATE_APPLICATION");
            if (!onlyAsPartner || applicationAction) {
                Advert advert = resource.getAdvert();
                ActionRepresentationExtended actionRepresentation = getActionRepresentationExtended(resource, publicAction, user);

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
        ActionOutcomeRepresentation representation = new ActionOutcomeRepresentation().withResource(resourceMapper.getResourceRepresentationSimple(actionOutcomeDTO.getResource()))
                .withTransitionResource(resourceMapper.getResourceRepresentationSimple(actionOutcomeDTO.getTransitionResource()))
                .withTransitionAction(actionOutcomeDTO.getTransitionAction().getId());

        List<Comment> replicableSequenceComments = actionOutcomeDTO.getReplicableSequenceComments();
        if (CollectionUtils.isNotEmpty(replicableSequenceComments)) {
            representation.setReplicable(new ActionOutcomeReplicableRepresentation()
                    .withParentResource(resourceMapper.getResourceRepresentationSimple(actionOutcomeDTO.getTransitionResource().getParentResource()))
                    .withSequenceComments(replicableSequenceComments.stream().map(commentMapper::getCommentRepresentationExtended).collect(Collectors.toList())));
        }

        return representation;
    }

    private ActionRepresentationExtended getActionRepresentationExtended(Resource resource, ActionDTO action, User user) {
        return getActionRepresentationSimple(action, ActionRepresentationExtended.class).addNextStates(stateMapper.getStateRepresentations(resource, action.getActionId()))
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
