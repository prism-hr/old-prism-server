package com.zuehlke.pgadmissions.rest.controller;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ApplicationRepresentation;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping(value = { "api/decline" })
public class DeclineController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UserService userService;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(method = RequestMethod.POST)
    public void declineAction(@RequestParam Integer resourceId, @RequestParam PrismAction actionId, @RequestParam String activationCode) throws Exception {
        User user = userService.getUserByActivationCode(activationCode);
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setUser(user.getId());
        commentDTO.setAction(actionId);
        commentDTO.setDeclinedResponse(true);
        resourceService.executeAction(user, resourceId, commentDTO);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ApplicationRepresentation getDeclineResource(@RequestParam Integer resourceId, @RequestParam PrismAction actionId,
            @RequestParam String activationCode) {
        userService.getUserByActivationCode(activationCode);
        Resource resource = resourceService.getById(actionId.getScope().getResourceClass(), resourceId);
        if (actionId.getScope() != PrismScope.APPLICATION) {
            throw new UnsupportedOperationException(actionId.getScope() + " action cannot be declined");
        }

        return dozerBeanMapper.map(resource, ApplicationRepresentation.class);
    }

}
