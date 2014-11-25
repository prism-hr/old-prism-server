package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class StateServiceHelperEscalation extends AbstractServiceHelper {

    @Autowired
    private ActionService actionService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private StateService stateService;

    @Override
    public void execute() throws DeduplicationException, InstantiationException, IllegalAccessException {
        LocalDate baseline = new LocalDate();
        List<PrismAction> actionIds = actionService.getEscalationActions();
        for (PrismAction actionId : actionIds) {
            Class<? extends Resource> resourceClass = actionId.getScope().getResourceClass();
            List<Integer> resourceIds = resourceService.getResourcesToEscalate(resourceClass, actionId, baseline);
            for (Integer resourceId : resourceIds) {
                stateService.executeDeferredStateTransition(resourceClass, resourceId, actionId);
            }
        }
    }

}
