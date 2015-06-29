package com.zuehlke.pgadmissions.services.integration;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.rest.representation.StateRepresentation;
import com.zuehlke.pgadmissions.services.StateService;

@Service
@Transactional
public class IntegrationStateService {

    @Inject
    private StateService stateService;

    public StateRepresentation getStateRepresentation(State state) {
        return new StateRepresentation().withState(state.getId()).withStateGroup(state.getStateGroup().getId());
    }

    public List<StateRepresentation> getSecondaryStateRepresentations(Resource resource) {
        List<StateRepresentation> secondaryStates = Lists.newLinkedList();
        for (State state : stateService.getSecondaryResourceStates(resource)) {
            secondaryStates.add(getStateRepresentation(state));
        }
        return secondaryStates;
    }

}
