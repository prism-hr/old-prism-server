package com.zuehlke.pgadmissions.workflow.resource.creators;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.ResourceDTO;

public interface ResourceCreator {

    public Resource create(User user, ResourceDTO newResource) throws Exception;

}
