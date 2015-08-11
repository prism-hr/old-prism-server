package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_IMPORT_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;

@Service
@Transactional
public class ResourceImportService {

    @Inject
    private ResourceService resourceService;

    @Inject
    private ActionService actionService;

    public Resource<?> importResource(User user, ResourceCreationDTO resourceDTO) {
        PrismAction action = null;
        if (resourceDTO.getScope() == INSTITUTION) {
            action = SYSTEM_IMPORT_INSTITUTION;
        }
        ActionOutcomeDTO outcome = resourceService.createResource(user, actionService.getById(action), resourceDTO);
        return outcome.getResource();
    }

}
