package uk.co.alumeni.prism.rest.controller;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.mapping.ResourceMapper;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceCreationDTO;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationStandard;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.UserService;

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
    public void declineAction(@RequestParam Integer resourceId, @RequestParam PrismAction actionId, @RequestParam String activationCode, @RequestBody Map<?, ?> undertow) {
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
