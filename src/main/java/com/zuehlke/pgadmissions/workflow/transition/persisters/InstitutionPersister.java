package com.zuehlke.pgadmissions.workflow.transition.persisters;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.EntityService;

@Component
public class InstitutionPersister implements ResourcePersister {

    @Inject
    private EntityService entityService;

    @Override
    public void persist(Resource resource) throws Exception {
        Institution institution = (Institution) resource;
        entityService.save(institution);
    }

}
