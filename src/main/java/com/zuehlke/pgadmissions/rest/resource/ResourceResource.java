package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
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
import java.util.Set;

@RestController
@RequestMapping(value = { "api/{resourceType}" })
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

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Transactional
    public ResourceRepresentation getResource(@PathVariable Integer id, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        User currentUser = userService.getCurrentUser();
        ResourceDynamic resource = entityService.getById(resourceDescriptor.getType(), id);
        if (resource == null) {
            return null;
        }

        // create main representation
        ResourceRepresentation representation = dozerBeanMapper.map(resource, resourceDescriptor.getRepresentationType());

        // set visible comments
        List<Comment> comments = commentService.getVisibleComments(resource, currentUser);
        representation.setComments(Lists.<CommentRepresentation> newArrayListWithExpectedSize(comments.size()));
        for (Comment comment : comments) {
            representation.getComments().add(dozerBeanMapper.map(comment, CommentRepresentation.class));
        }

        // set list of available actions
        List<PrismAction> permittedActions = actionService.getPermittedActions(resource, currentUser);
        representation.setActions(permittedActions);

        // set list of user to roles mappings
        List<User> users = roleService.getUsers(resource);
        List<ResourceRepresentation.UserRolesRepresentation> userRolesRepresentations = Lists.newArrayListWithCapacity(users.size());
        for (User user : users) {
            List<PrismRole> availableRoles = roleService.getRoles(resourceDescriptor.getType());
            Set<PrismRole> roles = Sets.newHashSet(roleService.getRoles(resource, user));
            List<ResourceRepresentation.RoleRepresentation> userRoles = Lists.newArrayListWithCapacity(availableRoles.size());
            for (PrismRole availableRole : availableRoles) {
                userRoles.add(new ResourceRepresentation.RoleRepresentation(availableRole, roles.contains(availableRole)));
            }
            ResourceRepresentation.UserRolesRepresentation userRolesRepresentation = dozerBeanMapper.map(user,
                    ResourceRepresentation.UserRolesRepresentation.class);
            userRolesRepresentation.setRoles(userRoles);
            userRolesRepresentations.add(userRolesRepresentation);
        }
        representation.setUsers(userRolesRepresentations);
        return representation;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ResourceListRowRepresentation> getResources(@RequestParam Integer page, @RequestParam(value = "per_page") Integer perPage,
            @ModelAttribute ResourceDescriptor resourceDescriptor) {
        List<ResourceConsoleListRowDTO> consoleListBlock = resourceService.getConsoleListBlock(resourceDescriptor.getType(), page, perPage);
        List<ResourceListRowRepresentation> representations = Lists.newArrayList();
        for (ResourceConsoleListRowDTO appDTO : consoleListBlock) {
            ResourceListRowRepresentation representation = dozerBeanMapper.map(appDTO, ResourceListRowRepresentation.class);
            representation.setResourceType(resourceDescriptor.getResourceType());
            representations.add(representation);
        }
        return representations;
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles", method = RequestMethod.PUT)
    public void changeRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody List<ResourceRepresentation.RoleRepresentation> roles) {
        ResourceDynamic resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);

        roleService.updateRoles(resource, user, roles);
    }

    @RequestMapping(value = "{resourceId}/users", method = RequestMethod.POST)
    public void addUserToResource(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody ResourceRepresentation.UserRolesRepresentation userRolesRepresentation) {
        ResourceDynamic resource = entityService.getById(resourceDescriptor.getType(), resourceId);

        userService.getOrCreateUserWithRoles(userRolesRepresentation.getFirstName(), userRolesRepresentation.getLastName(), userRolesRepresentation.getEmail(),
                resource, userRolesRepresentation.getRoles());
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceType) {
        if ("applications".equals(resourceType)) {
            return new ResourceDescriptor(Application.class, ApplicationRepresentation.class, "APPLICATION");
        } else if ("programs".equals(resourceType)) {
            return new ResourceDescriptor(Program.class, ProgramRepresentation.class, "PROGRAM");
        }
        throw new ResourceNotFoundException("Unknown resource type '" + resourceType + "'.");
    }

    private static class ResourceDescriptor {

        private Class<? extends ResourceDynamic> type;

        private Class<? extends ResourceRepresentation> representationType;

        private String resourceType;

        private ResourceDescriptor(Class<? extends ResourceDynamic> type, Class<? extends ResourceRepresentation> representationType, String resourceType) {
            this.type = type;
            this.representationType = representationType;
            this.resourceType = resourceType;
        }

        public Class<? extends ResourceDynamic> getType() {
            return type;
        }

        public Class<? extends ResourceRepresentation> getRepresentationType() {
            return representationType;
        }

        public String getResourceType() {
            return resourceType;
        }
    }

}
