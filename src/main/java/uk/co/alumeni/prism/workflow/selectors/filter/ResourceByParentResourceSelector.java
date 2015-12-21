package uk.co.alumeni.prism.workflow.selectors.filter;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterConstraintDTO;
import uk.co.alumeni.prism.services.ResourceService;

@Component
public class ResourceByParentResourceSelector implements PrismResourceListFilterSelector<Integer> {

	@Inject
	private ResourceService resourceService;

	@Override
	public List<Integer> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint) {
		return resourceService.getSimilarResources(PrismScope.valueOf(constraint.getFilterProperty().name().replace("_NAME", "")),
		        constraint.getValueString());
	}

}