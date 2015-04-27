package com.zuehlke.pgadmissions.workflow.resource.persisters;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface ResourcePersister {

    public void persist(Resource resource) throws Exception;

}
