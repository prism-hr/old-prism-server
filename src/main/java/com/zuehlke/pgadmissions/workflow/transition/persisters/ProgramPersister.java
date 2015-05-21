package com.zuehlke.pgadmissions.workflow.transition.persisters;

import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.EntityService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ProgramPersister implements ResourcePersister {

    @Inject
    private EntityService entityService;

    @Override
    public void persist(Resource resource) {
        Program program = (Program) resource;
        entityService.save(program);
    }

}
