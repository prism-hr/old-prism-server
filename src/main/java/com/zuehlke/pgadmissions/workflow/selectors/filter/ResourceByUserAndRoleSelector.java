package com.zuehlke.pgadmissions.workflow.selectors.filter;

import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListConstraint.USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static java.util.Arrays.asList;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListConstraint;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterConstraintDTO;
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
        PrismResourceListConstraint filter = constraint.getFilterProperty();
        if (filter.equals(USER)) {
            return resourceService.getResourcesByMatchingUserAndRole(scope, constraint.getValueString(), asList(APPLICATION_CREATOR));
        } else {
            return resourceService.getResourcesByMatchingUserAndRole(scope, constraint.getValueString(),
                    roleService.getRolesByScope(PrismScope.valueOf(constraint.getFilterProperty().name().replace("_USER", ""))));
        }
    }

}
