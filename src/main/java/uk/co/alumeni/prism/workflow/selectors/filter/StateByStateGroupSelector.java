package uk.co.alumeni.prism.workflow.selectors.filter;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterConstraintDTO;
import uk.co.alumeni.prism.services.StateService;

@Component
public class StateByStateGroupSelector implements PrismResourceListFilterSelector<PrismState> {

	@Inject
	private StateService stateService;

	@Override
	public List<PrismState> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint) {
		return stateService.getStatesByStateGroup(constraint.getValueStateGroup());
	}

}
