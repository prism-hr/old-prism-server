package com.zuehlke.pgadmissions.workflow.transition.creators;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;

public interface ResourceCreator {

    Resource create(User user, ResourceDTO newResource) throws Exception;

}
