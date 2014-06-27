package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.rest.domain.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.domain.ResourceRepresentation;
import com.zuehlke.pgadmissions.rest.domain.application.ApplicationRepresentation;
import com.zuehlke.pgadmissions.rest.domain.application.ProgramRepresentation;
import com.zuehlke.pgadmissions.rest.domain.application.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.services.*;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"api/{resourceType}"})
public class ResourceResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Transactional
    public ResourceRepresentation getResource(@PathVariable String resourceType, @PathVariable Integer id) {
        User currentUser = userService.getCurrentUser();
        ResourceDynamic resource = entityService.getById(resourceType.equals("applications") ? Application.class : Program.class, id);
        if (resource == null) {
            return null;
        }

        ResourceRepresentation representation = dozerBeanMapper.map(resource, resourceType.equals("applications") ? ApplicationRepresentation.class : ProgramRepresentation.class);

        List<Comment> comments = commentService.getVisibleComments(resource, currentUser);
        representation.setComments(Lists.<CommentRepresentation>newArrayListWithExpectedSize(comments.size()));
        for(Comment comment : comments) {
            representation.getComments().add(dozerBeanMapper.map(comment, CommentRepresentation.class));
        }

        List<PrismAction> permittedActions = actionService.getPermittedActions(resource, currentUser);
        representation.setActions(permittedActions);
        return representation;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ResourceListRowRepresentation> getResources(@PathVariable String resourceType, @RequestParam Integer page, @RequestParam(value = "per_page") Integer perPage) {
        List<ResourceConsoleListRowDTO> consoleListBlock = resourceService.getConsoleListBlock(resourceType.equals("applications") ? Application.class : Program.class, page, perPage);
        List<ResourceListRowRepresentation> representations = Lists.newArrayList();
        for (ResourceConsoleListRowDTO appDTO : consoleListBlock) {
            ResourceListRowRepresentation representation = dozerBeanMapper.map(appDTO, ResourceListRowRepresentation.class);
            representation.setResourceType(resourceType.equals("applications") ? "APPLICATION" : "PROGRAM");
            representations.add(representation);
        }
        return representations;
    }

}
