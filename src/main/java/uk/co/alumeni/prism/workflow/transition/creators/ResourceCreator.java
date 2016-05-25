package uk.co.alumeni.prism.workflow.transition.creators;

import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.dto.resource.ResourceCreationDTO;

public interface ResourceCreator<T extends ResourceCreationDTO> {

    Resource create(User user, T newResource);

}
