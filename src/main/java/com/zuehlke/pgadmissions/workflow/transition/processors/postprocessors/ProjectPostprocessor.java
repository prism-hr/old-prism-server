package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ProjectPostprocessor implements ResourceProcessor {

    @Inject
    private AdvertService advertService;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Project project = (Project) resource;
        DateTime updatedTimestamp = project.getUpdatedTimestamp();
        project.setUpdatedTimestampSitemap(updatedTimestamp);
        project.getProgram().setUpdatedTimestampSitemap(updatedTimestamp);
        project.getInstitution().setUpdatedTimestampSitemap(updatedTimestamp);
        advertService.setSequenceIdentifier(project.getAdvert(), project.getSequenceIdentifier().substring(0, 13));
    }

}
