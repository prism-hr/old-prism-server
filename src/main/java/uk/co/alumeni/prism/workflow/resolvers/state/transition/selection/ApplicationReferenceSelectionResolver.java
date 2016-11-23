package uk.co.alumeni.prism.workflow.resolvers.state.transition.selection;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.services.ApplicationService;

import javax.inject.Inject;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class ApplicationReferenceSelectionResolver implements StateTransitionSelectionResolver {

    @Inject
    private ApplicationService applicationService;

    @Override
    public boolean resolve(Resource resource) {
        return isNotEmpty(applicationService.getApplicationRefereesNotResponded((Application) resource));
    }

}
