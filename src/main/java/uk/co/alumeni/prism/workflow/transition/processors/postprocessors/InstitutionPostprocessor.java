package uk.co.alumeni.prism.workflow.transition.processors.postprocessors;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.workflow.transition.processors.ResourceProcessor;

import javax.inject.Inject;

@Component
public class InstitutionPostprocessor implements ResourceProcessor<Institution> {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Override
    public void process(Institution resource, Comment comment) {
        resourceService.setResourceParentAdvertState(resource, comment);
        advertService.setSequenceIdentifier(resource.getAdvert(), resource.getSequenceIdentifier().substring(0, 13));
    }

}
