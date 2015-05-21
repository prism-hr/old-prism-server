package com.zuehlke.pgadmissions.workflow.transition.processors;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.AdvertService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class InstitutionProcessor implements ResourceProcessor {

    @Inject
    private AdvertService advertService;

    @Override
    public void process(Resource resource, Comment comment) {
        Institution institution = (Institution) resource;

        if (comment.isSponsorshipComment()) {
            advertService.synchronizeSponsorship(institution, comment);
        }
    }

}
