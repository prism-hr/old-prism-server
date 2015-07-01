package com.zuehlke.pgadmissions.workflow.selectors.filter;

import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListContraint.SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListContraint.USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_CONFIRMED_SUPERVISOR_GROUP;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListContraint;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;

@Component
public class ResourceByUserAndRoleSelector implements PrismResourceListFilterSelector<Integer> {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ResourceService resourceService;

    @Inject
    RoleService roleService;

    @Override
    public List<Integer> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint) {
        List<PrismRole> roles = Lists.newArrayList();
        PrismResourceListContraint filter = constraint.getFilterProperty();
        if (filter == SUPERVISOR) {
            String searchTerm = constraint.getValueString();
            roles.addAll(Lists.<PrismRole> newArrayList(APPLICATION_CONFIRMED_SUPERVISOR_GROUP.getRoles()));
            List<Integer> applications = resourceService.getResourcesByUserMatchingUserAndRole(scope, searchTerm, roles);
            applications.addAll(applicationService.getApplicationsByMatchingSuggestedSupervisor(searchTerm));
            return applications;
        } else if (filter == USER) {
            roles.add(APPLICATION_CREATOR);
            return resourceService.getResourcesByUserMatchingUserAndRole(scope, constraint.getValueString(), roles);
        } else {
            roles.addAll(roleService.getRolesByScope(PrismScope.valueOf(constraint.getFilterProperty().name().replace("_USER", ""))));
            return resourceService.getResourcesByUserMatchingUserAndRole(scope, constraint.getValueString(), roles);
        }
    }

}
