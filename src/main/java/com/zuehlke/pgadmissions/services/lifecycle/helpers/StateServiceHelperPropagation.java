package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
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
	public void execute() throws Exception {
		List<PrismScope> scopeIds = scopeService.getScopesDescending();
		for (PrismScope scopeId : scopeIds) {
			List<StateTransitionPendingDTO> stateTransitionsPending = stateService.getStateTransitionsPending(scopeId);
			for (StateTransitionPendingDTO stateTransitionPending : stateTransitionsPending) {
				PrismAction actionId = stateTransitionPending.getActionId();
				PrismScope actionScope = actionId.getScope();
				Class<? extends Resource> resourceClass = actionScope.getResourceClass();
				List<Integer> resourceIds = resourceService.getResourcesToPropagate(scopeId, stateTransitionPending.getResourceId(), actionScope, actionId);
				for (Integer resourceId : resourceIds) {
					stateService.executeDeferredStateTransition(resourceClass, resourceId, actionId);
				}
				if (resourceIds.size() == 0) {
					stateService.deleteStateTransitionPending(stateTransitionPending.getId());
				}
			}
		}
	}

}
