package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ProgramPostprocessor implements ResourceProcessor<Program> {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Override
    public void process(Program resource, Comment comment) {
        DateTime updatedTimestamp = resource.getUpdatedTimestamp();
        resource.setUpdatedTimestampSitemap(updatedTimestamp);

        Department department = resource.getDepartment();
        if (department != null) {
            department.setUpdatedTimestampSitemap(updatedTimestamp);
        }

        resource.getInstitution().setUpdatedTimestampSitemap(updatedTimestamp);
        advertService.setSequenceIdentifier(resource.getAdvert(), resource.getSequenceIdentifier().substring(0, 13));

        if (comment.isRatingComment(PROGRAM)) {
            resourceService.synchronizeResourceEndorsement(resource, comment);
        }
    }

}
