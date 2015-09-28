package com.zuehlke.pgadmissions.workflow.transition.populators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class ApplicationPopulator implements ResourcePopulator<Application> {

    @Inject
    private ApplicationService applicationService;

    @Override
    public void populate(Application resource) {
        applicationService.prepopulateApplication(resource);
    }

}
