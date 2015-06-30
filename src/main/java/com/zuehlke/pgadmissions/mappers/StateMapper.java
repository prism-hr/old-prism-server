package com.zuehlke.pgadmissions.mappers;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.StateSelectableDTO;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationSimple;
import com.zuehlke.pgadmissions.services.StateService;

@Service
@Transactional
public class StateMapper {

    @Inject
    private StateService stateService;

    public StateRepresentationSimple getStateRepresentationSimple(State state) {
        return getStateRepresentationSimple(state.getId());
    }

    public StateRepresentationSimple getStateRepresentationSimple(PrismState state) {
        return new StateRepresentationSimple().withState(state).withStateGroup(state.getStateGroup());
    }

    public List<StateRepresentationSimple> getSecondaryStateRepresentations(Resource resource) {
        return getStateRepresentations(stateService.getSecondaryResourceStates(resource));
    }

    public List<StateRepresentationSimple> getStateRepresentations(List<PrismState> states) {
        List<StateRepresentationSimple> representations = Lists.newLinkedList();
        for (PrismState state : states) {
            representations.add(getStateRepresentationSimple(state));
        }
        return representations;
    }

    public List<StateRepresentationExtended> getStateRepresentations(Resource resource, PrismAction action) {
        List<StateRepresentationExtended> representations = Lists.newLinkedList();
        List<StateSelectableDTO> states = stateService.getSelectableTransitionStates(resource.getState(), action, resource.getAdvert().isImported());
        for (StateSelectableDTO state : states) {
            PrismState prismState = state.getState();
            representations.add(new StateRepresentationExtended().withState(prismState).withStateGroup(prismState.getStateGroup())
                    .withParallelizable(state.getParallelizable()));
        }
        return representations;
    }

    public List<StateRepresentationSimple> getRecommendedNextStateRepresentations(Resource resource) {
        List<StateRepresentationSimple> representations = Lists.newLinkedList();
        for (PrismState state : stateService.getRecommendedNextStates(resource)) {
            representations.add(getStateRepresentationSimple(state));
        }
        return representations;
    }

}
