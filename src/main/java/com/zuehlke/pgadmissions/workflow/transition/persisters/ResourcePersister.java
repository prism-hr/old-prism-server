package com.zuehlke.pgadmissions.workflow.transition.persisters;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface ResourcePersister {

    void persist(Resource resource);

}
