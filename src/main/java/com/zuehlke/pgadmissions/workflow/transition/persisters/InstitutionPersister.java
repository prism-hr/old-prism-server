package com.zuehlke.pgadmissions.workflow.transition.persisters;

import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.EntityService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class InstitutionPersister implements ResourcePersister {

    @Inject
    private EntityService entityService;

    @Override
    public void persist(Resource resource) {
        Institution institution = (Institution) resource;
        entityService.save(institution);
    }

}
