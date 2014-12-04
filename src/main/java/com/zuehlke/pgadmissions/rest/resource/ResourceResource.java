package com.zuehlke.pgadmissions.rest.resource;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.datatable.DataTable;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.ActionDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceUserRolesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.SimpleResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationExtendedRepresentation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@RestController
@RequestMapping("api/{resourceScope:applications|projects|programs|institutions|systems}")
@PreAuthorize("isAuthenticated()")
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
    private CustomizationService customizationService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationResource applicationResource;

    @Autowired
    private Mapper beanMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(method = RequestMethod.POST)
    public ActionOutcomeRepresentation createResource(@RequestBody ActionDTO actionDTO, @RequestHeader(value = "referer", required = false) String referrer)
            throws Exception {
        if (!actionDTO.getActionId().getActionCategory().equals(PrismActionCategory.CREATE_RESOURCE)) {
            throw new Error();
        }

        User user = userService.getCurrentUser();
        Object newResourceDTO = actionDTO.getOperativeResourceDTO();
        Action action = actionService.getById(actionDTO.getActionId());

        ActionOutcomeDTO actionOutcome = resourceService.createResource(user, action, newResourceDTO, referrer,
                actionDTO.getWorkflowPropertyConfigurationVersion());
        return beanMapper.map(actionOutcome, ActionOutcomeRepresentation.class);
    }

    @Transactional
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET)
    public AbstractResourceRepresentation getResource(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, AccessDeniedException {
        User currentUser = userService.getCurrentUser();
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        if (resource == null) {
            return null;
        }

        AbstractResourceRepresentation representation = beanMapper.map(resource, resourceDescriptor.getRepresentationType());
        representation.setTimeline(commentService.getComments(resource, currentUser));

        Set<ActionRepresentation> permittedActions = actionService.getPermittedActions(resource, currentUser);
        if (permittedActions.isEmpty()) {
            Action viewEditAction = actionService.getViewEditAction(resource);
            actionService.throwWorkflowPermissionException(resource, viewEditAction);
        }
        representation.setActions(permittedActions);
        representation.setRecommendedNextStates(stateService.getRecommendedNextStates(resource));
        List<PrismState> secondaryStates = Lists.newArrayListWithCapacity(resource.getResourceStates().size());
        for (ResourceState resourceState : resource.getResourceStates()) {
            if (!resourceState.getPrimaryState()) {
                secondaryStates.add(resourceState.getState().getId());
            }
        }

        representation.setSecondaryStates(stateService.getSecondaryResourceStateGroups(resource));

        List<User> users = userService.getResourceUsers(resource);
        List<ResourceUserRolesRepresentation> userRolesRepresentations = Lists.newArrayListWithCapacity(users.size());
        for (User user : users) {
            UserRepresentation userRepresentation = beanMapper.map(user, UserRepresentation.class);
            Set<PrismRole> roles = Sets.newHashSet(roleService.getUserRoles(resource, user));
            userRolesRepresentations.add(new ResourceUserRolesRepresentation(userRepresentation, roles));
        }
        representation.setUsers(userRolesRepresentations);

        representation.setWorkflowPropertyConfigurations(resourceService.getWorkflowPropertyConfigurations(resource));

        PrismScope resourceScope = resource.getResourceScope();
        switch (resourceScope) {
        case APPLICATION:
            applicationResource.enrichApplicationRepresentation((Application) resource, (ApplicationExtendedRepresentation) representation);
            break;
        case PROJECT:
        case PROGRAM:
        case INSTITUTION:
            ReflectionUtils.setProperty(representation, "resourceSummary", resourceService.getResourceSummary(resourceScope.getResourceClass(), resourceId));
            break;
        default:
            break;
        }

        return representation;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ResourceListRowRepresentation> getResources(@ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestParam(required = false) String filter, @RequestParam(required = false) String lastSequenceIdentifier) throws Exception {
        User currentUser = userService.getCurrentUser();
        ResourceListFilterDTO filterDTO = filter != null ? objectMapper.readValue(filter, ResourceListFilterDTO.class) : null;
        List<ResourceListRowRepresentation> representations = Lists.newArrayList();
        DateTime baseline = new DateTime().minusDays(1);
        PrismScope resourceScope = resourceDescriptor.getResourceScope();
        HashMultimap<PrismState, PrismAction> creationActions = actionService.getCreateResourceActionsByState(resourceScope);

        for (ResourceConsoleListRowDTO rowDTO : resourceService.getResourceList(resourceScope, filterDTO, lastSequenceIdentifier)) {
            ResourceListRowRepresentation representation = beanMapper.map(rowDTO, ResourceListRowRepresentation.class);
            representation.setResourceScope(resourceScope);
            representation.setId((Integer) PropertyUtils.getSimpleProperty(rowDTO, resourceScope.getLowerCaseName() + "Id"));

            addActions(currentUser, resourceScope, creationActions, rowDTO, representation);

            for (String scopeName : new String[] { "institution", "program", "project" }) {
                Integer id = (Integer) PropertyUtils.getSimpleProperty(rowDTO, scopeName + "Id");
                if (id != null) {
                    String title = (String) PropertyUtils.getSimpleProperty(rowDTO, scopeName + "Title");
                    PropertyUtils.setSimpleProperty(representation, scopeName, new SimpleResourceRepresentation(id, title));
                }
            }

            resourceService.filterResourceListData(representation, currentUser);

            representation.setRaisesUpdateFlag(rowDTO.getUpdatedTimestamp().isAfter(baseline));
            representation.setSecondaryStateGroups(stateService.getSecondaryResourceStateGroups(resourceScope,
                    (Integer) ReflectionUtils.getProperty(rowDTO, resourceScope.getLowerCaseName() + "Id")));

            representations.add(representation);
        }

        return representations;
    }

    @RequestMapping(method = RequestMethod.GET, params = "type=report")
    public void getReport(@ModelAttribute ResourceDescriptor resourceDescriptor, @RequestParam(required = false) String filter, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (resourceDescriptor.getResourceScope() != PrismScope.APPLICATION) {
            throw new UnsupportedOperationException("Report can only be generated for applications");
        }
        ResourceListFilterDTO filterDTO = filter != null ? objectMapper.readValue(filter, ResourceListFilterDTO.class) : null;
        DataTable reportTable = applicationService.getApplicationReport(filterDTO);
        DataSourceRequest dataSourceRequest = new DataSourceRequest(request);
        DataSourceHelper.setServletResponse(reportTable, dataSourceRequest, response);
        String fileName = response.getHeader("Content-Disposition").replace("attachment; filename=", "");
        response.setHeader("file-name", fileName);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{resourceScope:projects|programs|institutions}/{resourceId}", params = "type=summary")
    public ResourceSummaryRepresentation getSummary(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId) {
        PrismScope resourceScope = resourceDescriptor.getResourceScope();
        if (Arrays.asList(PrismScope.SYSTEM, PrismScope.APPLICATION).contains(resourceScope)) {
            throw new UnsupportedOperationException("Resource summary can only be generated for institutions, programs, projects");
        }
        return resourceService.getResourceSummary(resourceScope.getResourceClass(), resourceId);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles", method = RequestMethod.POST)
    public void addUserRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody Map<String, PrismRole> body) throws Exception {
        PrismRole role = body.get("role");
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.updateUserRole(resource, user, PrismRoleTransitionType.CREATE, role);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles/{role}", method = RequestMethod.DELETE)
    public void deleteUserRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @PathVariable PrismRole role,
            @ModelAttribute ResourceDescriptor resourceDescriptor) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.updateUserRole(resource, user, PrismRoleTransitionType.DELETE, role);
    }

    @RequestMapping(value = "{resourceId}/users", method = RequestMethod.POST)
    public UserRepresentation addUser(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody ResourceUserRolesRepresentation userRolesRepresentation) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        UserRepresentation newUser = userRolesRepresentation.getUser();

        User user = userService.getOrCreateUserWithRoles(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail(), resource.getLocale(), resource,
                userRolesRepresentation.getRoles());
        return beanMapper.map(user, UserRepresentation.class);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}", method = RequestMethod.DELETE)
    public void removeUser(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor)
            throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.deleteUserRoles(resource, user);
    }

    @RequestMapping(value = "/{resourceId}/comments", method = RequestMethod.POST)
    public ActionOutcomeRepresentation executeAction(@PathVariable Integer resourceId, @Valid @RequestBody CommentDTO commentDTO) throws Exception {
        ActionOutcomeDTO actionOutcome = resourceService.executeAction(resourceId, commentDTO);
        return beanMapper.map(actionOutcome, ActionOutcomeRepresentation.class);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestApiUtils.getResourceDescriptor(resourceScope);
    }

    private void addActions(User currentUser, PrismScope resourceScope, HashMultimap<PrismState, PrismAction> creationActions,
            ResourceConsoleListRowDTO rowDTO, ResourceListRowRepresentation representation) {
        representation.setActions(actionService.getPermittedActions(resourceScope, rowDTO.getSystemId(), rowDTO.getInstitutionId(), rowDTO.getProgramId(),
                rowDTO.getProjectId(), rowDTO.getApplicationId(), currentUser));
        for (PrismAction creationAction : creationActions.get(rowDTO.getStateId())) {
            representation.addAction(new ActionRepresentation().withId(creationAction).withRaisesUrgentFlag(false));
        }
    }

}
