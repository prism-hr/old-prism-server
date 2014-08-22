package com.zuehlke.pgadmissions.rest.resource;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.MethodUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProjectExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationExtendedRepresentation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
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
    private RoleService roleService;

    @Autowired
    private StateService stateService;

    @Autowired
    private Mapper dozerBeanMapper;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Transactional
    public AbstractResourceRepresentation getResource(@PathVariable Integer id, @ModelAttribute ResourceDescriptor resourceDescriptor)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, AccessDeniedException {
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
        if (permittedActions.isEmpty()) {
            Action viewEditAction = actionService.getViewEditAction(resource);
            actionService.throwWorkflowPermissionException(viewEditAction, resource);
        }
        representation.setActions(permittedActions);

        Optional<PrismAction> completeAction = Iterables.tryFind(
                permittedActions,
                Predicates.compose(Predicates.containsPattern("^(APPLICATION|INSTITUTION)_COMPLETE_|APPLICATION_MOVE_TO_DIFFERENT_STAGE"),
                        Functions.toStringFunction()));
        if (completeAction.isPresent()) {
            representation.setNextStates(stateService.getAvailableNextStates(resource, completeAction.get()));
        }

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
        MethodUtils.invokeMethod(this, "enrich" + resource.getClass().getSimpleName() + "Representation", new Object[] { resource, representation });
        return representation;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ResourceListRowRepresentation> getResources(@ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestParam(value = "page") Integer loadIndex) {
        List<ResourceConsoleListRowDTO> consoleListBlock = resourceService.getConsoleListBlock(resourceDescriptor.getType(), loadIndex);
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
            @RequestBody List<AbstractResourceRepresentation.RoleRepresentation> roles) throws WorkflowEngineException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);

        roleService.updateRoles(resource, user, roles);
        // TODO: return validation error if workflow engine exception is thrown.
    }

    @RequestMapping(value = "{resourceId}/users", method = RequestMethod.POST)
    public void addUserToResource(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody AbstractResourceRepresentation.UserRolesRepresentation userRolesRepresentation) throws WorkflowEngineException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);

        userService.getOrCreateUserWithRoles(userRolesRepresentation.getFirstName(), userRolesRepresentation.getLastName(), userRolesRepresentation.getEmail(),
                resource, userRolesRepresentation.getRoles());
        // TODO: return validation error if workflow engine exception is thrown.
    }

    public void enrichApplicationRepresentation(Application application, ApplicationExtendedRepresentation applicationRepresentation) {
        List<User> interested = userService.getUsersInterestedInApplication(application);
        List<User> potentiallyInterested = userService.getUsersPotentiallyInterestedInApplication(application, interested);
        List<UserExtendedRepresentation> interestedRepresentations = Lists.newArrayListWithCapacity(interested.size());
        List<UserExtendedRepresentation> potentiallyInterestedRepresentations = Lists.newArrayListWithCapacity(potentiallyInterested.size());

        for (User user : interested) {
            interestedRepresentations.add(dozerBeanMapper.map(user, UserExtendedRepresentation.class));
        }

        for (User user : potentiallyInterested) {
            potentiallyInterestedRepresentations.add(dozerBeanMapper.map(user, UserExtendedRepresentation.class));
        }

        applicationRepresentation.setUsersInterestedInApplication(interestedRepresentations);
        applicationRepresentation.setUsersPotentiallyInterestedInApplication(potentiallyInterestedRepresentations);

        applicationRepresentation.setAppointmentTimeslots(commentService.getAppointmentTimeslots(application));
        applicationRepresentation.setAppointmentPreferences(commentService.getAppointmentPreferences(application));

        applicationRepresentation.setOfferRecommendation(commentService.getOfferRecommendation(application));
        applicationRepresentation.setSupervisors(commentService.getApplicationSupervisors(application));
    }

    public void enrichProgramRepresentation(Program program, ProgramExtendedRepresentation programRepresentation) {
    }

    public void enrichInstitutionRepresentation(Institution institution, InstitutionExtendedRepresentation institutionRepresentation) {

    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        if ("applications".equals(resourceScope)) {
            return new ResourceDescriptor(Application.class, ApplicationExtendedRepresentation.class, "APPLICATION");
        } else if ("projects".equals(resourceScope)) {
            return new ResourceDescriptor(Project.class, ProjectExtendedRepresentation.class, "PROJECT");
        } else if ("programs".equals(resourceScope)) {
            return new ResourceDescriptor(Program.class, ProgramExtendedRepresentation.class, "PROGRAM");
        } else if ("institutions".equals(resourceScope)) {
            return new ResourceDescriptor(Institution.class, InstitutionExtendedRepresentation.class, "INSTITUTION");
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
