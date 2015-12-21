package uk.co.alumeni.prism.workflow.transition.processors.postprocessors;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.workflow.transition.processors.ResourceProcessor;

@Component
public class ProjectPostprocessor implements ResourceProcessor<Project> {

    @Inject
    private AdvertService advertService;

    @Override
    public void process(Project resource, Comment comment) {
        DateTime updatedTimestamp = resource.getUpdatedTimestamp();
        resource.setUpdatedTimestampSitemap(updatedTimestamp);

        Program program = resource.getProgram();
        if (program != null) {
            program.setUpdatedTimestampSitemap(updatedTimestamp);
        }

        Department department = resource.getDepartment();
        if (department != null) {
            department.setUpdatedTimestampSitemap(updatedTimestamp);
        }

        resource.getInstitution().setUpdatedTimestampSitemap(updatedTimestamp);
        advertService.setSequenceIdentifier(resource.getAdvert(), resource.getSequenceIdentifier().substring(0, 13));
    }

}