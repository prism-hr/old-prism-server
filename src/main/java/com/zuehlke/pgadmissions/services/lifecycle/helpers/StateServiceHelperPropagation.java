package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.StateService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class StateServiceHelperPropagation implements PrismServiceHelper {

    @Inject
    private ScopeService scopeService;

    @Inject
    private StateService stateService;

    @Inject
    private ResourceService resourceService;

    @Override
    public void execute() throws Exception {
        List<PrismScope> resourceScopes = scopeService.getScopesDescending();
        for (PrismScope resourceScope : resourceScopes) {
            List<StateTransitionPendingDTO> stateTransitionsPending = stateService.getStateTransitionsPending(resourceScope);
            for (StateTransitionPendingDTO stateTransitionPending : stateTransitionsPending) {
                PrismAction actionId = stateTransitionPending.getActionId();
                PrismScope actionScope = actionId.getScope();
                List<Integer> resourceIds = resourceService
                        .getResourcesToPropagate(resourceScope, stateTransitionPending.getResourceId(), actionScope, actionId);
                for (Integer resourceId : resourceIds) {
                    stateService.executeDeferredStateTransition(actionScope, resourceId, actionId);
                }
                if (resourceIds.size() == 0) {
                    stateService.deleteStateTransitionPending(stateTransitionPending.getId());
                }
            }
        }
    }

    @Override
    public void shutdown() {

    }

}
