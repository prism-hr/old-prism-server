package uk.co.alumeni.prism.workflow.transition.processors.postprocessors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.workflow.transition.processors.ResourceProcessor;

@Component
public class InstitutionPostprocessor implements ResourceProcessor<Institution> {

    @Inject
    private AdvertService advertService;

    @Override
    public void process(Institution resource, Comment comment) {
        if (comment.isPublishComment()) {
            resource.getAdvert().setPublished(true);
        }

        advertService.setSequenceIdentifier(resource.getAdvert(), resource.getSequenceIdentifier().substring(0, 13));
    }

}
