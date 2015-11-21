package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.selection;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class ApplicationReferenceSelectionResolver implements StateTransitionSelectionResolver {

    @Inject
    private ApplicationService applicationService;

    @Override
    public boolean resolve(Resource resource) {
        return isNotEmpty(applicationService.getApplicationRefereesNotResponded((Application) resource));
    }

}
