package com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ProjectPreprocessor implements ResourceProcessor {

    @Inject
    private ResourceService resourceService;

    @Override
    public void process(Resource resource, Comment comment) {
        Project project = (Project) resource;

        if (comment.isCreateComment()) {
            resourceService.synchronizePartner(project, comment);
        }
    }

}
