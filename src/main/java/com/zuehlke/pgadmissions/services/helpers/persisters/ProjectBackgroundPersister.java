package com.zuehlke.pgadmissions.services.helpers.persisters;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.services.ResourceService;
import org.springframework.stereotype.Component;

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
