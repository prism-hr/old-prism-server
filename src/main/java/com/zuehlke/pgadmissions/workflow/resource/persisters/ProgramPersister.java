package com.zuehlke.pgadmissions.workflow.resource.persisters;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.EntityService;

@Component
public class ProgramPersister implements ResourcePersister {

    @Inject
    private EntityService entityService;

    @Override
    public void persist(Resource resource) throws Exception {
        Program program = (Program) resource;
        entityService.save(program);
    }

}
