package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ProjectService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ProgramPostprocessor implements ResourceProcessor {

    @Inject
    private AdvertService advertService;

    @Inject
    private ProjectService projectService;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Program program = (Program) resource;
        DateTime updatedTimestamp = program.getUpdatedTimestamp();
        program.setUpdatedTimestampSitemap(updatedTimestamp);

        Department department = program.getDepartment();
        if (department != null) {
            department.setUpdatedTimestampSitemap(updatedTimestamp);
        }

        program.getInstitution().setUpdatedTimestampSitemap(updatedTimestamp);
        advertService.setSequenceIdentifier(program.getAdvert(), program.getSequenceIdentifier().substring(0, 13));

        if (comment.isProgramApproveComment()) {
            synchronizeProjects(comment, program);
        }
    }

    private void synchronizeProjects(Comment comment, Program program) {
        projectService.synchronizeProjectDueDates(program);
        if (comment.isProgramRestoreComment()) {
            projectService.restoreProjects(program, comment.getCreatedTimestamp().toLocalDate());
        }
    }

}
