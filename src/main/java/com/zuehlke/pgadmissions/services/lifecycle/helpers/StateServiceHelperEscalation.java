package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.StateService;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class StateServiceHelperEscalation implements AbstractServiceHelper {

	@Inject
	private ActionService actionService;

	@Inject
	private ResourceService resourceService;

	@Inject
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
