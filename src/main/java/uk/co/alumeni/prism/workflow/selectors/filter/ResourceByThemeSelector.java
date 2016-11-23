package uk.co.alumeni.prism.workflow.selectors.filter;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterConstraintDTO;
import uk.co.alumeni.prism.services.ResourceService;

import javax.inject.Inject;
import java.util.List;

@Component
public class ResourceByThemeSelector implements PrismResourceListFilterSelector<Integer> {

    @Inject
    private ResourceService resourceService;

    @Override
    public List<Integer> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint) {
        return resourceService.getResourcesByTheme(scope, constraint.getFilterExpression(), constraint.getValueString());
    }

}
