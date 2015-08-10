package com.zuehlke.pgadmissions.services;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional
public class ResourceImportService {

    @Inject
    private ResourceService resourceService;

    @Inject
    private ActionService actionService;

    public Resource importResource(User user, ResourceCreationDTO resourceDTO) {
        PrismAction action = null;
        if(resourceDTO.getScope() == PrismScope.INSTITUTION) {
            action = PrismAction.SYSTEM_CREATE_INSTITUTION;
        }
        ActionOutcomeDTO outcome = resourceService.createResource(user, actionService.getById(action), resourceDTO);
        return outcome.getResource();
    }

}
