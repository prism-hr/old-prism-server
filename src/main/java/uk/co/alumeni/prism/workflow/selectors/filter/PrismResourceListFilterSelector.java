package uk.co.alumeni.prism.workflow.selectors.filter;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterConstraintDTO;

public interface PrismResourceListFilterSelector<T> {

	List<T> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint);

}
