package com.zuehlke.pgadmissions.mapping;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.StateSelectableDTO;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationSimple;
import com.zuehlke.pgadmissions.services.StateService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StateMapper {

    @Inject
    private StateService stateService;

    public StateRepresentationSimple getStateRepresentationSimple(State state) {
        return getStateRepresentationSimple(state.getId());
    }

    public StateRepresentationSimple getStateRepresentationSimple(PrismState state) {
        return getStateRepresentation(state, StateRepresentationSimple.class);
    }

    public StateRepresentationExtended getStateRepresentationExtended(PrismState state, boolean parallelizable) {
        StateRepresentationExtended representation = getStateRepresentation(state, StateRepresentationExtended.class);
        representation.setParallelizable(parallelizable);
        return representation;
    }

    public List<StateRepresentationSimple> getSecondaryStateRepresentations(Resource resource) {
        return getStateRepresentations(stateService.getSecondaryResourceStates(resource));
    }

    public List<StateRepresentationSimple> getStateRepresentations(List<PrismState> states) {
        return states.stream()
                .map(this::getStateRepresentationSimple)
                .collect(Collectors.toList());
    }

    public List<StateRepresentationExtended> getStateRepresentations(Resource resource, PrismAction action) {
        List<StateSelectableDTO> states = stateService.getSelectableTransitionStates(resource.getState(), action, resource.getAdvert().isImported());
        return states.stream()
                .map(state -> getStateRepresentationExtended(state.getState(), BooleanUtils.toBoolean(state.getParallelizable())))
                .collect(Collectors.toList());
    }

    public List<StateRepresentationSimple> getRecommendedNextStateRepresentations(Resource resource) {
        return stateService.getRecommendedNextStates(resource).stream()
                .map(this::getStateRepresentationSimple)
                .collect(Collectors.toList());
    }

    private <T extends StateRepresentationSimple> T getStateRepresentation(PrismState state, Class<T> returnType) {
        T representation = BeanUtils.instantiate(returnType);

        representation.setState(state);
        representation.setStateGroup(state.getStateGroup());

        return representation;
    }

}
