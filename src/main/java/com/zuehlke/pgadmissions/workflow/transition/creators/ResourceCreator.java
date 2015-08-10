package com.zuehlke.pgadmissions.workflow.transition.creators;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;

public interface ResourceCreator<T extends ResourceCreationDTO> {

    Resource<?> create(User user, T newResource) throws Exception;

}
