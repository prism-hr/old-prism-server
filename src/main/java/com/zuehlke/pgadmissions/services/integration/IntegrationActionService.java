package com.zuehlke.pgadmissions.services.integration;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.rest.representation.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ActionRepresentation.SelectableStateRepresentation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.StateService;

@Service
@Transactional
public class IntegrationActionService {

    @Inject
    private ActionService actionService;

    @Inject
    private StateService stateService;

    //FIXME department
    public List<ActionRepresentation> getActionRepresentations(Resource resource, User user) {
        PrismScope scope = resource.getResourceScope();
        Integer resourceId = resource.getId();
        Integer systemId = resource.getSystem().getId();
        Integer institutionId = getResourceId(resource.getInstitution());
        Integer programId = getResourceId(resource.getProgram());
        Integer projectId = getResourceId(resource.getProject());
        Integer applicationId = getResourceId(resource.getApplication());

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

    private <T extends Resource> Integer getResourceId(T resource) {
        return resource == null ? null : resource.getId();
    }

}
