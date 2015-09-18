package com.zuehlke.pgadmissions.workflow.selectors.filter;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class StateByStateGroupSelector implements PrismResourceListFilterSelector<PrismState> {

	@Inject
	private StateService stateService;

	@Override
	public List<PrismState> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint) {
		return stateService.getStatesByStateGroup(constraint.getValueStateGroup());
	}

}
