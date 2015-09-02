package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.representation.action.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationSimple;
import com.zuehlke.pgadmissions.services.ActionService;

@Service
@Transactional
public class ActionMapper {

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private StateMapper stateMapper;

    @Inject
    private ActionService actionService;

    public ActionRepresentation getActionRepresentation(PrismAction action) {
        return getActionRepresentation(action, ActionRepresentation.class);
    }

    public List<ActionRepresentationSimple> getActionRepresentations(Collection<ActionDTO> actions) {
        List<ActionRepresentationSimple> representations = Lists.newLinkedList();
        for (ActionDTO action : actions) {
            representations.add(getActionRepresentationSimple(action, ActionRepresentationSimple.class));
        }
        return representations;
    }

    public List<ActionRepresentationExtended> getActionRepresentations(Resource resource, User user) {
        PrismScope scope = resource.getResourceScope();

        Set<ActionRepresentationExtended> representations = Sets.newLinkedHashSet();
        List<ActionDTO> actions = actionService.getPermittedActions(resource, user);
        for (ActionDTO action : actions) {
            representations.add(getActionRepresentationExtended(resource, action, user));
        }

        List<ActionDTO> publicActions = actionService.getPermittedUnsecuredActions(scope, Sets.newHashSet(resource.getId()), APPLICATION);
        for (ActionDTO publicAction : publicActions) {
            representations.add(getActionRepresentationExtended(resource, publicAction, user));
        }

        return Lists.newLinkedList(representations);
    }

    public ActionOutcomeRepresentation getActionOutcomeRepresentation(ActionOutcomeDTO actionOutcomeDTO) {
        return new ActionOutcomeRepresentation().withTransitionResource(
                resourceMapper.getResourceRepresentationSimple(actionOutcomeDTO.getResource())).withTransitionAction(actionOutcomeDTO.getTransitionAction().getId());
    }

    private ActionRepresentationExtended getActionRepresentationExtended(Resource resource, ActionDTO action, User user) {
        PrismAction prismAction = action.getActionId();
        ActionRepresentationExtended representation = getActionRepresentationSimple(action, ActionRepresentationExtended.class);

        representation.addActionEnhancements(actionService.getGlobalActionEnhancements(resource, prismAction, user));
        representation.addActionEnhancements(actionService.getCustomActionEnhancements(resource, prismAction, user));

        representation.addNextStates(stateMapper.getStateRepresentations(resource, prismAction));
        representation.addRecommendedNextStates(stateMapper.getRecommendedNextStateRepresentations(resource));

        return representation;
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

        return representation;
    }

}
