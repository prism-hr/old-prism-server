package com.zuehlke.pgadmissions.services.helpers.persisters;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.services.ProjectService;

@Component
public class ProjectBackgroundPersister implements ImageDocumentPersister {

    @Inject
    private ProjectService projectService;

    @Override
    public void persist(Integer projectId, Document image) {
        Project project = projectService.getById(projectId);
        project.setBackgroundImage(image);
    }

}
