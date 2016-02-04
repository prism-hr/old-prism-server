package com.zuehlke.pgadmissions.workflow.transition.processors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class ProjectProcessor implements ResourceProcessor {

    @Inject
    private AdvertService advertService;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Project project = (Project) resource;

        if (comment.isSponsorshipComment()) {
            advertService.synchronizeSponsorship(project, comment);
        }
    }

}
