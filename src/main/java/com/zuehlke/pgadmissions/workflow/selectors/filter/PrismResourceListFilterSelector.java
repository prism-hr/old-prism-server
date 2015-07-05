package com.zuehlke.pgadmissions.workflow.selectors.filter;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterConstraintDTO;

public interface PrismResourceListFilterSelector<T> {

	List<T> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint);

}
