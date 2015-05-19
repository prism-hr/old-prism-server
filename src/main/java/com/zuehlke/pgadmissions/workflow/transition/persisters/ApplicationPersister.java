package com.zuehlke.pgadmissions.workflow.transition.persisters;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.EntityService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ApplicationPersister implements ResourcePersister {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private EntityService entityService;

    @Override
    public void persist(Resource resource) throws Exception {
        Application application = (Application) resource;
        applicationService.prepopulateApplication(application);
        entityService.save(application);
    }

}
