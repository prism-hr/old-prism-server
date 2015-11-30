package uk.co.alumeni.prism.workflow.selectors.filter;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;

import java.util.List;

import javax.inject.Inject;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterConstraintDTO;
import uk.co.alumeni.prism.services.ApplicationService;
import uk.co.alumeni.prism.services.ResourceService;

public class ResourceByLocationSelector implements PrismResourceListFilterSelector<Integer> {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ResourceService resourceService;

    @Override
    public List<Integer> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint) {
        return scope.equals(APPLICATION) ? applicationService.getApplicationsByLocation(constraint.getValueString())
                : resourceService.getResourcesByLocation(scope, constraint.getValueString());
    }

}
