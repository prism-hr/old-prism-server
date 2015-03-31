package com.zuehlke.pgadmissions.workflow.selectors.filter;

import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilter.SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilter.USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_CONFIRMED_SUPERVISOR_GROUP;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilter;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;

@Component
public class ResourceByUserAndRoleSelector implements PrismResourceListFilterSelector<Integer> {

	@Inject
	private ResourceService resourceService;

	@Inject
	RoleService roleService;

	@Override
	public List<Integer> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint) {
		List<PrismRole> roles = Lists.newArrayList();
		PrismResourceListFilter filter = constraint.getFilterProperty();
		if (filter == SUPERVISOR) {
			roles.addAll(Lists.<PrismRole> newArrayList(APPLICATION_CONFIRMED_SUPERVISOR_GROUP.getRoles()));
		} else if (filter == USER) {
			roles.add(APPLICATION_CREATOR);
		} else {
			roles.addAll(roleService.getRolesByScope(PrismScope.valueOf(constraint.getFilterProperty().name().replace("_USER", ""))));
		}
		return resourceService.getResourcesByUserMatchingUserAndRole(scope, constraint.getValueString(), roles);
	}

}
