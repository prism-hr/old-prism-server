package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class StateServiceHelperPropagation implements AbstractServiceHelper {

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private StateService stateService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ResourceService resourceService;

    @Override
    public void execute() throws DeduplicationException, InstantiationException, WorkflowEngineException, IOException,
            IntegrationException, BeansException, IllegalAccessException {
        List<PrismScope> scopeIds = scopeService.getScopesDescending();
        for (PrismScope scopeId : scopeIds) {
            List<StateTransitionPendingDTO> stateTransitionPendingDTOs = stateService.getStateTransitionsPending(scopeId);
            for (StateTransitionPendingDTO stateTransitionPendingDTO : stateTransitionPendingDTOs) {
                Integer stateTransitionPendingId = stateTransitionPendingDTO.getId();
                List<PrismAction> actionIds = actionService.getPropagatedActions(stateTransitionPendingId);
                for (PrismAction actionId : actionIds) {
                    Class<? extends Resource> resourceClass = actionId.getScope().getResourceClass();
                    List<Integer> resourceIds = resourceService.getResourcesToPropagate(scopeId, stateTransitionPendingDTO.getResourceId(),
                            actionId.getScope(), actionId);
                    for (Integer resourceId : resourceIds) {
                        stateService.executeDeferredStateTransition(resourceClass, resourceId, actionId);
                    }
                    if (resourceIds.size() == 0) {
                        stateService.deleteStateTransitionPending(stateTransitionPendingId);
                    }
                }
            }
        }
    }

}
