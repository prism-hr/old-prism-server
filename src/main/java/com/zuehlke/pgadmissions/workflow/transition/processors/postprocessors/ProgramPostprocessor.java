package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ProgramPostprocessor implements ResourceProcessor<Program> {

    @Inject
    private AdvertService advertService;

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
    }

}
