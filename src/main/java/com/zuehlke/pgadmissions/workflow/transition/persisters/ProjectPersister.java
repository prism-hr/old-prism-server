package com.zuehlke.pgadmissions.workflow.transition.persisters;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.EntityService;

@Component
public class ProjectPersister implements ResourcePersister {

    @Inject
    private EntityService entityService;

    @Override
    public void persist(Resource resource) throws Exception {
        Project project = (Project) resource;
        entityService.save(project);

    }

}
