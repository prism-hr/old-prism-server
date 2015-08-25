package com.zuehlke.pgadmissions.workflow.transition.populators;

import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface ResourcePopulator<T extends Resource> {

    void populate(T resource);

}
