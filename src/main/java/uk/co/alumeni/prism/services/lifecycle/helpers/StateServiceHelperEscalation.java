package uk.co.alumeni.prism.services.lifecycle.helpers;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.StateService;

@Component
public class StateServiceHelperEscalation extends PrismServiceHelperAbstract {

    @Inject
    private ActionService actionService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private StateService stateService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        LocalDate baseline = new LocalDate();
        List<PrismAction> actionIds = actionService.getEscalationActions();
        for (PrismAction prismAction : actionIds) {
            PrismScope resourceScope = prismAction.getScope();
            List<Integer> resourceIds = resourceService.getResourcesToEscalate(resourceScope, prismAction, baseline);
            for (Integer resourceId : resourceIds) {
                executeDeferredStateTransition(resourceScope, resourceId, prismAction);
            }
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void executeDeferredStateTransition(PrismScope resourceScope, Integer resourceId, PrismAction prismAction) {
        if (!isShuttingDown()) {
            stateService.executeDeferredStateTransition(resourceScope, resourceId, prismAction);
        }
    }

}
