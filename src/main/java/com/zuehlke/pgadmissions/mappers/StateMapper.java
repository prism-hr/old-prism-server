package com.zuehlke.pgadmissions.mappers;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
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
            representations.add(getStateRepresentationExtended(state.getState(), state.getParallelizable()));
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

    private <T extends StateRepresentationSimple> T getStateRepresentation(PrismState state, Class<T> returnType) {
        T representation = BeanUtils.instantiate(returnType);

        representation.setState(state);
        representation.setStateGroup(state.getStateGroup());

        return representation;
    }

}
