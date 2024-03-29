package uk.co.alumeni.prism.mapping;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.rest.representation.state.StateRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.state.StateRepresentationSimple;
import uk.co.alumeni.prism.services.StateService;

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
        return stateService.getSelectableTransitionStates(resource, action).stream()
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
