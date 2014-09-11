package com.zuehlke.pgadmissions.services.helpers;

import java.util.List;

import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
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
public class StateServiceHelper extends AbstractServiceHelper {

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

    @Override
    public void execute() throws DeduplicationException {
        final List<DeferredStateTransitionDTO> transitions = Lists.newLinkedList();
        transitions.addAll(getEscalatedStateTransitions());
        transitions.addAll(getPropagatedStateTransitions());

        for (DeferredStateTransitionDTO transition : transitions) {
            stateService.executeDeferredStateTransition(transition);
        }
    }

    private List<DeferredStateTransitionDTO> getEscalatedStateTransitions() throws DeduplicationException {
        List<DeferredStateTransitionDTO> queue = Lists.newLinkedList();
        LocalDate baseline = new LocalDate();
        List<PrismAction> escalationActionIds = actionService.getEscalationActions();
        for (PrismAction escalatedActionId : escalationActionIds) {
            Class<? extends Resource> resourceClass = escalatedActionId.getScope().getResourceClass();
            List<Integer> resourceIds = resourceService.getResourcesToEscalate(resourceClass, escalatedActionId, baseline);
            for (Integer resourceId : resourceIds) {
                queue.add(new DeferredStateTransitionDTO(resourceClass, resourceId, escalatedActionId));
            }
        }
        return queue;
    }

    private List<DeferredStateTransitionDTO> getPropagatedStateTransitions() throws DeduplicationException {
        List<DeferredStateTransitionDTO> queue = Lists.newLinkedList();
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
                        queue.add(new DeferredStateTransitionDTO(propagatedResourceClass, resourceId, propagatedActionId));
                    }
                }
            }
        }
        return queue;
    }

}
