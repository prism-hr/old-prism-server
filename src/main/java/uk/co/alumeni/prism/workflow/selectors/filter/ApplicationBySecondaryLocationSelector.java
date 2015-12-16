package uk.co.alumeni.prism.workflow.selectors.filter;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterConstraintDTO;
import uk.co.alumeni.prism.services.ApplicationService;

@Component
public class ApplicationBySecondaryLocationSelector implements PrismResourceListFilterSelector<Integer> {

    @Inject
    private ApplicationService applicationService;

    @Override
    public List<Integer> getPossible(PrismScope scope, ResourceListFilterConstraintDTO constraint) {
        return applicationService.getApplicationsByLocation(constraint.getValueString(), false);
    }

}
