package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class InstitutionPostprocessor implements ResourceProcessor {

    @Inject
    private AdvertService advertService;
    
    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Institution institution = (Institution) resource;

        if (comment.isSponsorshipComment()) {
            advertService.synchronizeSponsorship(institution, comment);
        }
    }

}
