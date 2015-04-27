package com.zuehlke.pgadmissions.workflow.selectors.filter;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;

public interface PrismResourceListFilterSelector<T> {

	public List<T> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint);

}
