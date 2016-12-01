package uk.co.alumeni.prism.workflow.transition.processors.postprocessors;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.workflow.transition.processors.ResourceProcessor;

import javax.inject.Inject;

@Component
public class ProgramPostprocessor implements ResourceProcessor<Program> {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Override
    public void process(Program resource, Comment comment) {
        resourceService.setResourceParentAdvertState(resource, comment);

        if (comment.isRestoreComment()) {
            advertService.retireAdvertClosingDate(resource.getAdvert());
        }

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
