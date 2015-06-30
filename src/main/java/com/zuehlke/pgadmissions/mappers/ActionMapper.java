package com.zuehlke.pgadmissions.mappers;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

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

    public List<ActionRepresentationSimple> getActionRepresentations(Collection<ActionDTO> actions) {
        List<ActionRepresentationSimple> representations = Lists.newLinkedList();
        for (ActionDTO action : actions) {
            representations.add(getActionRepresentationSimple(action));
        }
        return representations;
    }

    // FIXME department
    public List<ActionRepresentationExtended> getActionRepresentations(Resource resource, User user) {
        PrismScope scope = resource.getResourceScope();
        Integer resourceId = resource.getId();
        Integer systemId = resource.getSystem().getId();
        Integer institutionId = getResourceId(resource.getInstitution());
        Integer programId = getResourceId(resource.getProgram());
        Integer projectId = getResourceId(resource.getProject());
        Integer applicationId = getResourceId(resource.getApplication());

        Set<ActionRepresentationExtended> representations = Sets.newLinkedHashSet();
        List<ActionDTO> actions = actionService.getPermittedActions(scope, resourceId, systemId, institutionId, programId, projectId,
                applicationId, user);
        for (ActionDTO action : actions) {
            representations.add(getActionRepresentationExtended(resource, action, user));
        }

        List<ActionDTO> publicActions = actionService.getPermittedUnsecuredActions(scope, Sets.newHashSet(resource.getId()), APPLICATION);
        for (ActionDTO publicAction : publicActions) {
            representations.add(getActionRepresentationExtended(resource, publicAction, user));
        }

        return Lists.newLinkedList(representations);
    }

    public ActionRepresentationSimple getActionRepresentationSimple(ActionDTO action) {
        return new ActionRepresentationSimple()
                .withId(action.getActionId())
                .withRaisesUrgentFlag(action.getRaisesUrgentFlag())
                .withPrimaryState(action.getPrimaryState());
    }

    public ActionRepresentationExtended getActionRepresentationExtended(Resource resource, ActionDTO action, User user) {
        PrismAction prismAction = action.getActionId();
        ActionRepresentationExtended representation = (ActionRepresentationExtended) getActionRepresentationSimple(action);

        representation.addActionEnhancements(actionService.getGlobalActionEnhancements(resource, prismAction, user));
        representation.addActionEnhancements(actionService.getCustomActionEnhancements(resource, prismAction, user));

        representation.addNextStates(stateMapper.getStateRepresentations(resource, prismAction));
        representation.addRecommendedNextStates(stateMapper.getRecommendedNextStateRepresentations(resource));

        return representation;
    }

    public ActionOutcomeRepresentation getActionOutcomeRepresentation(ActionOutcomeDTO actionOutcomeDTO) {
        return new ActionOutcomeRepresentation().withTransitionResource(
                resourceMapper.getResourceRepresentationSimple(actionOutcomeDTO.getResource())).withTransitionAction(
                actionOutcomeDTO.getTransitionAction().getId());
    }

    private <T extends Resource> Integer getResourceId(T resource) {
        return resource == null ? null : resource.getId();
    }

}
