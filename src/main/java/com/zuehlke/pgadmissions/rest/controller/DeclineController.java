package com.zuehlke.pgadmissions.rest.controller;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.mapping.ResourceMapper;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationStandard;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping(value = { "api/decline" })
public class DeclineController {

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public void declineAction(@RequestParam Integer resourceId, @RequestParam PrismAction actionId, @RequestParam String activationCode) {
        User user = userService.getUserByActivationCode(activationCode);
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setResource(new ResourceCreationDTO().withId(resourceId).withScope(actionId.getScope()));
        commentDTO.setUser(user.getId());
        commentDTO.setAction(actionId);
        commentDTO.setDeclinedResponse(true);
        resourceService.executeAction(user, commentDTO);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResourceRepresentationStandard getDeclineResource(@RequestParam Integer resourceId, @RequestParam PrismAction actionId,
            @RequestParam String activationCode) throws Exception {
        User user = userService.getUserByActivationCode(activationCode);
        if (user == null || !actionId.isDeclinableAction()) {
            throw new UnsupportedOperationException(actionId.getScope() + " action cannot be declined");
        }

        Resource resource = resourceService.getById(actionId.getScope().getResourceClass(), resourceId);
        return resourceMapper.getResourceRepresentationStandard(resource);
    }

}
