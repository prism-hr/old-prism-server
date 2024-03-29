package uk.co.alumeni.prism.services.helpers.persisters;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.services.ResourceService;

import javax.inject.Inject;

@Component
public class ProjectBackgroundPersister implements ImageDocumentPersister {

    @Inject
    private ResourceService resourceService;

    @Override
    public void persist(Integer projectId, Document image) {
        Project project = resourceService.getById(Project.class, projectId);
        project.getAdvert().setBackgroundImage(image);
    }

}
