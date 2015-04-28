package com.zuehlke.pgadmissions.workflow.transition.persisters;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface ResourcePersister {

    public void persist(Resource resource) throws Exception;

}
