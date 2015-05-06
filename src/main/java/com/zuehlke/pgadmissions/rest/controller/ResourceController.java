package com.zuehlke.pgadmissions.rest.controller;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.DELETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.datatable.DataTable;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.ResourceListActionDTO;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.rest.ResourceDescriptor;
import com.zuehlke.pgadmissions.rest.RestApiUtils;
import com.zuehlke.pgadmissions.rest.dto.ActionDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.rest.representation.ActionOutcomeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceUserRolesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.SimpleResourceRepresentation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;
import com.zuehlke.pgadmissions.workflow.resource.representation.ResourceRepresentationEnricher;

@RestController
@RequestMapping("api/{resourceScope:applications|projects|programs|institutions|systems}")
public class ResourceController {

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private CommentService commentService;

    @Inject
    private UserService userService;

    @Inject
    private ActionService actionService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private Mapper mapper;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ApplicationContext applicationContext;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ActionOutcomeRepresentation createResource(@RequestBody @Valid ActionDTO actionDTO) throws Exception {
        ResourceDTO newResource = actionDTO.getNewResource();

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(newResource, "userDTO");
        ValidationUtils.rejectIfEmpty(errors, "resourceId", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "resourceScope", "notNull");
        if (errors.hasErrors()) {
            throw new PrismValidationException("Could not create resource", errors);
        }

        if (!actionDTO.getActionId().getActionCategory().equals(PrismActionCategory.CREATE_RESOURCE)) {
            throw new Error();
        }

        User user = userService.getCurrentUser();
        Action action = actionService.getById(actionDTO.getActionId());

        ActionOutcomeDTO actionOutcome = resourceService.create(user, action, newResource, actionDTO.getReferer(),
                actionDTO.getWorkflowPropertyConfigurationVersion());
        return mapper.map(actionOutcome, ActionOutcomeRepresentation.class);
    }

    @Transactional
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public AbstractResourceRepresentation getResource(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor)
            throws Exception {
        User currentUser = userService.getCurrentUser();
        Resource resource = loadResource(resourceId, resourceDescriptor);

        AbstractResourceRepresentation representation = mapper.map(resource, resourceDescriptor.getRepresentationType());
        representation.setTimeline(commentService.getComments(resource, currentUser));

        Set<ActionRepresentation> permittedActions = actionService.getPermittedActions(resource, currentUser);
        if (permittedActions.isEmpty()) {
            Action action = actionService.getViewEditAction(resource);
            throw new WorkflowPermissionException(resource, action);
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
            UserRepresentation userRepresentation = mapper.map(user, UserRepresentation.class);
            Set<PrismRole> roles = Sets.newHashSet(roleService.getRolesForResource(resource, user));
            userRolesRepresentations.add(new ResourceUserRolesRepresentation(userRepresentation, roles));
        }

        representation.setUsers(userRolesRepresentations);
        representation.setWorkflowPropertyConfigurations(resourceService.getWorkflowPropertyConfigurations(resource));

        PrismScope resourceScope = resource.getResourceScope();
        Class<? extends ResourceRepresentationEnricher> resourceRepresentationEncricher = resourceScope.getResourceRepresentationEnricher();
        if (resourceRepresentationEncricher != null) {
            applicationContext.getBean(resourceRepresentationEncricher).enrich(resourceScope, resourceId, representation);
        }

        return representation;
    }

    @RequestMapping(value = "/{resourceId}/displayProperties", method = RequestMethod.GET)
    @PreAuthorize("permitAll")
    public Map<PrismDisplayPropertyDefinition, String> getDisplayProperties(
            @PathVariable Integer resourceId,
            @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestParam PrismScope propertiesScope) throws Exception {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        return resourceService.getDisplayProperties(resource, propertiesScope);
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public List<ResourceListRowRepresentation> getResources(@ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestParam(required = false) String filter, @RequestParam(required = false) String lastSequenceIdentifier) throws Exception {
        ResourceListFilterDTO filterDTO = filter != null ? objectMapper.readValue(filter, ResourceListFilterDTO.class) : null;
        List<ResourceListRowRepresentation> representations = Lists.newArrayList();
        DateTime baseline = new DateTime().minusDays(1);
        PrismScope resourceScope = resourceDescriptor.getResourceScope();

        for (ResourceListRowDTO rowDTO : resourceService.getResourceList(resourceScope, filterDTO, lastSequenceIdentifier)) {
            ResourceListRowRepresentation representation = mapper.map(rowDTO, ResourceListRowRepresentation.class);
            representation.setResourceScope(resourceScope);
            representation.setId((Integer) PropertyUtils.getSimpleProperty(rowDTO, resourceScope.getLowerCamelName() + "Id"));

            Set<ActionRepresentation> actionRepresentations = Sets.newLinkedHashSet();
            for (ResourceListActionDTO actionDTO : rowDTO.getActions()) {
                actionRepresentations.add(mapper.map(actionDTO, ActionRepresentation.class));
            }
            representation.setActions(actionRepresentations);

            for (String scopeName : new String[] { "institution", "program", "project" }) {
                Integer id = (Integer) PropertyUtils.getSimpleProperty(rowDTO, scopeName + "Id");
                if (id != null) {
                    String title = (String) PropertyUtils.getSimpleProperty(rowDTO, scopeName + "Title");
                    PropertyUtils.setSimpleProperty(representation, scopeName, new SimpleResourceRepresentation(id, title));
                }
            }

            representation.setRaisesUpdateFlag(rowDTO.getUpdatedTimestamp().isAfter(baseline));
            representation.setSecondaryStateGroups(stateService.getSecondaryResourceStateGroups(resourceScope,
                    (Integer) PrismReflectionUtils.getProperty(rowDTO, resourceScope.getLowerCamelName() + "Id")));

            representations.add(representation);
        }

        return representations;
    }

    @RequestMapping(method = RequestMethod.GET, params = "type=report")
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
    public ResourceSummaryRepresentation getSummary(@ModelAttribute ResourceDescriptor resourceDescriptor, @PathVariable Integer resourceId) throws Exception {
        PrismScope resourceScope = resourceDescriptor.getResourceScope();
        if (Arrays.asList(SYSTEM, APPLICATION).contains(resourceScope)) {
            throw new UnsupportedOperationException("Resource summary can only be generated for institutions, programs, projects");
        }
        return resourceService.getResourceSummary(resourceScope, resourceId);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public void addUserRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody Map<String, PrismRole> body) throws Exception {
        PrismRole role = body.get("role");
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.assignUserRoles(resource, user, CREATE, role);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}/roles/{role}", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public void deleteUserRole(@PathVariable Integer resourceId, @PathVariable Integer userId, @PathVariable PrismRole role,
            @ModelAttribute ResourceDescriptor resourceDescriptor) throws Exception {
        Resource resource = loadResource(resourceId, resourceDescriptor);
        User user = userService.getById(userId);
        roleService.assignUserRoles(resource, user, DELETE, role);
    }

    @RequestMapping(value = "{resourceId}/users", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public UserRepresentation addUser(@PathVariable Integer resourceId, @ModelAttribute ResourceDescriptor resourceDescriptor,
            @RequestBody ResourceUserRolesRepresentation userRolesRepresentation) throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        UserRepresentation newUser = userRolesRepresentation.getUser();

        User user = userService.getOrCreateUserWithRoles(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail(), resource,
                userRolesRepresentation.getRoles());
        return mapper.map(user, UserRepresentation.class);
    }

    @RequestMapping(value = "{resourceId}/users/{userId}", method = RequestMethod.DELETE)
    @PreAuthorize("isAuthenticated()")
    public void removeUser(@PathVariable Integer resourceId, @PathVariable Integer userId, @ModelAttribute ResourceDescriptor resourceDescriptor)
            throws Exception {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        User user = userService.getById(userId);
        roleService.deleteUserRoles(resource, user);
    }

    @RequestMapping(value = "/{resourceId}/comments", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ActionOutcomeRepresentation executeAction(@PathVariable Integer resourceId, @Valid @RequestBody CommentDTO commentDTO) throws Exception {
        ActionOutcomeDTO actionOutcome = resourceService.executeAction(resourceId, commentDTO);
        return mapper.map(actionOutcome, ActionOutcomeRepresentation.class);
    }

    @ModelAttribute
    private ResourceDescriptor getResourceDescriptor(@PathVariable String resourceScope) {
        return RestApiUtils.getResourceDescriptor(resourceScope);
    }

    private Resource loadResource(Integer resourceId, ResourceDescriptor resourceDescriptor) {
        Resource resource = entityService.getById(resourceDescriptor.getType(), resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found");
        }
        return resource;
    }

}