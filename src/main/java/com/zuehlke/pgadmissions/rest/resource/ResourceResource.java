package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.ActionDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProjectExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationExtendedRepresentation;
import com.zuehlke.pgadmissions.services.*;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = {"api/{resourceScope}"})
public class ResourceResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    private ProgramService programService;

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
        representation.setComments(Lists.<CommentRepresentation>newArrayListWithExpectedSize(comments.size()));
        for (Comment comment : comments) {
            representation.getComments().add(dozerBeanMapper.map(comment, CommentRepresentation.class));
        }

        // set list of available actions
        List<ActionRepresentation> permittedActions = actionService.getPermittedActions(resource, currentUser);
        if (permittedActions.isEmpty()) {
            Action viewEditAction = actionService.getViewEditAction(resource);
            actionService.throwWorkflowPermissionException(resource, viewEditAction);
        }
        representation.setActions(permittedActions);
        representation.setNextStates(stateService.getAvailableNextStates(resource, permittedActions));

        // set list of available action enhancements (viewing and editing permissions)
        List<PrismActionEnhancement> permittedActionEnhancements = actionService.getPermittedActionEnhancements(resource, currentUser);
        representation.setActionEnhancements(permittedActionEnhancements);

        // set list of user to roles mappings
        List<User> users = userService.getEnabledResourceUsers(resource);
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
        MethodUtils.invokeMethod(this, "enrich" + resource.getClass().getSimpleName() + "Representation", new Object[]{resource, representation});
        return representation;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ResourceListRowRepresentation> getResources(@ModelAttribute ResourceDescriptor resourceDescriptor,
                                                            @RequestBody(required = false) ResourceListFilterDTO filterDTO, @RequestParam(required = false) String lastSequenceIdentifier) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<ResourceListRowRepresentation> representations = Lists.newArrayList();
        try {
            PrismScope resourceScope = resourceDescriptor.getResourceScope();
            for (ResourceConsoleListRowDTO rowDTO : resourceService.getResourceConsoleList(resourceScope, filterDTO, lastSequenceIdentifier)) {
                ResourceListRowRepresentation representation = dozerBeanMapper.map(rowDTO, ResourceListRowRepresentation.class);
                User user = userService.getCurrentUser();

                representation.setResourceScope(resourceDescriptor.getResourceScope());
                representation.setActions(actionService.getPermittedActions(rowDTO.getSystemId(), rowDTO.getInstitutionId(), rowDTO.getProgramId(),
                        rowDTO.getProjectId(), rowDTO.getApplicationId(), rowDTO.getStateId(), user));
                representation.setId((Integer) PropertyUtils.getSimpleProperty(rowDTO, resourceScope.getLowerCaseName() + "Id"));
                representations.add(representation);
            }
            return representations;
        } catch (DeduplicationException e) {
            throw new ResourceNotFoundException("Error saving default filter", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ActionOutcomeRepresentation createResource(@ModelAttribute ResourceDescriptor resourceDescriptor, @RequestBody ActionDTO actionDTO,
                                                      @RequestHeader(value = "referer", required = false) String referrer) throws WorkflowEngineException {
        if (actionDTO.getActionId().getActionCategory() != PrismActionCategory.CREATE_RESOURCE) {
            throw new Error(actionDTO.getActionId().name() + " is not a creation action.");
        }
        User user = userService.getCurrentUser();
        Object newResourceDTO = actionDTO.getOperativeResourceDTO();
        Action action = actionService.getById(actionDTO.getActionId());

        try {
            ActionOutcomeDTO actionOutcome = resourceService.createResource(user, action, newResourceDTO, referrer);
            return dozerBeanMapper.map(actionOutcome, ActionOutcomeRepresentation.class);
        } catch (DeduplicationException e) {
            logger.error(e.getMessage());
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles", method = RequestMethod.PUT)
    public void changeRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor,
                           @RequestBody List<AbstractResourceRepresentation.RoleRepresentation> roles) throws WorkflowEngineException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);

        try {
            roleService.updateRoles(resource, user, roles);
            // TODO: return validation error if workflow engine exception is thrown.
        } catch (DeduplicationException e) {
            logger.error(e.getMessage());
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "{resourceId}/users", method = RequestMethod.POST)
    public void addUserToResource(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
                                  @RequestBody AbstractResourceRepresentation.UserRolesRepresentation userRolesRepresentation) throws WorkflowEngineException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);

        try {
            userService.getOrCreateUserWithRoles(userRolesRepresentation.getFirstName(), userRolesRepresentation.getLastName(),
                    userRolesRepresentation.getEmail(), resource, userRolesRepresentation.getRoles());
            // TODO: return validation error if workflow engine exception is thrown.
        } catch (DeduplicationException e) {
            logger.error(e.getMessage());
            throw new ResourceNotFoundException();
        }
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
        List<ProgramStudyOption> enabledProgramStudyOptions = programService.getEnabledProgramStudyOptions(application.getProgram());
        List<PrismStudyOption> availableStudyOptions = Lists.newArrayListWithCapacity(enabledProgramStudyOptions.size());
        for (ProgramStudyOption studyOption : enabledProgramStudyOptions) {
            availableStudyOptions.add(studyOption.getStudyOption().getPrismStudyOption());
        }
        applicationRepresentation.setAvailableStudyOptions(availableStudyOptions);
    }

    public void enrichProgramRepresentation(Program program, ProgramExtendedRepresentation programRepresentation) {
    }

    public void enrichInstitutionRepresentation(Institution institution, InstitutionExtendedRepresentation institutionRepresentation) {

    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        if ("applications".equals(resourceScope)) {
            return new ResourceDescriptor(Application.class, ApplicationExtendedRepresentation.class, PrismScope.APPLICATION);
        } else if ("projects".equals(resourceScope)) {
            return new ResourceDescriptor(Project.class, ProjectExtendedRepresentation.class, PrismScope.PROJECT);
        } else if ("programs".equals(resourceScope)) {
            return new ResourceDescriptor(Program.class, ProgramExtendedRepresentation.class, PrismScope.PROGRAM);
        } else if ("institutions".equals(resourceScope)) {
            return new ResourceDescriptor(Institution.class, InstitutionExtendedRepresentation.class, PrismScope.INSTITUTION);
        }
        throw new ResourceNotFoundException("Unknown resource type '" + resourceScope + "'.");
    }

    private static class ResourceDescriptor {

        private Class<? extends Resource> type;

        private Class<? extends AbstractResourceRepresentation> representationType;

        private PrismScope resourceScope;

        private ResourceDescriptor(Class<? extends Resource> type, Class<? extends AbstractResourceRepresentation> representationType, PrismScope resourceScope) {
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

        public PrismScope getResourceScope() {
            return resourceScope;
        }
    }

}
