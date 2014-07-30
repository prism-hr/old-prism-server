package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;
import java.util.Set;

import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.representation.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.application.ApplicationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.application.ProgramRepresentation;
import com.zuehlke.pgadmissions.rest.representation.application.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping(value = { "api/{resourceScope}" })
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
    private Mapper dozerBeanMapper;

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Transactional
    public AbstractResourceRepresentation getResource(@PathVariable Integer id, @ModelAttribute ResourceDescriptor resourceDescriptor) {
        User currentUser = userService.getCurrentUser();
        Resource resource = entityService.getById(resourceDescriptor.getType(), id);
        if (resource == null) {
            return null;
        }

        // create main representation
        AbstractResourceRepresentation representation = dozerBeanMapper.map(resource, resourceDescriptor.getRepresentationType());

        // set visible comments
        List<Comment> comments = commentService.getVisibleComments(resource, currentUser);
        representation.setComments(Lists.<CommentRepresentation> newArrayListWithExpectedSize(comments.size()));
        for (Comment comment : comments) {
            representation.getComments().add(dozerBeanMapper.map(comment, CommentRepresentation.class));
        }

        // set list of available actions
        List<PrismAction> permittedActions = actionService.getPermittedActions(resource, currentUser);
        representation.setActions(permittedActions);

        // set list of available action enhancements (viewing and editing permissions)
        List<PrismActionEnhancement> permittedActionEnhancements = actionService.getPermittedActionEnhancements(resource, currentUser);
        representation.setActionEnhancements(permittedActionEnhancements);

        // set list of user to roles mappings
        List<User> users = roleService.getUsers(resource);
        List<AbstractResourceRepresentation.UserRolesRepresentation> userRolesRepresentations = Lists.newArrayListWithCapacity(users.size());
        for (User user : users) {
            List<PrismRole> availableRoles = roleService.getRoles(resourceDescriptor.getType());
            Set<PrismRole> roles = Sets.newHashSet(roleService.getUserRoles(resource, user));
            List<AbstractResourceRepresentation.RoleRepresentation> userRoles = Lists.newArrayListWithCapacity(availableRoles.size());
            for (PrismRole availableRole : availableRoles) {
                userRoles.add(new AbstractResourceRepresentation.RoleRepresentation(availableRole, roles.contains(availableRole)));
            }
            AbstractResourceRepresentation.UserRolesRepresentation userRolesRepresentation = dozerBeanMapper.map(user,
                    AbstractResourceRepresentation.UserRolesRepresentation.class);
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
            representation.setResourceScope(resourceDescriptor.getResourceScope());
            representations.add(representation);
        }
        return representations;
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles", method = RequestMethod.PUT)
    public void changeRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody List<AbstractResourceRepresentation.RoleRepresentation> roles) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);

        roleService.updateRoles(resource, user, roles);
    }

    @RequestMapping(value = "{resourceId}/users", method = RequestMethod.POST)
    public void addUserToResource(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody AbstractResourceRepresentation.UserRolesRepresentation userRolesRepresentation) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);

        userService.getOrCreateUserWithRoles(userRolesRepresentation.getFirstName(), userRolesRepresentation.getLastName(), userRolesRepresentation.getEmail(),
                resource, userRolesRepresentation.getRoles());
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        if ("applications".equals(resourceScope)) {
            return new ResourceDescriptor(Application.class, ApplicationRepresentation.class, "APPLICATION");
        } else if ("programs".equals(resourceScope)) {
            return new ResourceDescriptor(Program.class, ProgramRepresentation.class, "PROGRAM");
        }
        throw new ResourceNotFoundException("Unknown resource type '" + resourceScope + "'.");
    }

    private static class ResourceDescriptor {

        private Class<? extends Resource> type;

        private Class<? extends AbstractResourceRepresentation> representationType;

        private String resourceScope;

        private ResourceDescriptor(Class<? extends Resource> type, Class<? extends AbstractResourceRepresentation> representationType, String resourceScope) {
            this.type = type;
            this.representationType = representationType;
            this.resourceScope = resourceScope;
        }

        public Class<? extends Resource> getType() {
            return type;
        }

        public Class<? extends AbstractResourceRepresentation> getRepresentationType() {
            return representationType;
        }

        public String getResourceScope() {
            return resourceScope;
        }
    }

}
