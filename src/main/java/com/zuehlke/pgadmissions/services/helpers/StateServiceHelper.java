package com.zuehlke.pgadmissions.services.helpers;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.StateTransitionPendingDTO;
import com.zuehlke.pgadmissions.services.*;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StateServiceHelper {

    private static final Logger log = LoggerFactory.getLogger(StateServiceHelper.class);

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

    public void executeEscalatedStateTransitions() throws Exception {
        LocalDate baseline = new LocalDate();
        List<PrismAction> escalationActionIds = actionService.getEscalationActions();
        for (PrismAction escalatedActionId : escalationActionIds) {
            Class<? extends Resource> resourceClass = escalatedActionId.getScope().getResourceClass();
            List<Integer> resourceIds = resourceService.getResourcesToEscalate(resourceClass, escalatedActionId, baseline);
            for (Integer resourceId : resourceIds) {
                stateService.executeDeferredStateTransition(resourceClass, resourceId, escalatedActionId);
            }
        }
    }

    public void executePropagatedStateTransitions() throws Exception {
        List<PrismScope> scopeIds = scopeService.getScopesDescending();
        for (PrismScope scopeId : scopeIds) {
            // TODO remove comment
            log.info("iterating over scope " + scopeId);

            List<StateTransitionPendingDTO> stateTransitionPendingDTOs = stateService.getStateTransitionsPending(scopeId);

            // TODO remove comment
            log.info("pending trasitions " + stateTransitionPendingDTOs.size());
            for (StateTransitionPendingDTO stateTransitionPendingDTO : stateTransitionPendingDTOs) {
                Integer stateTransitionPendingId = stateTransitionPendingDTO.getId();
                boolean completed = true;
                List<PrismAction> propagatedActionIds = actionService.getPropagatedActions(stateTransitionPendingId);
                for (PrismAction propagatedActionId : propagatedActionIds) {
                    PrismScope propagatedScopeId = propagatedActionId.getScope();
                    List<Integer> resourceIds = resourceService.getResourcesToPropagate(scopeId, stateTransitionPendingDTO.getResourceId(),
                            propagatedActionId.getScope(), propagatedActionId);
                    for (Integer resourceId : resourceIds) {
                        try {
                            stateService.executeDeferredStateTransition(propagatedScopeId.getResourceClass(), resourceId, propagatedActionId);
                        } catch (Exception e) {
                            log.error("Problem executing deferred state transition", e);
                            completed = false;
                            continue;
                        }
                    }
                }
                if (completed) {
                    stateService.deleteStateTransitionPending(stateTransitionPendingId);
                }
            }
        }
    }
}
