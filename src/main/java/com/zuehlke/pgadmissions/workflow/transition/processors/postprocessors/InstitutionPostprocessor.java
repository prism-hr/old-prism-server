package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class InstitutionPostprocessor implements ResourceProcessor<Institution> {

    @Inject
    private AdvertService advertService;
    
    @Inject
    private ResourceService resourceService;

    @Override
    public void process(Institution resource, Comment comment) {
        advertService.setSequenceIdentifier(resource.getAdvert(), resource.getSequenceIdentifier().substring(0, 13));
        if (comment.isResourceEndorsementComment()) {
            resourceService.synchronizeResourceEndorsement(resource, comment);
        }
    }

}
