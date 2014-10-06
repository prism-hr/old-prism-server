package com.zuehlke.pgadmissions.rest.resource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.ActionDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceUserRolesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ProjectExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.SystemExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationExtendedRepresentation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping(value = {"api/{resourceScope:applications|projects|programs|institutions|systems}"})
public class ResourceResource {

    private static final Logger logger = LoggerFactory.getLogger(ResourceResource.class);

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

    @Autowired
    private ObjectMapper objectMapper;

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
            CommentRepresentation commentRepresentation = dozerBeanMapper.map(comment, CommentRepresentation.class);
            commentRepresentation.setTransitionComment(comment.isTransitionComment());
            representation.getComments().add(commentRepresentation);
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
        List<ResourceUserRolesRepresentation> userRolesRepresentations = Lists.newArrayListWithCapacity(users.size());
        for (User user : users) {
            UserRepresentation userRepresentation = dozerBeanMapper.map(user, UserRepresentation.class);
            Set<PrismRole> roles = Sets.newHashSet(roleService.getUserRoles(resource, user));
            userRolesRepresentations.add(new ResourceUserRolesRepresentation(userRepresentation, roles));
        }
        representation.setUsers(userRolesRepresentations);
        MethodUtils.invokeMethod(this, "enrich" + resource.getClass().getSimpleName() + "Representation", new Object[]{resource, representation});
        return representation;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ResourceListRowRepresentation> getResources(@ModelAttribute ResourceDescriptor resourceDescriptor,
                                                            @RequestParam(required = false) String filter, @RequestParam(required = false) String lastSequenceIdentifier) throws IllegalAccessException,
            NoSuchMethodException, InvocationTargetException, IOException {
        ResourceListFilterDTO filterDTO = filter != null ? objectMapper.readValue(filter, ResourceListFilterDTO.class) : null;
        List<ResourceListRowRepresentation> representations = Lists.newArrayList();
        try {
            DateTime baseline = new DateTime().minusDays(1);
            PrismScope resourceScope = resourceDescriptor.getResourceScope();
            List<ResourceConsoleListRowDTO> rowDTOs = resourceService.getResourceConsoleList(resourceScope, filterDTO, lastSequenceIdentifier);
            for (ResourceConsoleListRowDTO rowDTO : rowDTOs) {
                ResourceListRowRepresentation representation = dozerBeanMapper.map(rowDTO, ResourceListRowRepresentation.class);
                User currentUser = userService.getCurrentUser();

                representation.setResourceScope(resourceDescriptor.getResourceScope());
                representation.setActions(actionService.getPermittedActions(rowDTO.getSystemId(), rowDTO.getInstitutionId(), rowDTO.getProgramId(),
                        rowDTO.getProjectId(), rowDTO.getApplicationId(), rowDTO.getStateId(), currentUser));
                representation.setId((Integer) PropertyUtils.getSimpleProperty(rowDTO, resourceScope.getLowerCaseName() + "Id"));

                resourceService.filterResourceListData(representation, currentUser);

                representation.setRaisesUpdateFlag(rowDTO.getUpdatedTimestamp().isAfter(baseline));
                representations.add(representation);
            }
            return representations;
        } catch (DeduplicationException e) {
            logger.error("Unable to save default filter", e);
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ActionOutcomeRepresentation createResource(@RequestBody ActionDTO actionDTO, @RequestHeader(value = "referer", required = false) String referrer) throws WorkflowEngineException {
        if (actionDTO.getActionId().getActionCategory() != PrismActionCategory.CREATE_RESOURCE) {
            throw new Error(actionDTO.getActionId().name() + " is not a creation action.");
        }

        User user = userService.getCurrentUser();
        Object newResourceDTO = actionDTO.getOperativeResourceDTO();
        Action action = actionService.getById(actionDTO.getActionId());

        try {
            ActionOutcomeDTO actionOutcome = resourceService.createResource(user, action, newResourceDTO, referrer);
            return dozerBeanMapper.map(actionOutcome, ActionOutcomeRepresentation.class);
        } catch (Exception e) {
            logger.error("Unable to create resource", e);
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles", method = RequestMethod.POST)
    public void addUserRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor,
                            @RequestBody Map<String, PrismRole> body) throws WorkflowEngineException {
        PrismRole role = body.get("role");
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);

        try {
            roleService.updateUserRole(resource, user, role, PrismRoleTransitionType.CREATE);
            // TODO: return validation error if workflow engine exception is thrown.
        } catch (DeduplicationException e) {
            logger.error("Unable to edit user role", e);
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles/{role}", method = RequestMethod.DELETE)
    public void deleteUserRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @PathVariable PrismRole role, @ModelAttribute ResourceDescriptor resourceDescriptor) throws WorkflowEngineException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);

        try {
            roleService.updateUserRole(resource, user, role, PrismRoleTransitionType.DELETE);
            // TODO: return validation error if workflow engine exception is thrown.
        } catch (DeduplicationException e) {
            logger.error("Unable to edit user role", e);
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "{resourceId}/users", method = RequestMethod.POST)
    public UserRepresentation addUser(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
                                      @RequestBody ResourceUserRolesRepresentation userRolesRepresentation) throws WorkflowEngineException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        UserRepresentation newUser = userRolesRepresentation.getUser();

        try {
            User user = userService.getOrCreateUserWithRoles(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail(), resource, userRolesRepresentation.getRoles());
            return dozerBeanMapper.map(user, UserRepresentation.class);
            // TODO: return validation error if workflow engine exception is thrown.
        } catch (DeduplicationException e) {
            logger.error("Unable to add new user role", e);
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = "{resourceId}/users/{userId}", method = RequestMethod.DELETE)
    public void removeUser(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor) throws WorkflowEngineException {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);

        // TODO implement remove user
    }

    @RequestMapping(value = "/{resourceId}/comments", method = RequestMethod.POST)
    public ActionOutcomeRepresentation performAction(@PathVariable Integer resourceId, @Valid @RequestBody CommentDTO commentDTO) {
        try {
            ActionOutcomeDTO actionOutcome = resourceService.performAction(resourceId, commentDTO);
            return dozerBeanMapper.map(actionOutcome, ActionOutcomeRepresentation.class);
        } catch (Exception e) {
            PrismAction actionId = commentDTO.getAction();
            logger.error("Could not perform action " + actionId + " on " + actionId.getScope().getLowerCaseName() + " id " + resourceId.toString(), e);
            throw new ResourceNotFoundException();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void enrichApplicationRepresentation(Application application, ApplicationExtendedRepresentation applicationRepresentation) {
        List<User> interested = userService.getUsersInterestedInApplication(application);
        List<User> potentiallyInterested = userService.getUsersPotentiallyInterestedInApplication(application, interested);
        List<UserRepresentation> interestedRepresentations = Lists.newArrayListWithCapacity(interested.size());
        List<UserRepresentation> potentiallyInterestedRepresentations = Lists.newArrayListWithCapacity(potentiallyInterested.size());

        for (User user : interested) {
            interestedRepresentations.add(dozerBeanMapper.map(user, UserRepresentation.class));
        }

        for (User user : potentiallyInterested) {
            potentiallyInterestedRepresentations.add(dozerBeanMapper.map(user, UserRepresentation.class));
        }

        applicationRepresentation.setProgramTitle(application.getProgram().getTitle());
        applicationRepresentation.setProjectTitle(application.getProject() != null ? application.getProject().getTitle() : null);

        applicationRepresentation.setUsersInterestedInApplication(interestedRepresentations);
        applicationRepresentation.setUsersPotentiallyInterestedInApplication(potentiallyInterestedRepresentations);

        applicationRepresentation.setAppointmentTimeslots(commentService.getAppointmentTimeslots(application));
        applicationRepresentation.setAppointmentPreferences(commentService.getAppointmentPreferences(application));

        applicationRepresentation.setOfferRecommendation(commentService.getOfferRecommendation(application));
        applicationRepresentation.setAssignedSupervisors(commentService.getApplicationSupervisors(application));
        List<ProgramStudyOption> enabledProgramStudyOptions = programService.getEnabledProgramStudyOptions(application.getProgram());
        List<PrismStudyOption> availableStudyOptions = Lists.newArrayListWithCapacity(enabledProgramStudyOptions.size());
        for (ProgramStudyOption studyOption : enabledProgramStudyOptions) {
            availableStudyOptions.add(studyOption.getStudyOption().getPrismStudyOption());
        }
        applicationRepresentation.setAvailableStudyOptions(availableStudyOptions);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void enrichProjectRepresentation(Project program, ProjectExtendedRepresentation programRepresentation) {
    }

    @SuppressWarnings("UnusedDeclaration")
    public void enrichProgramRepresentation(Program program, ProgramExtendedRepresentation programRepresentation) {
    }

    @SuppressWarnings("UnusedDeclaration")
    public void enrichInstitutionRepresentation(Institution institution, InstitutionExtendedRepresentation institutionRepresentation) {
    }

    @SuppressWarnings("UnusedDeclaration")
    public void enrichSystemRepresentation(System system, SystemExtendedRepresentation systemRepresentation) {
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
        } else if ("systems".equals(resourceScope)) {
            return new ResourceDescriptor(com.zuehlke.pgadmissions.domain.System.class, SystemExtendedRepresentation.class, PrismScope.SYSTEM);
        }
        logger.error("Unknown resource scope " + resourceScope);
        throw new ResourceNotFoundException();
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
