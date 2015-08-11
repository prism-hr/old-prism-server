package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.DepartmentService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class DepartmentPostprocessor implements ResourceProcessor<Department> {

    @Inject
    private AdvertService advertService;

    @Inject
    private DepartmentService departmentService;

    @Override
    public void process(Department resource, Comment comment) {
        DateTime updatedTimestamp = resource.getUpdatedTimestamp();
        resource.setUpdatedTimestampSitemap(updatedTimestamp);
        resource.getInstitution().setUpdatedTimestampSitemap(updatedTimestamp);
        advertService.setSequenceIdentifier(resource.getAdvert(), resource.getSequenceIdentifier().substring(0, 13));

        if (comment.isCreateComment() || comment.isViewEditComment()) {
            departmentService.synchronizeImportedSubjectAreas(resource);
        }
    }

}
