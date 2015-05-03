package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class StateServiceHelperEscalation implements AbstractServiceHelper {

	@Autowired
	private ActionService actionService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private StateService stateService;

	@Override
	public void execute() throws Exception {
		LocalDate baseline = new LocalDate();
		List<PrismAction> actionIds = actionService.getEscalationActions();
		for (PrismAction prismAction : actionIds) {
		    PrismScope resourceScope = prismAction.getScope();
			List<Integer> resourceIds = resourceService.getResourcesToEscalate(resourceScope, prismAction, baseline);
			for (Integer resourceId : resourceIds) {
				stateService.executeDeferredStateTransition(resourceScope, resourceId, prismAction);
			}
		}
	}

}
