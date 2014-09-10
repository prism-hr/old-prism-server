package com.zuehlke.pgadmissions.services.helpers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.DeferredStateTransitionDTO;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class StateServiceHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ActionService actionService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private SystemService systemService;
    
    ExecutorService executor = Executors.newFixedThreadPool(10);

    public void executeDeferredStateTransitions() {
        Runnable transition = new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("Flushing resource escalations");
                    executeEscalatedStateTransitions();

                    logger.info("Flushing resource propagations");
                    executePropagatedStateTransitions();
                } catch (Exception e) {
                    logger.error("Error executing deferred state transitions", e);
                }
            }
        };
        
        try {
            executor.submit(transition);
        } catch (RejectedExecutionException e) {
            logger.info("The thread pool is saturated", e);
        }
    }

    private void executeEscalatedStateTransitions() throws DeduplicationException {
        LocalDate baseline = new LocalDate();
        List<PrismAction> escalationActionIds = actionService.getEscalationActions();
        for (PrismAction escalatedActionId : escalationActionIds) {
            Class<? extends Resource> resourceClass = escalatedActionId.getScope().getResourceClass();
            List<Integer> resourceIds = resourceService.getResourcesToEscalate(resourceClass, escalatedActionId, baseline);
            for (Integer resourceId : resourceIds) {
                stateService.executeDeferredStateTransition(new DeferredStateTransitionDTO(resourceClass, resourceId, escalatedActionId));
            }
        }
    }

    private void executePropagatedStateTransitions() throws DeduplicationException {
        List<PrismScope> scopeIds = scopeService.getScopesDescending();
        for (PrismScope scopeId : scopeIds) {
            List<StateTransitionPendingDTO> stateTransitionPendingDTOs = stateService.getStateTransitionsPending(scopeId);
            for (StateTransitionPendingDTO stateTransitionPendingDTO : stateTransitionPendingDTOs) {
                Integer stateTransitionPendingId = stateTransitionPendingDTO.getId();
                List<PrismAction> propagatedActionIds = actionService.getPropagatedActions(stateTransitionPendingId);
                for (PrismAction propagatedActionId : propagatedActionIds) {
                    Class<? extends Resource> propagatedResourceClass = propagatedActionId.getScope().getResourceClass();
                    List<Integer> resourceIds = resourceService.getResourcesToPropagate(scopeId, stateTransitionPendingDTO.getResourceId(),
                            propagatedActionId.getScope(), propagatedActionId);
                    if (resourceIds.size() == 0) {
                        stateService.deleteStateTransitionPending(stateTransitionPendingId);
                    }
                    for (Integer resourceId : resourceIds) {
                        stateService.executeDeferredStateTransition(new DeferredStateTransitionDTO(propagatedResourceClass, resourceId, propagatedActionId));
                    }
                }
            }
        }
    }

}
