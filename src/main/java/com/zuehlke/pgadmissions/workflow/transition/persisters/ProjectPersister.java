package com.zuehlke.pgadmissions.workflow.transition.persisters;

import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.EntityService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ProjectPersister implements ResourcePersister {

    @Inject
    private EntityService entityService;

    @Override
    public void persist(Resource resource) {
        Project project = (Project) resource;
        entityService.save(project);

    }

}
