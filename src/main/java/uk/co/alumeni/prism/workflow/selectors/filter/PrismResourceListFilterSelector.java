package uk.co.alumeni.prism.workflow.selectors.filter;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterConstraintDTO;

import java.util.List;

public interface PrismResourceListFilterSelector<T> {

    List<T> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint);

}
