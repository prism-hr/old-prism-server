package uk.co.alumeni.prism.workflow.selectors.action;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.services.ApplicationService;

@Component
public class ApplicationByReferencesProvidedSelector implements PrismResourceByParentResourceSelector {

    @Inject
    private ApplicationService applicationService;

    @Override
    public List<Integer> getPossible(Resource parentResource) {
        List<Integer> possible = applicationService.getApplicationsWithReferencesProvided(parentResource);
        possible.removeAll(applicationService.getApplicationsWithReferencesPending(parentResource));
        return possible;
    }

}
