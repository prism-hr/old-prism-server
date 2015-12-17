package uk.co.alumeni.prism.workflow.selectors.filter;

import static java.util.Arrays.asList;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListConstraint.USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.PrismResourceListConstraint;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterConstraintDTO;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;

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
            return resourceService.getResourcesByUserAndRole(scope, constraint.getValueString(), asList(APPLICATION_CREATOR));
        } else {
            return resourceService.getResourcesByUserAndRole(scope, constraint.getValueString(),
                    roleService.getRolesByScope(PrismScope.valueOf(constraint.getFilterProperty().name().replace("_USER", ""))));
        }
    }

}
