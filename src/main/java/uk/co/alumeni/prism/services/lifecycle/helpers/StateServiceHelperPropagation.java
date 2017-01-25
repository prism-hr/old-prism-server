package uk.co.alumeni.prism.services.lifecycle.helpers;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.dto.StateTransitionPendingDTO;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.ScopeService;
import uk.co.alumeni.prism.services.StateService;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class StateServiceHelperPropagation extends PrismServiceHelperAbstract {

    @Inject
    private ScopeService scopeService;

    @Inject
    private StateService stateService;

    @Inject
    private ResourceService resourceService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        List<PrismScope> resourceScopes = scopeService.getScopesDescending();
        for (PrismScope resourceScope : resourceScopes) {
            List<StateTransitionPendingDTO> stateTransitionsPending = stateService.getStateTransitionsPending(resourceScope);
            for (StateTransitionPendingDTO stateTransitionPending : stateTransitionsPending) {
                PrismAction actionId = stateTransitionPending.getActionId();
                PrismScope actionScope = actionId.getScope();
                List<Integer> resourceIds = resourceService.getResourcesToPropagate(resourceScope, stateTransitionPending.getResourceId(), actionScope,
                        actionId);
                for (Integer resourceId : resourceIds) {
                    executeDeferredStateTransition(actionScope, resourceId, actionId);
                }
                if (resourceIds.size() == 0) {
                    deleteStateTransitionPending(stateTransitionPending);
                }
            }
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void executeDeferredStateTransition(PrismScope actionScope, Integer resourceId, PrismAction actionId) {
        if (!isShuttingDown()) {
            stateService.executeDeferredStateTransition(actionScope, resourceId, actionId);
        }
    }

    private void deleteStateTransitionPending(StateTransitionPendingDTO stateTransitionPending) {
        if (!isShuttingDown()) {
            stateService.deleteStateTransitionPending(stateTransitionPending.getId());
        }
    }

}
