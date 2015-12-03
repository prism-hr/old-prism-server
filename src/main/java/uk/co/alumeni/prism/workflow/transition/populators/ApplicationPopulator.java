package uk.co.alumeni.prism.workflow.transition.populators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.services.ApplicationService;

@Component
public class ApplicationPopulator implements ResourcePopulator<Application> {

    @Inject
    private ApplicationService applicationService;

    @Override
    public void populate(Application resource) {
        applicationService.prepopulateApplication(resource);
    }

}
