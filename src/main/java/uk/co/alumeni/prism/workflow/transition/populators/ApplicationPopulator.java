package uk.co.alumeni.prism.workflow.transition.populators;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.services.ApplicationService;

import javax.inject.Inject;

@Component
public class ApplicationPopulator implements ResourcePopulator<Application> {

    @Inject
    private ApplicationService applicationService;

    @Override
    public void populate(Application resource) {
        applicationService.prepopulateApplication(resource);
    }

}
